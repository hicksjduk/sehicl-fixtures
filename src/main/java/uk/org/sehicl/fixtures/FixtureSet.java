package uk.org.sehicl.fixtures;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FixtureSet implements Iterator<Supplier<Stream<Match>>>
{
    private final List<Match> matches;
    private final List<Supplier<Stream<Match>>> suppliers;
    private AtomicInteger nextCombination = new AtomicInteger(0);
    private Integer fixedCombination = null;

    public int getNextCombination()
    {
        return nextCombination.get();
    }

    public void setNextCombination(int newValue)
    {
        nextCombination.set(newValue);
    }

    public FixtureSet(Collection<Match> matches, boolean variationSignificant)
    {
        this.matches = new ArrayList<>(matches);
        int maxCombinations = variationSignificant
                ? IntStream.rangeClosed(3, matches.size()).reduce(2, (x, y) -> x * y) : 1;
        suppliers = IntStream.range(0, maxCombinations).mapToObj(this::supplier).collect(
                Collectors.toCollection(() -> new ArrayList<>()));
    }

    public Integer getFixedCombination()
    {
        return fixedCombination;
    }

    public void setFixedCombination(int fixedCombination)
    {
        this.fixedCombination = fixedCombination;
        nextCombination.set(fixedCombination);
    }

    @Override
    public boolean hasNext()
    {
        return fixedCombination == null ? nextCombination.get() < suppliers.size()
                : fixedCombination == nextCombination.get();
    }

    @Override
    public Supplier<Stream<Match>> next()
    {
        if (!hasNext())
        {
            throw new IllegalStateException();
        }
        return suppliers.get(nextCombination.getAndIncrement());
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

    public void reset()
    {
        nextCombination.set(0);
    }

    public String getCombinationString()
    {
        return String.format("%d/%d", nextCombination.get(), suppliers.size());
    }
}
