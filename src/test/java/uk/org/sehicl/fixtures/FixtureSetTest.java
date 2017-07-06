package uk.org.sehicl.fixtures;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class FixtureSetTest
{
    @Test
    public void test()
    {
        AtomicInteger team = new AtomicInteger();
        int matchCount = 5;
        FixtureSet fs = new FixtureSet(
                IntStream
                        .range(0, matchCount)
                        .mapToObj(i -> new Match(new Team("" + team.incrementAndGet()),
                                new Team("" + team.incrementAndGet())))
                        .collect(Collectors.toList()));
        Set<String> permutations = new HashSet<>();
        while (fs.hasNext())
        {
            final String pString = fs.next().get().collect(Collectors.toList()).toString();
            assertThat(permutations.add(pString)).as(pString).isTrue();
        }
    }

}
