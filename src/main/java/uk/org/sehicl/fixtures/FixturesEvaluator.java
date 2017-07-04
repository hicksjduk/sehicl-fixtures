package uk.org.sehicl.fixtures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FixturesEvaluator
{
    private final FixtureSequencer sequencer = new FixtureSequencer();
    private final ExecutorService executor = Executors.newFixedThreadPool(8);
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
//                    evaluate(sequencedFixtures);
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
            System.out.println();
            System.out.println(
                    sequencedFixtures.stream().map(FixtureSet::getCombinationString).collect(
                            Collectors.joining(", ")));
            final FixtureList fl = new FixtureList(
                    sequencedFixtures.stream().map(FixtureSet::get).collect(Collectors.toList()));
            if (!fl.isValid())
            {
                fl.getValidationErrors().forEach(System.out::println);
            }
            else
            {
                updateBest(fl);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    private void updateBest(FixtureList fl)
    {
        final int score = fl.evaluate();
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
            System.out.println(String.format("Combination found with best score %d:"));
            System.out.println(bestFixtureList);
        }
    }
}
