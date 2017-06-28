package uk.org.sehicl.fixtures;

import static org.assertj.core.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import uk.org.sehicl.fixtures.FixtureSequencer.SequenceItem;

public class FixtureSequencerTest
{
    @Test
    public void test()
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
}
