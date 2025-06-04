import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CSVImporterTest {
    @Test
    public void readAllData_parsesPlayers() {
        String csv = "season,player,team,pts,trb,ast\n" +
                "2022-23,LeBron James,LAL,28.9,8.3,6.8\n" +
                "2022-23,Stephen Curry,GSW,29.4,6.1,6.3\n";
        InputStream in = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        CSVImporter importer = new CSVImporter(in);
        List<Player> players = importer.readAllData();
        assertEquals(2, players.size());
        Player p1 = players.get(0);
        assertEquals("LeBron James", p1.getName());
        assertEquals(28.9, p1.getStats().get("Points"));
    }
}
