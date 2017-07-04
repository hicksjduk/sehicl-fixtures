package uk.org.sehicl.fixtures;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FixtureSet
{
    private final List<Match> matches;
    private final List<Supplier<Stream<Match>>> suppliers;
    private final int maxCombinations;
    private int currentCombination = 0;

    public FixtureSet(Collection<Match> matches)
    {
        this.matches = new ArrayList<>(matches);
        maxCombinations = IntStream.rangeClosed(3, matches.size()).reduce(2, (x, y) -> x * y);
        suppliers = IntStream.range(0, maxCombinations).mapToObj(this::supplier).collect(
                Collectors.toCollection(() -> new ArrayList<>()));
    }
    
    public String getCombinationString()
    {
        return String.format("%d/%d", currentCombination + 1, maxCombinations);
    }

    public boolean nextOrReset()
    {
        boolean answer = ++currentCombination < maxCombinations;
        if (!answer)
        {
            currentCombination = 0;
        }
        return answer;
    }

    public Supplier<Stream<Match>> get()
    {
        return suppliers.get(currentCombination);
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

    private Supplier<Stream<Match>> supplier(int combination)
    {
        List<Match> list = new LinkedList<>();
        return () ->
        {
            synchronized (list)
            {
                if (list.isEmpty())
                {
                    list.addAll(getCombination(combination));
                }
            }
            return list.stream();
        };
    }
}
