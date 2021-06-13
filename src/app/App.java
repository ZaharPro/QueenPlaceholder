package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final PlaceholderController controller = new PlaceholderController();

    public void start(Stage stage) {
        executor.execute(controller);
        stage.setScene(new Scene(controller.getRoot()));
        stage.setTitle("Queen Placeholder");
        stage.getIcons().add(new Image("queen.png"));
        stage.setResizable(false);
        stage.show();
    }

    public void stop() {
        controller.cancel();
        try {
            controller.get();
        } catch (Exception ignored) {
        }
        executor.shutdown();
    }
}