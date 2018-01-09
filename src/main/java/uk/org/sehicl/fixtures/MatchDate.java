package uk.org.sehicl.fixtures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

public class MatchDate
{
    private static List<MatchDate> INSTANCES = Arrays.asList(
            new MatchDate.Builder(2018, Calendar.SEPTEMBER, 30).setLastHour(20).build(),
            new MatchDate.Builder(2018, Calendar.OCTOBER, 14).build(),
            new MatchDate.Builder(2018, Calendar.OCTOBER, 21).build(),
            new MatchDate.Builder(2018, Calendar.OCTOBER, 28).build(),
            new MatchDate.Builder(2018, Calendar.NOVEMBER, 6).build(),
            new MatchDate.Builder(2018, Calendar.NOVEMBER, 13).build(),
            new MatchDate.Builder(2018, Calendar.NOVEMBER, 20).build(),
            new MatchDate.Builder(2018, Calendar.NOVEMBER, 27).build(),
            new MatchDate.Builder(2018, Calendar.DECEMBER, 2)
                    .setFirstHour(17)
                    .setOddHour(17)
                    .build(),
            new MatchDate.Builder(2018, Calendar.DECEMBER, 9)
                    .setFirstHour(17)
                    .build(),
            new MatchDate.Builder(2018, Calendar.DECEMBER, 16)
                    .setFirstHour(17)
                    .build(),
            new MatchDate.Builder(2019, Calendar.JANUARY, 6).build(),
            new MatchDate.Builder(2019, Calendar.JANUARY, 13).build(),
            new MatchDate.Builder(2019, Calendar.JANUARY, 20).build(),
            new MatchDate.Builder(2019, Calendar.JANUARY, 27).build(),
            new MatchDate.Builder(2019, Calendar.FEBRUARY, 3).build(),
            new MatchDate.Builder(2019, Calendar.FEBRUARY, 10).build(),
            new MatchDate.Builder(2019, Calendar.FEBRUARY, 17).build(),
            new MatchDate.Builder(2019, Calendar.FEBRUARY, 24).build(),
            new MatchDate.Builder(2019, Calendar.MARCH, 3).build(),
            new MatchDate.Builder(2019, Calendar.MARCH, 10).build(),
            new MatchDate.Builder(2019, Calendar.MARCH, 17).build(),
            new MatchDate.Builder(2019, Calendar.MARCH, 24).build(),
            new MatchDate.Builder(2019, Calendar.MARCH, 31).build());

    public static List<MatchDate> getInstances()
    {
        return INSTANCES;
    }

    private final Date date;
    private final List<Integer> hours = new ArrayList<>();
    private final int mins;

    private MatchDate(Builder builder)
    {
        this.date = builder.date;
        this.mins = builder.mins;
        for (int h = builder.firstHour; h <= builder.lastHour; h++)
        {
            hours.add(h);
            if (builder.oddHour == null || builder.oddHour != h)
                hours.add(h);
        }
    }

    public Date getDate()
    {
        return date;
    }

    public List<Integer> getHours()
    {
        return hours;
    }

    public int getMins()
    {
        return mins;
    }

    public static class Builder
    {
        private final Date date;
        private int firstHour = 18;
        private int lastHour = 21;
        private int mins = 15;
        private Integer oddHour = null;

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

        public Builder setOddHour(int oddHour)
        {
            this.oddHour = oddHour;
            return this;
        }
    }
}
