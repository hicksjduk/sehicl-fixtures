package uk.org.sehicl.fixtures;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

public class MatchDate
{
    private static List<MatchDate> INSTANCES = Arrays.asList(
            new MatchDate.Builder(2017, Calendar.SEPTEMBER, 17).setFirstHour(20).build(),
            new MatchDate.Builder(2017, Calendar.SEPTEMBER, 24).setFirstHour(20).build(),
            new MatchDate.Builder(2017, Calendar.OCTOBER, 1).setFirstHour(17).build(),
            new MatchDate.Builder(2017, Calendar.OCTOBER, 15)
                    .setFirstHour(17)
                    .build(),
            new MatchDate.Builder(2017, Calendar.OCTOBER, 22)
                    .setFirstHour(17)
                    .build(),
            new MatchDate.Builder(2017, Calendar.OCTOBER, 29)
                    .setFirstHour(17)
                    .build(),
            new MatchDate.Builder(2017, Calendar.NOVEMBER, 5).setFirstHour(17).build(),
            new MatchDate.Builder(2017, Calendar.NOVEMBER, 12)
                    .setFirstHour(17)
                    .build(),
            new MatchDate.Builder(2017, Calendar.NOVEMBER, 19).build(),
            new MatchDate.Builder(2017, Calendar.NOVEMBER, 26).build(),
            new MatchDate.Builder(2017, Calendar.DECEMBER, 3).build(),
            new MatchDate.Builder(2017, Calendar.DECEMBER, 10).build(),
            new MatchDate.Builder(2017, Calendar.DECEMBER, 17)
                    .setFirstHour(17)
                    .setLastHour(20)
                    .setOdd(true)
                    .build(),
            new MatchDate.Builder(2018, Calendar.JANUARY, 7).build(),
            new MatchDate.Builder(2018, Calendar.JANUARY, 14).build(),
            new MatchDate.Builder(2018, Calendar.JANUARY, 21).build(),
            new MatchDate.Builder(2018, Calendar.JANUARY, 28).build(),
            new MatchDate.Builder(2018, Calendar.FEBRUARY, 4).build(),
            new MatchDate.Builder(2018, Calendar.FEBRUARY, 11).build(),
            new MatchDate.Builder(2018, Calendar.FEBRUARY, 18).build(),
            new MatchDate.Builder(2018, Calendar.FEBRUARY, 25).build(),
            new MatchDate.Builder(2018, Calendar.MARCH, 4).build(),
            new MatchDate.Builder(2018, Calendar.MARCH, 11).build(),
            new MatchDate.Builder(2018, Calendar.MARCH, 18).build());

    public static List<MatchDate> getInstances()
    {
        return INSTANCES;
    }

    private final Date date;
    private final int firstHour;
    private final int mins;
    private final int matchCount;

    private MatchDate(Builder builder)
    {
        this.date = builder.date;
        this.firstHour = builder.firstHour;
        this.mins = builder.mins;
        this.matchCount = (builder.lastHour - firstHour + 1) * 2 - (builder.odd ? 1 : 0);
    }

    @Override
    public String toString()
    {
        return "MatchDate [date=" + date + ", firstHour=" + firstHour + ", matchCount=" + matchCount
                + "]";
    }

    public Date getDate()
    {
        return date;
    }

    public int getFirstHour()
    {
        return firstHour;
    }

    public int getMins()
    {
        return mins;
    }

    public int getMatchCount()
    {
        return matchCount;
    }

    public static class Builder
    {
        private final Date date;
        private int firstHour = 18;
        private int lastHour = 21;
        private int mins = 15;
        private boolean odd = false;

        public Builder(int year, int month, int day)
        {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            this.date = DateUtils.truncate(cal, Calendar.DATE).getTime();
        }

        public MatchDate build()
        {
            return new MatchDate(this);
        }

        public Builder setFirstHour(int firstHour)
        {
            this.firstHour = firstHour;
            return this;
        }

        public Builder setLastHour(int lastHour)
        {
            this.lastHour = lastHour;
            return this;
        }

        public Builder setMins(int mins)
        {
            this.mins = mins;
            return this;
        }

        public Builder setOdd(boolean odd)
        {
            this.odd = odd;
            return this;
        }
    }
}
