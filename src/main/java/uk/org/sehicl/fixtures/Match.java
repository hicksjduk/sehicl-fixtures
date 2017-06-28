package uk.org.sehicl.fixtures;

public class Match
{
    private final Team homeTeam;
    private final Team awayTeam;

    public Match(Team homeTeam, Team awayTeam)
    {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public Team getHomeTeam()
    {
        return homeTeam;
    }

    public Team getAwayTeam()
    {
        return awayTeam;
    }
    
    public String toString()
    {
        return String.format("%s v %s", homeTeam.getName(), awayTeam.getName());
    }
}
