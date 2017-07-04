package uk.org.sehicl.fixtures;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FixturesEvaluator
{
    private final FixtureSequencer sequencer = new FixtureSequencer();

    public static void main(String[] args)
    {
        new FixturesEvaluator().evaluate();
    }

    private void evaluate()
    {
        List<FixtureSet> sequencedFixtures = new ArrayList<>(
                sequencer.getSequencedFixtures());
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
                evaluate(sequencedFixtures);
            }
        }
    }

    private void evaluate(List<FixtureSet> sequencedFixtures)
    {
        System.out.println(sequencedFixtures
                .stream()
                .map(FixtureSet::getCombinationString)
                .collect(Collectors.joining(", ")));
    }
}
