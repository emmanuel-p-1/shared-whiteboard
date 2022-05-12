package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Client extends Application {
  private final Whiteboard wb = new Whiteboard();
  private final UserPane userPane = new UserPane();

  public void startGUI(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    wb.addTools();

    primaryStage.setTitle("Whiteboard application");

    HBox root = new HBox();

    root.getChildren().add(wb.getToolbox());
    root.getChildren().add(wb.getCanvas());
    root.getChildren().add(userPane.createUserPane());

    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();

    wb.getCanvas().setOnMouseDragged(wb::draw);
  }
}
