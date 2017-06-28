package uk.org.sehicl.fixtures;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class FixtureSet implements Iterator<List<Match>>
{
    private final List<Match> matches;

    private List<Match> currentCombination;
    private final int maxCombinations;
    private int nextCombination = 0;

    public FixtureSet(Collection<Match> matches)
    {
        this(matches, IntStream.rangeClosed(3, matches.size()).reduce(2, (x, y) -> x * y));
    }

    public FixtureSet(Collection<Match> matches, int combination)
    {
        this.matches = new ArrayList<>(matches);
        maxCombinations = IntStream.rangeClosed(3, matches.size()).reduce(2, (x, y) -> x * y);
    }
    
    @Override
    public boolean hasNext()
    {
        return nextCombination < maxCombinations;
    }

    @Override
    public List<Match> next()
    {
        if (!hasNext())
        {
            throw new IllegalStateException("No next combination");
        }
        return currentCombination = getCombination(nextCombination++);
    }

    public List<Match> current()
    {
        if (currentCombination == null)
        {
            throw new IllegalStateException("No current combination");
        }
        return currentCombination;
    }

    private List<Match> getCombination(int combination)
    {
        List<Match> workingSet = new ArrayList<>(matches);
        int workingCombination = combination;
        List<Match> answer = new LinkedList<>();
        while (!workingSet.isEmpty())
        {
            int workingSetSize = workingSet.size();
            int i = workingCombination % workingSetSize;
            workingCombination = workingCombination / workingSetSize;
            answer.add(workingSet.remove(i));
        }
        return answer;
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
