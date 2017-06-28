package uk.org.sehicl.fixtures;

import org.junit.Test;

public class MatchDateTest
{

    @Test
    public void test()
    {
        MatchDate.getInstances().stream().forEach(System.out::println);
    }

}
