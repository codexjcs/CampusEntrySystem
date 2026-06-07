package com.campus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage  stage;
    private static String currentStudentId;

    public static String getCurrentStudentId()        { return currentStudentId; }
    public static void   setCurrentStudentId(String s){ currentStudentId = s; }

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            stage.setTitle("Campus Entry & Exit Monitoring System");
            stage.setResizable(true);
            stage.setMinWidth(400);
            stage.setMinHeight(300);

            // ── Initialize DB tables on first run ────────────────────────────
            DatabaseInitializer.initialize();

            switchScene("role-selection.fxml");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchScene(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/campus/" + fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                MainApp.class.getResource("/com/campus/style.css").toExternalForm());
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    public static Stage getStage() { return stage; }

    public static void main(String[] args) { launch(args); }
}
