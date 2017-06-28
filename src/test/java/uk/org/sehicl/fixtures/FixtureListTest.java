package uk.org.sehicl.fixtures;

import static org.assertj.core.api.Assertions.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class FixtureListTest
{

    @Test
    public void test()
    {
        FixtureList.getInstances().forEach(this::test);
    }

    private void test(FixtureList fl)
    {
        Map<List<Team>, AtomicInteger> matchCountsByHomeAway = new HashMap<>();
        fl.getFixtures().forEach(m -> add(m, matchCountsByHomeAway));
        fl.getTeams().forEach(t1 ->
        {
            fl.getTeams().forEach(t2 ->
            {
                if (t1 != t2)
                {
                    int t1t2Count = matchCountsByHomeAway
                            .getOrDefault(Arrays.asList(t1, t2), new AtomicInteger(0))
                            .get();
                    int t2t1Count = matchCountsByHomeAway
                            .getOrDefault(Arrays.asList(t2, t1), new AtomicInteger(0))
                            .get();
                    if (fl.getTeams().size() == 6)
                    {
                        assertThat(t1t2Count).as("Home: %s, Away: %s", t1, t2).isEqualTo(1);
                        assertThat(t2t1Count).as("Home: %s, Away: %s", t2, t1).isEqualTo(1);
                    }
                    else
                    {
                        assertThat(t1t2Count).isNotEqualTo(t2t1Count);
                        assertThat(t1t2Count + t2t1Count).isEqualTo(1);
                    }
                }
            });
        });
    }

    private void add(Match m, Map<List<Team>, AtomicInteger> matchCountsByHomeAway)
    {
        List<Team> key = Arrays.asList(m.getHomeTeam(), m.getAwayTeam());
        AtomicInteger count = matchCountsByHomeAway.get(key);
        if (count == null)
        {
            matchCountsByHomeAway.put(key, new AtomicInteger(1));
        }
        else
        {
            count.incrementAndGet();
        }
    }
}
