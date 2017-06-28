package uk.org.sehicl.fixtures;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.Test;

import uk.org.sehicl.fixtures.FixtureSequencer.SequenceItem;

public class FixtureSequencerTest
{
    @Test
    public void testItems()
    {
        Map<String, AtomicInteger> countsByDivision = new HashMap<>();
        new FixtureSequencer().getItems().forEach(item -> add(countsByDivision, item));
        assertThat(countsByDivision.get("D1").get()).isEqualTo(30);
        assertThat(countsByDivision.get("D2").get()).isEqualTo(30);
        assertThat(countsByDivision.get("D3").get()).isEqualTo(45);
        assertThat(countsByDivision.get("D4").get()).isEqualTo(45);
        assertThat(countsByDivision.get("D5").get()).isEqualTo(45);
    }

    private void add(Map<String, AtomicInteger> countsByDivision, SequenceItem item)
    {
        item.getLeagues().forEach(l ->
        {
            AtomicInteger count = countsByDivision.get(l);
            if (count == null)
            {
                countsByDivision.put(l, new AtomicInteger(item.getMatches()));
            }
            else
            {
                count.addAndGet(item.getMatches());
            }
        });
    }

    @Test
    public void testGetSequencedFixtures()
    {
        List<FixtureSet> result = new FixtureSequencer().getSequencedFixtures(new FixtureList());
        Iterator<FixtureSet> iterator = result.iterator();
        IntStream.range(0, 13).forEach(i ->
        {
            assertThat(iterator.next().size()).isEqualTo(5);
            assertThat(iterator.next().size()).isEqualTo(4);
            assertThat(iterator.next().size()).isEqualTo(4);
        });
        assertThat(iterator.next().size()).isEqualTo(5);
        assertThat(iterator.next().size()).isEqualTo(5);
        assertThat(iterator.next().size()).isEqualTo(3);
        assertThat(iterator.next().size()).isEqualTo(5);
        assertThat(iterator.next().size()).isEqualTo(3);
        assertThat(iterator.next().size()).isEqualTo(5);
        assertThat(iterator.hasNext()).isFalse();
        result.forEach(System.out::println);
    }
}
