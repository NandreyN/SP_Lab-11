package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(
                getClass().getResource("styles.css").toExternalForm());
        primaryStage.show();
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) throws InterruptedException {
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null && splash.isVisible()) {
            Thread.sleep(1000);
            splash.close();
        }
        launch(args);
    }
}
