package uk.org.sehicl.fixtures;

public class Team
{
    private final String name;

    public Team(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return "Team [name=" + name + "]";
    }
}
