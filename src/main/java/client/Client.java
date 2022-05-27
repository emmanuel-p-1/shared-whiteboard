package client;

import client.whiteboard.Whiteboard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import remote.Action;
import server.Server;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Client extends Application {
  public static ArrayList<Action> recentActions = new ArrayList<>();
  static Whiteboard wb;
  static UserPane userPane;
  private Server server;

  private static Stage primaryStage;

  private Connection connection;

  protected void startGUI(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Client.primaryStage = primaryStage;

    wb = new Whiteboard();
    userPane = new UserPane();
    Setup setup = new Setup();

    primaryStage.setTitle("Whiteboard Application");

    HBox root = new HBox();

    Scene main = new Scene(root);

    primaryStage.setScene(setup.getSelectScene());
    primaryStage.show();

    setup.getCreate().setOnAction(e -> {
      primaryStage.setScene(setup.getCreateScene());
      primaryStage.show();
      primaryStage.centerOnScreen();
    });

    setup.getCreateServer().setOnAction(e -> {
      try {
        startConnection(setup.getUsername(), setup.getServerName(), setup.getPort());
      } catch (AlreadyBoundException | RemoteException | UnknownHostException ex) {
        // Unhandled Exception
        ex.printStackTrace();
      }

      root.getChildren().add(wb.getAdminToolbox());
      root.getChildren().add(wb.getCanvas());
      root.getChildren().add(userPane.getUserPane());

      primaryStage.setScene(main);
      primaryStage.show();
      primaryStage.centerOnScreen();
    });

    setup.getConnect().setOnAction(e -> {
      primaryStage.setScene(setup.getConnectServerScene());
      primaryStage.show();
      primaryStage.centerOnScreen();
    });

    setup.getConnectServer().setOnAction(e -> {
      try {
        Registry registry = LocateRegistry.getRegistry(setup.getAddress(), setup.getPort());
        for (String reg : registry.list()) {
          Button btn = new Button(reg);
          setup.getConnectSelectPane().getChildren().add(btn);
          btn.setOnAction(ev -> {
            setup.setServerName(reg);

            primaryStage.setScene(setup.getConnectScene());
            primaryStage.show();
            primaryStage.centerOnScreen();
          });
        }
      } catch (RemoteException ev) {
        // Unhandled Exception
        ev.printStackTrace();
      }

      primaryStage.setScene(setup.getConnectSelectScene());
      primaryStage.show();
      primaryStage.centerOnScreen();
    });

    setup.getJoinServer().setOnAction(e -> {
      joinConnection(setup.getUsername(), setup.getServerName(), setup.getAddress(), setup.getPort());

      root.getChildren().add(wb.getToolbox());
      root.getChildren().add(wb.getCanvas());
      root.getChildren().add(userPane.getUserPane());

      primaryStage.setScene(main);
      primaryStage.show();
      primaryStage.centerOnScreen();
    });

    wb.getCanvas().setOnMouseDragged(wb::draw);
    wb.getCanvas().setOnMousePressed(wb::click);
    wb.getCanvas().setOnMouseReleased(wb::release);
  }

  private void startConnection(String username, String serverName, int port) throws AlreadyBoundException, RemoteException, UnknownHostException {
    server = new Server(serverName, port);
    server.run(username);
    connection = new Connection(username, serverName, Inet4Address.getLocalHost().getHostAddress(), port);
    connection.start();
  }

  private void joinConnection(String username, String serverName, String address, int port) {
    connection = new Connection(username, serverName, address, port);
    connection.start();
  }

  public static Stage getStage() {
    return primaryStage;
  }

//  public static void addUsers(List<String> users) {
//    GridPane userGrid = userPane.getUsers();
//    for (int i = 0; i < users.size(); i++) {
//      userGrid.add(new Label(users.get(i)), 0, i);
//      userGrid.add(new Button("X"), 1, i);
//    }
//  }

  @Override
  public void stop() throws Exception {
    connection.closeConnection();
    server.closeRegistry();
//    Platform.exit();
//    System.exit(0);
  }
}
