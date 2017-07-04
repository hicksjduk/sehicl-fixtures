package uk.org.sehicl.fixtures;

import java.util.stream.Collectors;

import org.junit.Test;

public class FixtureListTest
{
    @Test
    public void test()
    {
        final FixtureList fl = new FixtureList(new FixtureSequencer()
                .getSequencedFixtures()
                .stream()
                .map(FixtureSet::get)
                .collect(Collectors.toList()));
        if (fl.isValid())
        {
            System.out.println(fl.evaluate());
        }
        else
        {
            fl.getValidationErrors().forEach(System.out::println);
        }
        System.out.println(fl);
    }
}
