import java.util.HashMap;
import java.util.Map;

public class Player {
    private final String name;
    private final String season;
    private final String team;
    private final Map<String, Double> stats;

    public Player(String name, String season, String team) {
        this.name = name;
        this.season = season;
        this.team = team;
        this.stats = new HashMap<>();
    }

    public String getName() { return name; }
    public String getSeason() { return season; }
    public String getTeam() { return team; }
    public Map<String, Double> getStats() { return stats; }
    public void setStat(String statName, double value) {
        stats.put(statName, value);
    }
}