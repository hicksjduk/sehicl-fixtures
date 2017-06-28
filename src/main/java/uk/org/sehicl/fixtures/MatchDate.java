package uk.org.sehicl.fixtures;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

public class MatchDate
{
    private static List<MatchDate> INSTANCES = Arrays.asList(
            new MatchDate(2017, Calendar.SEPTEMBER, 17, 20),
            new MatchDate(2017, Calendar.SEPTEMBER, 24, 20),
            new MatchDate(2017, Calendar.OCTOBER, 1, 17),
            new MatchDate(2017, Calendar.OCTOBER, 15, 17),
            new MatchDate(2017, Calendar.OCTOBER, 22, 17),
            new MatchDate(2017, Calendar.OCTOBER, 29, 17),
            new MatchDate(2017, Calendar.NOVEMBER, 5, 17),
            new MatchDate(2017, Calendar.NOVEMBER, 12, 17),
            new MatchDate(2017, Calendar.NOVEMBER, 19),
            new MatchDate(2017, Calendar.NOVEMBER, 26),
            new MatchDate(2017, Calendar.DECEMBER, 3),
            new MatchDate(2017, Calendar.DECEMBER, 10),
            new MatchDate(2017, Calendar.DECEMBER, 17, 17, 20, true),
            new MatchDate(2018, Calendar.JANUARY, 7),
            new MatchDate(2018, Calendar.JANUARY, 14),
            new MatchDate(2018, Calendar.JANUARY, 21),
            new MatchDate(2018, Calendar.JANUARY, 28),
            new MatchDate(2018, Calendar.FEBRUARY, 4),
            new MatchDate(2018, Calendar.FEBRUARY, 11),
            new MatchDate(2018, Calendar.FEBRUARY, 18),
            new MatchDate(2018, Calendar.FEBRUARY, 25),
            new MatchDate(2018, Calendar.MARCH, 4),
            new MatchDate(2018, Calendar.MARCH, 11),
            new MatchDate(2018, Calendar.MARCH, 18));
    
    public static List<MatchDate> getInstances()
    {
        return INSTANCES;
    }

    private final Date date;
    private final int firstHour;
    private final int matchCount;

    private MatchDate(int year, int month, int day)
    {
        this(year, month, day, 18, 21, false);
    }

    private MatchDate(int year, int month, int day, int firstHour)
    {
        this(year, month, day, firstHour, 21, false);
    }

    private MatchDate(int year, int month, int day, int firstHour, boolean odd)
    {
        this(year, month, day, firstHour, 21, odd);
    }

    private MatchDate(int year, int month, int day, int firstHour, int lastHour, boolean odd)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        this.date = DateUtils.truncate(cal, Calendar.DATE).getTime();
        this.firstHour = firstHour;
        this.matchCount = (lastHour - firstHour + 1) * 2 - (odd ? 1 : 0);
    }

    @Override
    public String toString()
    {
        return "MatchDate [date=" + date + ", firstHour=" + firstHour + ", matchCount=" + matchCount
                + "]";
    }
}
