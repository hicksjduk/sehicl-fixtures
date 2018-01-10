package uk.org.sehicl.fixtures;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FixtureList
{
    private static final Logger LOG = LoggerFactory.getLogger(FixtureList.class);

    @FunctionalInterface
    private static interface DateFormatter
    {
        String format(Date d);
    }

    private static DateFormatter formatter(String formatString)
    {
        DateFormat formatter = new SimpleDateFormat(formatString);
        Map<Date, String> cache = new HashMap<>();
        return date ->
        {
            String answer;
            synchronized (cache)
            {
                answer = cache.get(date);
                if (answer == null)
                {
                    synchronized (formatter)
                    {
                        answer = formatter.format(date);
                    }
                    cache.put(date, answer);
                }
            }
            return answer;
        };
    }

    private final static DateFormatter DATE_FORMATTER = formatter("yyyyMMdd");
    private final static DateFormatter TIME_FORMATTER = formatter("HHmm");

    private final List<ScheduledMatch> matches;
    private Boolean valid = null;
    private final AtomicInteger score = new AtomicInteger();

    public FixtureList(Collection<Supplier<Stream<Match>>> sequence)
    {
        final Iterator<Match> matchSequence = sequence
                .stream()
                .map(Supplier::get)
                .reduce(Stream.of(), (x, y) -> Stream.concat(x, y))
                .iterator();
        matches = MatchDate
                .getInstances()
                .stream()
                .flatMap((md) -> getMatches(md, matchSequence))
                .collect(Collectors.toList());
    }

    private boolean validate()
    {
        final BiConsumer<Map<Object, SortedSet<ScheduledMatch>>, ScheduledMatch> accumulator = (map,
                match) ->
        {
            Stream.of(match.match.getHomeTeam(), match.match.getAwayTeam()).forEach(team ->
            {
                {
                    SortedSet<ScheduledMatch> matchesForTeam = map.get(team);
                    if (matchesForTeam == null)
                        map.put(team, matchesForTeam = new TreeSet<ScheduledMatch>());
                    matchesForTeam.add(match);
                }
                ;
            });
        };
        final Map<Object, SortedSet<ScheduledMatch>> schedulesByTeam = matches
                .stream()
                .collect(HashMap::new, accumulator, Map::putAll);
        schedulesByTeam
                .entrySet()
                .stream()
                .forEach(e -> validateSchedule(e.getKey(), e.getValue()));
        return true;
    }

    private void validateSchedule(Object team, SortedSet<ScheduledMatch> schedule)
    {
        forEachAdjacentPair(schedule.stream().map(ScheduledMatch::getDateTime),
                (first, second) -> validateGap(team, first, second));
    }

    private void validateGap(Object team, Date first, Date second)
    {
        if (second.getTime() - first.getTime() < TimeUnit.DAYS.toMillis(8))
            throw new RuntimeException(String.format(
                    "Team %s playing less than 2 weeks apart: %tc, %tc", team, first, second));
    }

    public boolean isValid()
    {
        boolean answer = false;
        synchronized (this)
        {
            if (valid == null)
                try
                {
                    valid = answer = this.validate();
                }
                catch (RuntimeException ex)
                {
                    LOG.error("Validation error", ex);
                }
            else
                answer = valid;
        }
        return answer;
    }

    @JsonProperty
    private Boolean getValid()
    {
        return valid;
    }

    @JsonProperty
    private void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    private Stream<ScheduledMatch> getMatches(MatchDate md, Iterator<Match> matchSequence)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(md.getDate());
        cal.set(Calendar.MINUTE, md.getMins());
        final List<Integer> startHours = md.getHours();
        ScheduledMatch[] matches = new ScheduledMatch[startHours.size()];
        for (int i = 0; matchSequence.hasNext() && i < matches.length; i++)
        {
            cal.set(Calendar.HOUR_OF_DAY, startHours.get(i));
            matches[i] = new ScheduledMatch(matchSequence.next(), cal.getTime(),
                    i % 2 == 0 ? Court.A : Court.B);
        }
        return Stream.of(matches).filter(Objects::nonNull);
    }

    public static class ScheduledMatch implements Comparable<ScheduledMatch>
    {
        private final Match match;
        private final Date dateTime;
        private final Court court;

        public ScheduledMatch(Match match, Date dateTime, Court court)
        {
            this.match = match;
            this.dateTime = dateTime;
            this.court = court;
        }

        @Override
        public int compareTo(ScheduledMatch o)
        {
            return dateTime.compareTo(o.dateTime);
        }

        public String toString()
        {
            return String.format("%tc %s %s", dateTime, court, match);
        }

        public Date getDateTime()
        {
            return dateTime;
        }
    }

    public int evaluate()
    {
        final Set<Team> teams = matches
                .stream()
                .flatMap(m -> Stream.of(m.match.getHomeTeam(), m.match.getAwayTeam()))
                .collect(Collectors.toSet());
        final List<String> dateStrings = MatchDate
                .getInstances()
                .stream()
                .map(m -> DATE_FORMATTER.format(m.getDate()))
                .collect(Collectors.toCollection(ArrayList::new));
        final Set<String> timeStrings = matches
                .stream()
                .map(m -> TIME_FORMATTER.format(m.dateTime))
                .collect(Collectors.toSet());
        final Integer answer = teams
                .stream()
                .map(t -> matches.stream().filter(
                        m -> m.match.getHomeTeam() == t || m.match.getAwayTeam() == t))
                .collect(Collectors.summingInt(
                        (mstr) -> evaluateTeamFixtures(mstr, dateStrings, timeStrings)));
        score.set(answer);
        return answer;
    }

    public int getScore()
    {
        return score.get();
    }

    @JsonProperty
    private void setScore(int score)
    {
        this.score.set(score);
    }

    private int evaluateTeamFixtures(Stream<ScheduledMatch> matches, List<String> dateStrings,
            Set<String> timeStrings)
    {
        Map<String, AtomicInteger> countsByTime = timeStrings
                .stream()
                .collect(Collectors.toMap(s -> s, s -> new AtomicInteger()));
        Map<Court, AtomicInteger> countsByCourt = Stream
                .of(Court.values())
                .collect(Collectors.toMap(c -> c, c -> new AtomicInteger()));
        List<Integer> interMatchGaps = new ArrayList<>();
        AtomicReference<ScheduledMatch> lastMatch = new AtomicReference<>();
        matches.forEach(m ->
        {
            countsByTime.get(TIME_FORMATTER.format(m.dateTime)).incrementAndGet();
            countsByCourt.get(m.court).incrementAndGet();
            if (lastMatch.get() != null)
            {
                interMatchGaps.add(getGap(dateStrings, lastMatch.get().dateTime, m.dateTime));
            }
            lastMatch.set(m);
        });
        countsByTime.get(TIME_FORMATTER.format(lastMatch.get().dateTime)).decrementAndGet();
        countsByCourt.get(lastMatch.get().court).decrementAndGet();
        return getSpread(countsByTime.values(), AtomicInteger::get)
                + getSpread(countsByCourt.values(), AtomicInteger::get)
                + getSpread(interMatchGaps, Integer::intValue);
    }

    private int getGap(List<String> dates, Date d1, Date d2)
    {
        return dates.indexOf(DATE_FORMATTER.format(d2)) - dates.indexOf(DATE_FORMATTER.format(d1));
    }

    private <T> int getSpread(Collection<T> values, ToIntFunction<? super T> converter)
    {
        return values.stream().mapToInt(converter).max().getAsInt()
                - values.stream().mapToInt(converter).min().getAsInt();
    }

    public static enum Court
    {
        A, B
    }

    public String toString()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        matches.forEach(pw::println);
        return sw.toString();
    }

    public static <T> void forEachAdjacentPair(Stream<T> stream, BiConsumer<T, T> processor)
    {
        Function<T, Function<T, T>> initFuncSupplier = value -> ignoredValue -> value;
        Function<T, Function<T, T>> processorFuncSupplier = second -> first ->
        {
            processor.accept(first, second);
            return second;
        };
        BiFunction<Function<T, T>, T, Function<T, T>> aggregator = (prevFunc,
                value) -> prevFunc == null ? initFuncSupplier.apply(value)
                        : prevFunc.andThen(processorFuncSupplier.apply(value));
        Function<T, T> processorChain = stream.reduce(null, aggregator, Function::andThen);
        if (processorChain != null)
            processorChain.apply(null);
    }
}
