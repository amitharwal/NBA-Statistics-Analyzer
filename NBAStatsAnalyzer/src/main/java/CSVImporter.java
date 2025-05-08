import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVImporter {
    private final InputStream csvStream;

    public CSVImporter(InputStream csvStream) {
        this.csvStream = csvStream;
    }

    public List<Player> readAllData() {
        List<Player> players = new ArrayList<>();
        int totalRows = 0;
        int malformedRows = 0;
        int missingFieldRows = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.err.println("CSVImporter: Empty CSV file.");
                return players;
            }

            String[] headers = headerLine.split(",", -1);
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                String h = headers[i].trim().toLowerCase();
                if ((h.equals("season") || h.contains("season")) && !idx.containsKey("season"))
                    idx.put("season", i);
                if ((h.equals("player_name") || h.equals("player") || h.contains("player")) && !idx.containsKey("player"))
                    idx.put("player", i);
                if ((h.equals("team_abbreviation") || h.equals("team_id") || h.equals("team") || h.contains("team"))
                        && !idx.containsKey("team"))
                    idx.put("team", i);
                if ((h.equals("pts") || h.contains("point")) && !idx.containsKey("points"))
                    idx.put("points", i);
                if ((h.equals("trb") || h.contains("reb")) && !idx.containsKey("rebounds"))
                    idx.put("rebounds", i);
                if ((h.equals("ast") || h.contains("ast")) && !idx.containsKey("assists"))
                    idx.put("assists", i);
            }

            if (!idx.keySet().containsAll(Set.of("season", "player", "team", "points", "rebounds", "assists"))) {
                System.err.println("CSVImporter: Missing required columns. Found only: " + idx.keySet());
                return players;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                totalRows++;
                String[] vals = line.split(",", -1);
                if (vals.length < headers.length) {
                    malformedRows++;
                    continue;
                }

                String season = vals[idx.get("season")].trim();
                String name   = vals[idx.get("player")].trim();
                String team   = vals[idx.get("team")].trim();
                if (season.isEmpty() || name.isEmpty()) {
                    missingFieldRows++;
                    continue;
                }

                Player p = new Player(name, season, team);
                try {
                    double pts = Double.parseDouble(vals[idx.get("points")]);
                    double reb = Double.parseDouble(vals[idx.get("rebounds")]);
                    double ast = Double.parseDouble(vals[idx.get("assists")]);
                    p.setStat("Points", pts);
                    p.setStat("Rebounds", reb);
                    p.setStat("Assists", ast);
                } catch (NumberFormatException ex) {
                    malformedRows++;
                    continue;
                }
                players.add(p);
            }

            System.out.printf("CSVImporter: Read %d rows. Parsed %d players. Skipped %d malformed, %d missing-field rows.%n",
                    totalRows, players.size(), malformedRows, missingFieldRows);

        } catch (IOException e) {
            System.err.println("CSVImporter: Error reading CSV file.");
            e.printStackTrace();
        }
        return players;
    }
}
