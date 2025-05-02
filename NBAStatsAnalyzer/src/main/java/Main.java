import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1000, 700);

            var css = getClass().getResource("/light.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stage.setTitle("NBA Statistics Analyzer");
            stage.getIcons().add(new Image("https://d1csarkz8obe9u.cloudfront.net/posterpreviews/nba-logo-design-template-84ea26dc6256bc0ceed9603b95ba6ab9_screen.jpg?ts=1684801676"));

            stage.setScene(scene);
            stage.show();

            FadeTransition ft = new FadeTransition(Duration.seconds(1.2), root);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
