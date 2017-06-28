package uk.org.sehicl.fixtures;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FixtureSet
{
    private final List<Match> matches;

    public FixtureSet(Collection<Match> matches)
    {
        this.matches = new ArrayList<>(matches);
    }

    public String toString()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        matches.forEach(pw::println);
        return sw.toString();
    }

    public int size()
    {
        return matches.size();
    }
}
