import java.util.List;

public class PlayerDataManager {
    private final List<Player> players;
    public PlayerDataManager(List<Player> players) {
        this.players = players;
    }
    public List<Player> getPlayers() {
        return players;
    }
}