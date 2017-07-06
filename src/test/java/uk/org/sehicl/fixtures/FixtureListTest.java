package uk.org.sehicl.fixtures;

import java.util.stream.Collectors;

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
}
