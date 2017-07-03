package uk.org.sehicl.fixtures;

import java.util.ArrayList;
import java.util.List;

import uk.org.sehicl.fixtures.FixtureSet.CombinationIterator;

public class FixturesEvaluator
{
    private final FixtureList fixtures = new FixtureList();
    private final FixtureSequencer sequencer = new FixtureSequencer();

    public static void main(String[] args)
    {
        new FixturesEvaluator().evaluate();
    }

    private void evaluate()
    {
        List<FixtureSet> sequencedFixtures = new ArrayList<>(sequencer.getSequencedFixtures(fixtures));
        CombinationIterator[] iterators = new CombinationIterator[sequencedFixtures.size()];
        long combCount = 0;
        boolean done = false;
        while (!done)
        {
            done = true;
            for (int i = 0; done && i < iterators.length; i++)
            {
                if (iterators[i] == null)
                {
                    iterators[i] = sequencedFixtures.get(i).iterator();
                }
                if (!iterators[i].hasNext())
                {
                    iterators[i] = sequencedFixtures.get(i).iterator();
                }
                else
                {
                    done = false;
                }
            }
            System.out.println(++combCount);
        }
    }
}
