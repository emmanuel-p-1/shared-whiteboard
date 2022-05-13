package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Client extends Application {

  public void startGUI(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Whiteboard wb = new Whiteboard();
    UserPane userPane = new UserPane();

    primaryStage.setTitle("Whiteboard Application");

    HBox root = new HBox();

    root.getChildren().add(wb.getToolbox());
    root.getChildren().add(wb.getCanvas());
    root.getChildren().add(userPane.createUserPane());

    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();

    wb.getCanvas().setOnMouseDragged(wb::draw);
    wb.getCanvas().setOnMousePressed(wb::click);
    wb.getCanvas().setOnMouseReleased(wb::release);
    root.setOnKeyPressed(wb::type);
  }
}
