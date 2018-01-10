package uk.org.sehicl.fixtures;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixtureListTest
{
    private static final Logger LOG = LoggerFactory.getLogger(FixtureListTest.class);

    @Test
    public void test()
    {
        final FixtureList fl = new FixtureList(new FixtureSequencer()
                .getSequencedFixtures()
                .stream()
                .map(FixtureSet::next)
                .collect(Collectors.toList()));
        if (fl.isValid())
        {
            LOG.debug("Valid fixture list with an evaluation score of {}:\n{}", fl.evaluate(), fl);
        }
        else
        {
            LOG.debug("Invalid fixture list:\n{}", fl);
        }
    }

    @Test
    public void testForEachAdjacentPairWithEmptyStream()
    {
        testForEachAdjacentPair();
    }

    @Test
    public void testForEachAdjacentPairWithOneElementStream()
    {
        testForEachAdjacentPair("x");
    }

    @Test
    public void testForEachAdjacentPairWithTwoElementStream()
    {
        testForEachAdjacentPair(1, 2);
    }

    @Test
    public void testForEachAdjacentPairWithMultipleElementStream()
    {
        testForEachAdjacentPair(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void testForEachAdjacentPairWithNullsInStream()
    {
        testForEachAdjacentPair(null, "a", "b", null, "c", null, "d", null);
    }

    @SafeVarargs
    private final <T> void testForEachAdjacentPair(T... objects)
    {
        LOG.debug("Testing forEachAdjacentPair, input = {}", Arrays.toString(objects));
        @SuppressWarnings("unchecked")
        BiConsumer<T, T> processor = (BiConsumer<T, T>) mock(BiConsumer.class);
        FixtureList.forEachAdjacentPair(Stream.of(objects), processor);
        for (int i = 0, j = 1; j < objects.length; i++, j++)
        {
            verify(processor).accept(objects[i], objects[j]);
            LOG.debug("Processor called with ({}, {})", objects[i], objects[j]);
        }
        verifyNoMoreInteractions(processor);
    }
}
