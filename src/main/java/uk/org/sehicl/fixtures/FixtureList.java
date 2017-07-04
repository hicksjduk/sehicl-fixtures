package uk.org.sehicl.fixtures;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FixtureList
{
    private final static DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
    private final static DateFormat TIME_FORMATTER = new SimpleDateFormat("HHmm");

    private final List<ScheduledMatch> matches;
    private final Set<Team> teams;
    private final List<String> dates;
    private final Set<String> timeStrings;
    private final List<String> validationErrors;

    public FixtureList(List<Supplier<Stream<Match>>> sequence)
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
        teams = matches
                .stream()
                .flatMap(m -> Stream.of(m.match.getHomeTeam(), m.match.getAwayTeam()))
                .collect(Collectors.toSet());
        dates = MatchDate
                .getInstances()
                .stream()
                .map(m -> DATE_FORMATTER.format(m.getDate()))
                .collect(Collectors.toCollection(ArrayList::new));
        timeStrings = matches
                .stream()
                .map(m -> TIME_FORMATTER.format(m.dateTime))
                .collect(Collectors.toSet());
        validationErrors = validate();
    }

    private List<String> validate()
    {
        Set<String> teamDates = new HashSet<>();
        for (ScheduledMatch m : matches)
        {
            for (Team t : Arrays.asList(m.match.getHomeTeam(), m.match.getAwayTeam()))
            {
                String td = String.format("%s %s", DATE_FORMATTER.format(m.dateTime), t);
                if (!teamDates.add(td))
                {
                    return Arrays.asList(
                            String.format("Team playing more than once on the same day - %s", td));
                }
            }
        }
        return null;
    }

    public boolean isValid()
    {
        return validationErrors == null;
    }

    public List<String> getValidationErrors()
    {
        return validationErrors;
    }

    private Stream<ScheduledMatch> getMatches(MatchDate md, Iterator<Match> matchSequence)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(md.getDate());
        cal.set(Calendar.HOUR_OF_DAY, md.getFirstHour());
        cal.set(Calendar.MINUTE, md.getMins());
        ScheduledMatch[] matches = new ScheduledMatch[md.getMatchCount()];
        for (int i = 0; i < matches.length; i++)
        {
            matches[i] = new ScheduledMatch(matchSequence.next(), cal.getTime(),
                    i % 2 == 0 ? Court.A : Court.B);
            if (i % 2 != 0)
            {
                cal.add(Calendar.HOUR_OF_DAY, 1);
            }
        }
        return Stream.of(matches);
    }

    public static class ScheduledMatch
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

        public String toString()
        {
            return String.format("%tc %s %s", dateTime, court, match);
        }
    }

    public int evaluate()
    {
        return teams
                .stream()
                .map(t -> matches.stream().filter(
                        m -> m.match.getHomeTeam() == t || m.match.getAwayTeam() == t))
                .collect(Collectors.summingInt(this::evaluateTeamFixtures));
    }

    private int evaluateTeamFixtures(Stream<ScheduledMatch> matches)
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
                final int gap = getGap(lastMatch.get().dateTime, m.dateTime);
                if (gap == 0)
                {
                    throw new RuntimeException(
                            String.format("Same team is playing more than once on %s",
                                    DATE_FORMATTER.format(m.dateTime)));
                }
                interMatchGaps.add(gap);
            }
            lastMatch.set(m);
        });
        countsByTime.get(TIME_FORMATTER.format(lastMatch.get().dateTime)).decrementAndGet();
        countsByCourt.get(lastMatch.get().court).decrementAndGet();
        return getSpread(countsByTime.values(), AtomicInteger::get)
                + getSpread(countsByCourt.values(), AtomicInteger::get)
                + getSpread(interMatchGaps, Integer::intValue);
    }

    private final int getGap(Date d1, Date d2)
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
}
