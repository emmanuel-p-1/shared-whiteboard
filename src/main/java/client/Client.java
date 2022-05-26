package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import remote.Action;
import server.Server;

import java.util.ArrayList;

public class Client extends Application {
  static ArrayList<Action> actions = new ArrayList<>();
  static Whiteboard wb;
  static UserPane userPane;

  Connection connection;

  protected void startGUI(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    wb = new Whiteboard();
    userPane = new UserPane();

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

    startConnection();
  }

  private void startConnection() {
    Server.run();
    connection = new Connection();
    connection.start();
  }
}
