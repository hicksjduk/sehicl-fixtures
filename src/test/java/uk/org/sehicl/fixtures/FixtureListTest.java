package uk.org.sehicl.fixtures;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

public class FixtureListTest
{
    @Test
    public void test()
    {
        LeagueFixtures.getLeagueLists().forEach(fl ->
        {
            test(fl);
            System.out.println(fl);
        });
    }

    private void test(LeagueFixtures fl)
    {
        fl.getTeams().forEach(t -> test(t, fl));
    }

    private void test(Team t, LeagueFixtures fl)
    {
        List<Match> fixtures = fl
                .getFixtures()
                .stream()
                .filter(m -> t == m.getHomeTeam() || t == m.getAwayTeam())
                .collect(Collectors.toList());
        Map<Team, Match> homeGames = new HashMap<>();
        Map<Team, Match> awayGames = new HashMap<>();
        int haSequence = 0;
        Match lastMatch = null;
        for (Match m : fixtures)
        {
            boolean home = m.getHomeTeam() == t;
            Team opponents = home ? m.getAwayTeam() : m.getHomeTeam();
            (home ? homeGames : awayGames).put(opponents, m);
            if (lastMatch == null
                    || t == (home ? lastMatch.getAwayTeam() : lastMatch.getHomeTeam()))
            {
                haSequence = 1;
            }
            else
            {
                assertThat(haSequence++).isLessThan(2);
            }
        }
        assertThat(homeGames.size() + awayGames.size()).isEqualTo(fixtures.size());
        assertThat(homeGames.size() - awayGames.size()).isBetween(-1, 1);
    }
}
