package fri.sparovcek;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Sparovcek extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        fri.sparovcek.SceneController.openLoginScene(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}