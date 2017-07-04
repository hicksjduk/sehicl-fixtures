package uk.org.sehicl.fixtures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixturesEvaluator
{
    private static final Logger LOG = LoggerFactory.getLogger(FixturesEvaluator.class);

    private final FixtureSequencer sequencer = new FixtureSequencer();
    private final ExecutorService executor = Executors.newWorkStealingPool();
    private int bestScore = Integer.MAX_VALUE;
    private FixtureList bestFixtureList = null;

    public static void main(String[] args)
    {
        new FixturesEvaluator().evaluate();
    }

    private void evaluate()
    {
        List<FixtureSet> sequencedFixtures = new ArrayList<>(sequencer.getSequencedFixtures());
        boolean done = false;
        while (!done)
        {
            done = true;
            for (FixtureSet fs : sequencedFixtures)
            {
                if (fs.nextOrReset())
                {
                    done = false;
                    break;
                }
            }
            if (!done)
            {
                try
                {
                    executor.execute(() -> evaluate(sequencedFixtures));
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        }
    }

    private void evaluate(List<FixtureSet> sequencedFixtures)
    {
        try
        {
            LOG.debug(
                    sequencedFixtures.stream().map(FixtureSet::getCombinationString).collect(
                            Collectors.joining(", ")));
            final FixtureList fl = new FixtureList(
                    sequencedFixtures.stream().map(FixtureSet::get).collect(Collectors.toList()));
            if (fl.isValid())
            {
                updateBest(fl.evaluate(), fl);
            }
        }
        catch (Throwable t)
        {
            LOG.error("Fault barrier", t);
        }
    }

    private void updateBest(int score, FixtureList fl)
    {
        boolean best = false;
        synchronized (this)
        {
            if (best = score < bestScore)
            {
                bestScore = score;
                bestFixtureList = fl;
            }
        }
        if (best)
        {
            LOG.info(String.format("Combination found with best score {}:", bestScore));
            LOG.info("{}", bestFixtureList);
        }
    }
}
