import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {
    @FXML private TextField searchField, compareField1, compareField2;
    @FXML private ComboBox<String> sortDropdown;
    @FXML private TextArea resultArea;

    private boolean darkMode = false;
    private List<Player> allPlayers;

    @FXML
    public void initialize() {
        System.out.println("✅ DashboardController initialized.");
        // Load CSV from resources
        var inputStream = getClass().getResourceAsStream("/all_seasons.csv");
        if (inputStream == null) {
            resultArea.setText("❌ CSV not found in resources!");
            return;
        }

        CSVImporter importer = new CSVImporter(inputStream);
        allPlayers = importer.readAllData();
        System.out.println("✔ Loaded " + allPlayers.size() + " players.");

        sortDropdown.getItems().addAll("Sort by", "Points", "Rebounds", "Assists");
        sortDropdown.getSelectionModel().selectFirst();
        showWelcomeMessage();
    }

    @FXML
    public void handleApply() {
        updateResults();
    }

    @FXML
    public void handleCompare() {
        String name1 = compareField1.getText().trim().toLowerCase();
        String name2 = compareField2.getText().trim().toLowerCase();

        Player p1 = findTopPlayer(name1);
        Player p2 = findTopPlayer(name2);
        if (p1 == null || p2 == null) {
            resultArea.setText("⚠ One or both players not found.");
            return;
        }

        resultArea.setText("📊 Player Comparison\n\n");
        resultArea.appendText(String.format("%-25s Season: %-8s | Team: %-4s\n",
                p1.getName(), p1.getSeason(), p1.getTeam()));
        resultArea.appendText(String.format("PPG: %-5.1f | RPG: %-5.1f | APG: %-5.1f\n\n",
                p1.getStats().get("Points"),
                p1.getStats().get("Rebounds"),
                p1.getStats().get("Assists")));

        resultArea.appendText(String.format("%-25s Season: %-8s | Team: %-4s\n",
                p2.getName(), p2.getSeason(), p2.getTeam()));
        resultArea.appendText(String.format("PPG: %-5.1f | RPG: %-5.1f | APG: %-5.1f\n\n",
                p2.getStats().get("Points"),
                p2.getStats().get("Rebounds"),
                p2.getStats().get("Assists")));

        resultArea.appendText("🏆 Stat Leaders:\n");
        resultArea.appendText("PPG: " + compareStat(p1, p2, "Points") + "\n");
        resultArea.appendText("RPG: " + compareStat(p1, p2, "Rebounds") + "\n");
        resultArea.appendText("APG: " + compareStat(p1, p2, "Assists") + "\n");

        compareField1.clear();
        compareField2.clear();
    }

    @FXML
    public void handleToggleTheme() {
        darkMode = !darkMode;
        String bg = darkMode ? "#1e1e1e" : "#ffffff";
        String fg = darkMode ? "#ffffff" : "#000000";
        resultArea.setStyle(String.format(
                "-fx-control-inner-background: %s; -fx-text-fill: %s;", bg, fg));
    }

    private void updateResults() {
        String query = searchField.getText().trim().toLowerCase();
        String sortOption = sortDropdown.getSelectionModel().getSelectedItem();

        List<Player> filtered = allPlayers.stream()
                .filter(p -> p.getName().toLowerCase().contains(query)
                        || p.getTeam().toLowerCase().contains(query))
                .collect(Collectors.toList());

        if (sortOption != null) {
            switch (sortOption) {
                case "Points"   -> filtered.sort(Comparator.comparingDouble(p -> -p.getStats().getOrDefault("Points", 0.0)));
                case "Rebounds" -> filtered.sort(Comparator.comparingDouble(p -> -p.getStats().getOrDefault("Rebounds", 0.0)));
                case "Assists"  -> filtered.sort(Comparator.comparingDouble(p -> -p.getStats().getOrDefault("Assists", 0.0)));
            }
        }

        if (filtered.isEmpty()) {
            resultArea.setText("😕 No matching players found.");
        } else {
            resultArea.setText(String.format("%-25s %-10s %-6s %-6s %-6s\n",
                    "Name", "Season", "PPG", "RPG", "APG"));
            resultArea.appendText("=".repeat(65) + "\n");
            for (int i = 0; i < Math.min(filtered.size(), 100); i++) {
                Player p = filtered.get(i);
                resultArea.appendText(String.format("%-25s %-10s %-6.1f %-6.1f %-6.1f\n",
                        p.getName(), p.getSeason(),
                        p.getStats().getOrDefault("Points", 0.0),
                        p.getStats().getOrDefault("Rebounds", 0.0),
                        p.getStats().getOrDefault("Assists", 0.0)));
            }
        }
    }

    private Player findTopPlayer(String nameQuery) {
        return allPlayers.stream()
                .filter(p -> p.getName().toLowerCase().contains(nameQuery))
                .max(Comparator.comparingDouble(p -> p.getStats().getOrDefault("Points", 0.0)))
                .orElse(null);
    }

    private String compareStat(Player p1, Player p2, String key) {
        double s1 = p1.getStats().getOrDefault(key, 0.0);
        double s2 = p2.getStats().getOrDefault(key, 0.0);
        return s1 > s2 ? p1.getName() : s2 > s1 ? p2.getName() : "Tie";
    }

    private void showWelcomeMessage() {
        resultArea.setText(
                "🏀 Welcome to the NBA Statistics Analyzer!\n\n" +
                        "→ Search by player name or team\n" +
                        "→ Use sort dropdown to rank players by stats\n" +
                        "→ Use the bottom panel to compare players side-by-side\n\n" +
                        "Click 'Apply' to get started!"
        );
    }
}