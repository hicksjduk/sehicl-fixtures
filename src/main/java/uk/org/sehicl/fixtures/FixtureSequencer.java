package uk.org.sehicl.fixtures;

import java.util.Arrays;
import java.util.List;

public class FixtureSequencer
{
    private final List<SequenceItem> items = Arrays.asList(
            // @formatter:off
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D3", "D4", "D5"),
            new SequenceItem("D2", "D3", "D4", "D5"),
            new SequenceItem("D1", "D2", "D3", "D4", "D5"),
            new SequenceItem("D5", 5),
            new SequenceItem("D2", 3),
            new SequenceItem("D4", 5),
            new SequenceItem("D1", 3),
            new SequenceItem("D3", 5)
            // @formatter:on
    );

    public static class SequenceItem
    {
        private final List<String> leagues;
        private final int matches;

        public SequenceItem(String... leagues)
        {
            this(Arrays.asList(leagues), 1);
        }

        public SequenceItem(String league, int matches)
        {
            this(Arrays.asList(league), matches);
        }

        private SequenceItem(List<String> leagues, int matches)
        {
            this.leagues = leagues;
            this.matches = matches;
        }

        public List<String> getLeagues()
        {
            return leagues;
        }

        public int getMatches()
        {
            return matches;
        }
    }

    public List<SequenceItem> getItems()
    {
        return items;
    }
}
