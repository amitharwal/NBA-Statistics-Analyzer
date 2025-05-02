import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import java.util.Comparator;
import java.util.List;
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
            Label errorLabel = new Label("❌ CSV not found!");
            resultContainer.getChildren().add(errorLabel);
            return;
        }

        CSVImporter importer = new CSVImporter(inputStream);
        allPlayers = importer.readAllData();
        System.out.println("Loaded " + allPlayers.size() + " players.");

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
        resultContainer.getChildren().clear();

        String name1 = compareField1.getText().trim().toLowerCase();
        String name2 = compareField2.getText().trim().toLowerCase();

        Player p1 = findTopPlayer(name1);
        Player p2 = findTopPlayer(name2);
        if (p1 == null || p2 == null) {
            resultContainer.getChildren().add(new Label("⚠ One or both players not found."));
            return;
        }

        resultContainer.getChildren().add(new Label("📊 Player Comparison"));

        resultContainer.getChildren().add(createPlayerStatsBox(p1));
        resultContainer.getChildren().add(createPlayerStatsBox(p2));

        Label title = new Label("🏆 Stat Leaders:");
        title.getStyleClass().add("section-title");
        resultContainer.getChildren().add(title);

        resultContainer.getChildren().add(new Label("PPG: " + compareStat(p1, p2, "Points")));
        resultContainer.getChildren().add(new Label("RPG: " + compareStat(p1, p2, "Rebounds")));
        resultContainer.getChildren().add(new Label("APG: " + compareStat(p1, p2, "Assists")));

        compareField1.clear();
        compareField2.clear();
    }

    @FXML
    private void updateResults() {
        resultContainer.getChildren().clear();

        String query = searchField.getText().trim().toLowerCase();
        String sortOption = sortDropdown.getSelectionModel().getSelectedItem();

        List<Player> filtered = allPlayers.stream()
                .filter(p -> p.getName().toLowerCase().contains(query)
                        || p.getTeam().toLowerCase().contains(query))
                .collect(Collectors.toList());

        if (sortOption != null) {
            switch (sortOption) {
                case "Points" -> filtered.sort(Comparator.comparingDouble(p -> -p.getStats().getOrDefault("Points", 0.0)));
                case "Rebounds" -> filtered.sort(Comparator.comparingDouble(p -> -p.getStats().getOrDefault("Rebounds", 0.0)));
                case "Assists" -> filtered.sort(Comparator.comparingDouble(p -> -p.getStats().getOrDefault("Assists", 0.0)));
            }
        }

        if (filtered.isEmpty()) {
            resultContainer.getChildren().add(new Label("😕 No matching players found."));
        } else {
            for (int i = 0; i < Math.min(filtered.size(), 100); i++) {
                Player p = filtered.get(i);
                resultContainer.getChildren().add(createPlayerStatsBox(p));
            }
        }
    }

    private VBox createPlayerStatsBox(Player p) {
        VBox box = new VBox(5);
        box.getStyleClass().add("player-box");
        box.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(p.getName() + " — " + p.getSeason() + " | " + p.getTeam());
        name.getStyleClass().add("player-name");

        Label stats = new Label(String.format("PPG: %.1f   RPG: %.1f   APG: %.1f",
                p.getStats().getOrDefault("Points", 0.0),
                p.getStats().getOrDefault("Rebounds", 0.0),
                p.getStats().getOrDefault("Assists", 0.0)));
        stats.getStyleClass().add("player-stats");

        box.getChildren().addAll(name, stats);
        return box;
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
        resultContainer.getChildren().clear();
        Label welcome = new Label("""
            🏀 Welcome to the NBA Statistics Analyzer!

            → Search by player name or team
            → Use sort dropdown to rank players by stats
            → Use the bottom panel to compare players side-by-side

            Click 'Apply' to get started!
        """);
        welcome.setWrapText(true);
        welcome.setStyle("-fx-font-size: 15px; -fx-padding: 15;");
        resultContainer.getChildren().add(welcome);
    }
}
