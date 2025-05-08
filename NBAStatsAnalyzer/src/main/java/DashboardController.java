import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController {
    @FXML private TextField searchField, compareField1, compareField2;
    @FXML private ComboBox<String> sortDropdown;
    @FXML private VBox resultContainer;

    private List<Player> allPlayers;

    @FXML
    public void initialize() {
        var inputStream = getClass().getResourceAsStream("/all_seasons.csv");
        if (inputStream == null) {
            showError("CSV not found in resources.");
            return;
        }

        CSVImporter importer = new CSVImporter(inputStream);
        allPlayers = importer.readAllData();
        System.out.println("Loaded " + allPlayers.size() + " players.");

        sortDropdown.getItems().addAll("Sort by", "Points", "Rebounds", "Assists");
        sortDropdown.getSelectionModel().selectFirst();

        showWelcomeMessage();
    }

    private void showError(String message) {
        resultContainer.getChildren().clear();
        Text error = new Text(message);
        error.getStyleClass().add("player-name");
        resultContainer.getChildren().add(error);
    }

    private void showWelcomeMessage() {
        resultContainer.getChildren().clear();
        VBox box = createPlayerBox("🏀 Welcome NBA Fans", List.of(
                "→ Search by player name or team",
                "→ Sort to rank players by stats",
                "→ Compare players side-by-side",
                "→ Click 'Apply' to get started!",
                "→ We made this app to help you learn more about the NBA. Thank you for sharing it with us."
        ));
        resultContainer.getChildren().add(box);
    }

    @FXML
    public void handleApply() {
        String query = searchField.getText().trim().toLowerCase();
        String sortOption = sortDropdown.getSelectionModel().getSelectedItem();

        List<Player> filtered = allPlayers.stream()
                .filter(p -> p.getName().toLowerCase().contains(query)
                        || p.getTeam().toLowerCase().contains(query))
                .collect(Collectors.toList());

        switch (sortOption) {
            case "Points"   -> filtered.sort(Comparator.comparingDouble(p -> -p.getStats().getOrDefault("Points", 0.0)));
            case "Rebounds" -> filtered.sort(Comparator.comparingDouble(p -> -p.getStats().getOrDefault("Rebounds", 0.0)));
            case "Assists"  -> filtered.sort(Comparator.comparingDouble(p -> -p.getStats().getOrDefault("Assists", 0.0)));
        }

        resultContainer.getChildren().clear();
        if (filtered.isEmpty()) {
            showError("No matching players found.");
        } else {
            filtered.stream().limit(100).forEach(player -> {
                VBox box = createPlayerBox(player.getName(), List.of(
                        "Season: " + player.getSeason() + " | Team: " + player.getTeam(),
                        "PPG: " + format(player, "Points") + "   RPG: " + format(player, "Rebounds") + "   APG: " + format(player, "Assists")
                ));
                resultContainer.getChildren().add(box);
            });
        }
    }

    @FXML
    public void handleCompare() {
        String name1 = compareField1.getText().trim().toLowerCase();
        String name2 = compareField2.getText().trim().toLowerCase();
        compareField1.clear();
        compareField2.clear();

        Player p1 = findTopPlayer(name1);
        Player p2 = findTopPlayer(name2);

        resultContainer.getChildren().clear();
        if (p1 == null || p2 == null) {
            showError("One or both players not found.");
            return;
        }

        resultContainer.getChildren().add(createPlayerBox("📊 " + p1.getName(), List.of(
                "Season: " + p1.getSeason() + " | Team: " + p1.getTeam(),
                "PPG: " + format(p1, "Points") + "   RPG: " + format(p1, "Rebounds") + "   APG: " + format(p1, "Assists")
        )));

        resultContainer.getChildren().add(createPlayerBox("📊 " + p2.getName(), List.of(
                "Season: " + p2.getSeason() + " | Team: " + p2.getTeam(),
                "PPG: " + format(p2, "Points") + "   RPG: " + format(p2, "Rebounds") + "   APG: " + format(p2, "Assists")
        )));

        resultContainer.getChildren().add(createPlayerBox("🏆 Stat Leaders", List.of(
                "PPG: " + compareStat(p1, p2, "Points"),
                "RPG: " + compareStat(p1, p2, "Rebounds"),
                "APG: " + compareStat(p1, p2, "Assists")
        )));
    }

    @FXML
    private VBox createPlayerBox(String title, List<String> lines) {
        VBox box = new VBox();
        box.getStyleClass().add("player-box");
        box.setPadding(new Insets(15));
        box.setSpacing(6);

        Label name = new Label(title);
        name.getStyleClass().add("player-name");

        box.getChildren().add(name);

        for (String line : lines) {
            Label stat = new Label(line);
            stat.getStyleClass().add("player-stats");
            box.getChildren().add(stat);
        }
        return box;
    }

    private Player findTopPlayer(String query) {
        return allPlayers.stream()
                .filter(p -> p.getName().toLowerCase().contains(query))
                .max(Comparator.comparingDouble(p -> p.getStats().getOrDefault("Points", 0.0)))
                .orElse(null);
    }

    private String compareStat(Player p1, Player p2, String key) {
        double s1 = p1.getStats().getOrDefault(key, 0.0);
        double s2 = p2.getStats().getOrDefault(key, 0.0);
        return String.format("%s (%.1f) vs. %s (%.1f)",
                s1 >= s2 ? p1.getName() : p2.getName(), Math.max(s1, s2),
                s1 < s2 ? p1.getName() : p2.getName(), Math.min(s1, s2));
    }

    private String format(Player p, String stat) {
        return String.format("%.1f", p.getStats().getOrDefault(stat, 0.0));
    }
}
