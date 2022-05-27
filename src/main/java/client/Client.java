package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import remote.Action;
import server.Server;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Client extends Application {
  static ArrayList<Action> recentActions = new ArrayList<>();
  static Whiteboard wb;
  static UserPane userPane;
  private Server server;

  Connection connection;

  protected void startGUI(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    wb = new Whiteboard();
    userPane = new UserPane();
    Setup setup = new Setup();

    primaryStage.setTitle("Whiteboard Application");

    HBox root = new HBox();

    root.getChildren().add(wb.getToolbox());
    root.getChildren().add(wb.getCanvas());
    root.getChildren().add(userPane.createUserPane());

    Scene main = new Scene(root);

    primaryStage.setScene(setup.getScene());
    primaryStage.show();

    setup.getCreate().setOnAction(e -> {
      try {
        startConnection(setup.getUsername(), setup.getServerName());
        primaryStage.setScene(main);
        primaryStage.show();
        primaryStage.centerOnScreen();
      } catch (AlreadyBoundException | RemoteException | UnknownHostException ex) {
        // Unhandled Exception
        ex.printStackTrace();
      }
    });

    setup.getConnect().setOnAction(e -> {
      joinConnection(setup.getUsername(), setup.getServerName(), setup.getAddress());
      primaryStage.setScene(main);
      primaryStage.show();
      primaryStage.centerOnScreen();
    });

    wb.getCanvas().setOnMouseDragged(wb::draw);
    wb.getCanvas().setOnMousePressed(wb::click);
    wb.getCanvas().setOnMouseReleased(wb::release);
  }

  private void startConnection(String username, String serverName) throws AlreadyBoundException, RemoteException, UnknownHostException {
    server = new Server(serverName);
    server.run(username);
    connection = new Connection(username, serverName, Inet4Address.getLocalHost().getHostAddress());
    connection.start();
  }

  private void joinConnection(String username, String serverName, String address) {
    connection = new Connection(username, serverName, address);
    connection.start();
  }
}
