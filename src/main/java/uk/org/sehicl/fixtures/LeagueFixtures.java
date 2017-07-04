package uk.org.sehicl.fixtures;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LeagueFixtures
{
    private final static Map<String, LeagueFixtures> INSTANCES = Stream
            .of(new LeagueFixtures("D1", 6, 2), new LeagueFixtures("D2", 6, 2),
                    new LeagueFixtures("D3", 10, 1), new LeagueFixtures("D4", 10, 1),
                    new LeagueFixtures("D5", 10, 1))
            .collect(Collectors.toMap(LeagueFixtures::getLeague, lfl -> lfl));

    public static LeagueFixtures getLeagueList(String league)
    {
        return INSTANCES.get(league);
    }

    public static Collection<LeagueFixtures> getLeagueLists()
    {
        return INSTANCES.values();
    }

    private final String league;
    private final List<Team> teams = new ArrayList<>();
    private final List<Match> fixtures = new ArrayList<>();

    private LeagueFixtures(String league, int teamCount, int fixtureCount)
    {
        this.league = league;
        generateTeams(teamCount);
        generateFixtures(fixtureCount);
    }

    private void generateTeams(int teamCount)
    {
        IntStream.rangeClosed(1, teamCount).forEach(
                i -> teams.add(new Team(String.format("%s/%d", league, i))));
        Collections.shuffle(teams);
    }

    private void generateFixtures(int fixtureCount)
    {
        int teamCount = teams.size();
        Deque<Team> polygon = new ArrayDeque<>(
                teamCount % 2 == 1 ? teams : teams.subList(0, teamCount - 1));
        Team extra = polygon.size() == teamCount ? null : teams.get(teamCount - 1);
        IntStream.range(0, fixtureCount).forEach(f -> generateFixtures(polygon, extra, f % 2 == 1));
    }

    private void generateFixtures(Deque<Team> polygon, Team extra, boolean reverseHa)
    {
        IntStream.range(0, polygon.size()).forEach(round ->
        {
            generateRound(round, polygon, extra, reverseHa);
            polygon.add(polygon.pop());
        });
    }

    private void generateRound(int round, Deque<Team> polygon, Team extra, boolean reverseHa)
    {
        List<Match> matchesInRound = new ArrayList<>();
        Deque<Team> teamsInRound = new ArrayDeque<>(polygon);
        Team lastTeam = teamsInRound.removeLast();
        while (teamsInRound.size() > 1)
        {
            Team t1 = teamsInRound.removeFirst();
            Team t2 = teamsInRound.removeLast();
            boolean t1AtHome = (matchesInRound.size() % 2 == 1) == reverseHa;
            matchesInRound.add(t1AtHome ? new Match(t1, t2) : new Match(t2, t1));
        }
        if (extra != null)
        {
            boolean extraAtHome = (round % 2 == 0) == reverseHa;
            Match m = extraAtHome ? new Match(extra, lastTeam) : new Match(lastTeam, extra);
            matchesInRound.add(matchesInRound.size() / 2, m);
        }
        fixtures.addAll(matchesInRound);
    }

    public String getLeague()
    {
        return league;
    }

    public List<Team> getTeams()
    {
        return teams;
    }

    public List<Match> getFixtures()
    {
        return fixtures;
    }

    @Override
    public String toString()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println();
        pw.println(league);
        pw.println();
        fixtures.stream().forEach(pw::println);
        return sw.toString();
    }
}
