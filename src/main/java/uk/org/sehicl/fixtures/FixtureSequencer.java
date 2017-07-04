package uk.org.sehicl.fixtures;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public List<FixtureSet> getSequencedFixtures()
    {
        List<FixtureSet> answer = new LinkedList<>();
        Map<String, Iterator<Match>> matchStreams = LeagueFixtures.getLeagueLists().stream().collect(
                Collectors.toMap(LeagueFixtures::getLeague, l -> l.getFixtures().iterator()));
        getItems().forEach(item -> answer.add(getMatches(item, matchStreams)));
        return answer;
    }

    private FixtureSet getMatches(SequenceItem item, Map<String, Iterator<Match>> matchIterators)
    {
        List<Match> answer = new LinkedList<>();
        IntStream.range(0, item.getMatches()).forEach(i ->
        {
            item.getLeagues().forEach(l ->
            {
                answer.add(matchIterators.get(l).next());
            });
        });
        return new FixtureSet(answer);
    }
}
