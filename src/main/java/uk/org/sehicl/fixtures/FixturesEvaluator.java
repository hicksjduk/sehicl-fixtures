package uk.org.sehicl.fixtures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class FixturesEvaluator
{
    private static final Logger LOG = LoggerFactory.getLogger(FixturesEvaluator.class);
    private static File checkpointDir = new File("checkpoints");
    private static File bestDir = new File("best");

    private final FixtureSequencer sequencer = new FixtureSequencer();
    // private final BlockingQueue<Runnable> executorQueue = new ArrayBlockingQueue<>(500);
    // private final ExecutorService executor = new ThreadPoolExecutor(8, 20, 10, TimeUnit.SECONDS,
    // executorQueue);
    private int bestScore = Integer.MAX_VALUE;
    // private final SortedSet<Checkpoint> pendingTransactions = new TreeSet<>();
    private final Integer partition;

    public static void main(String[] args)
    {
        FixturesEvaluator evaluator;
        if (args.length > 0 && args[0].matches("\\d+"))
        {
            final String partition = args[0];
            checkpointDir = new File(checkpointDir, partition);
            bestDir = new File(bestDir, partition);
            evaluator = new FixturesEvaluator(Integer.parseInt(partition));
        }
        else
        {
            evaluator = new FixturesEvaluator();
        }
        checkpointDir.mkdirs();
        bestDir.mkdirs();
        evaluator.evaluate();
    }

    public FixturesEvaluator()
    {
        this(null);
    }

    public FixturesEvaluator(Integer partition)
    {
        this.partition = partition;
    }

    private void evaluate()
    {
        List<FixtureSet> sequencedFixtures = sequencer.getSequencedFixtures();
        if (partition != null)
        {
            sequencedFixtures.get(0).setFixedCombination(partition);
        }
        Deque<Supplier<Stream<Match>>> suppliers = new LinkedList<>(
                resetFromCheckpoint(sequencedFixtures));
        while (true)
        {
            final FixtureSet fixtureSet = sequencedFixtures.get(suppliers.size());
            if (!fixtureSet.hasNext())
            {
                suppliers.removeLast();
                if (suppliers.isEmpty())
                {
                    break;
                }
                fixtureSet.reset();
            }
            else
            {
                suppliers.addLast(fixtureSet.next());
                String combStr = sequencedFixtures
                        .stream()
                        .limit(suppliers.size())
                        .map(FixtureSet::getCombinationString)
                        .collect(Collectors.joining(","));
                final FixtureList fixtureList = new FixtureList(suppliers);
                if (!fixtureList.isValid())
                {
                    LOG.debug("Invalid: {}", combStr);
                    suppliers.removeLast();
                }
                else if (suppliers.size() == sequencedFixtures.size())
                {
                    submitForEvaluation(fixtureList, new Checkpoint(sequencedFixtures), combStr);
                    suppliers.removeLast();
                }
            }
        }
    }

    private void submitForEvaluation(FixtureList fixtureList, Checkpoint checkpoint, String combStr)
    {
        // synchronized (pendingTransactions)
        // {
        // pendingTransactions.add(checkpoint);
        // }
        // submitJob(() -> evaluate(fixtureList, checkpoint));
        evaluate(fixtureList, checkpoint, combStr);
    }

    // private void submitJob(Runnable job)
    // {
    // try
    // {
    // executor.execute(job);
    // }
    // catch (RejectedExecutionException ex)
    // {
    // try
    // {
    // executorQueue.put(job);
    // }
    // catch (InterruptedException e)
    // {
    // LOG.error("Unable to submit job", ex);
    // }
    // }
    // }

    private void evaluate(FixtureList fixtureList, Checkpoint checkpoint, String combStr)
    {
        try
        {
            final int score = fixtureList.evaluate();
            LOG.debug("Evaluated, score {}: {}", score, combStr);
            synchronized (this)
            {
                if (score < bestScore)
                {
                    LOG.info("Best score so far: {} - {}", score, fixtureList);
                    bestScore = score;
                    try (FileWriter fw = new FileWriter(new File(bestDir, String.format(
                            "%1$tY%1$tm%1$td%1$tk%1$tM%1$tS%1$tN.%2$d", new Date(), score))))
                    {
                        fw.write(fixtureList.toString());
                    }
                }
            }
            writeCheckpoint(checkpoint);
            // boolean isFirst;
            // synchronized (pendingTransactions)
            // {
            // isFirst = checkpoint == pendingTransactions.first();
            // pendingTransactions.remove(checkpoint);
            // }
            // if (isFirst)
            // {
            // writeCheckpoint(checkpoint);
            // }
        }
        catch (Throwable t)
        {
            LOG.error("Fault barrier", t);
        }
    }

    private Collection<Supplier<Stream<Match>>> resetFromCheckpoint(List<FixtureSet> fixtureSets)
    {
        Collection<Supplier<Stream<Match>>> answer = new LinkedList<>();
        String fileName = Stream
                .of(checkpointDir.list())
                .sorted((a, b) -> b.compareTo(a))
                .findFirst()
                .orElse(null);
        if (fileName != null)
        {
            String[] split = fileName.split("\\.");
            this.bestScore = Integer.valueOf(split[1]);
            final Checkpoint checkpoint = new Checkpoint(IntStream
                    .range(0, split[0].length() / 2)
                    .map(i -> Integer.valueOf(split[0].substring(i * 2, i * 2 + 2), 16))
                    .toArray());
            answer = checkpoint.applyTo(fixtureSets);
            LOG.debug("Reset to checkpoint {}", checkpoint);
        }
        else
        {
            LOG.debug("No checkpoint file found, starting from the beginning");
        }
        return answer;
    }

    private synchronized void writeCheckpoint(Checkpoint checkpoint)
    {
        int best;
        synchronized (this)
        {
            best = bestScore;
        }
        String counts = IntStream
                .of(checkpoint.combCounts)
                .mapToObj(i -> String.format("%02x", i))
                .collect(Collectors.joining());
        String filename = String.format("%s.%d", counts, best);
        try (Writer writer = new FileWriter(new File(checkpointDir, filename)))
        {
            writer.write("");
        }
        catch (IOException ex)
        {
            LOG.error("Unexpected error writing checkpoint file", ex);
        }
        tidyCheckpoints(filename);
        // submitJob(() -> tidyCheckpoints(counts));
    }

    private void tidyCheckpoints(String cpName)
    {
        Stream.of(checkpointDir.listFiles((f) -> f.getName().compareTo(cpName) < 0)).forEach(
                File::delete);
    }

    private static class Checkpoint implements Comparable<Checkpoint>
    {
        @JsonCreator
        private static Checkpoint create(String str)
        {
            return new Checkpoint(Stream.of(str.split(",")).mapToInt(Integer::valueOf).toArray());
        }

        private final int[] combCounts;

        public Checkpoint(List<FixtureSet> fixtureSets)
        {
            this(fixtureSets.stream().mapToInt(FixtureSet::getNextCombination).toArray());
        }

        private Checkpoint(int[] combCounts)
        {
            this.combCounts = combCounts;
        }

        @Override
        public int compareTo(Checkpoint o)
        {
            int answer = 0;
            for (int i = 0; answer == 0 && i < combCounts.length; i++)
            {
                answer = combCounts[i] - o.combCounts.length;
            }
            return answer;
        }

        @JsonValue
        public String toString()
        {
            return IntStream.of(combCounts).mapToObj(i -> String.format("%03d", i)).collect(
                    Collectors.joining("."));
        }

        public Collection<Supplier<Stream<Match>>> applyTo(List<FixtureSet> fixtureSets)
        {
            Collection<Supplier<Stream<Match>>> answer = new LinkedList<>();
            if (fixtureSets.size() != combCounts.length)
            {
                throw new RuntimeException(
                        "Can't apply checkpoint as collections are different lengths");
            }
            Iterator<FixtureSet> it = fixtureSets.iterator();
            for (int i = 0; it.hasNext(); i++)
            {
                FixtureSet fs = it.next();
                fs.setNextCombination(combCounts[i] - 1);
                Supplier<Stream<Match>> supplier = fs.next();
                if (it.hasNext())
                {
                    answer.add(supplier);
                }

            }
            return answer;
        }
    }
}
