package client;

import client.GUI.setup.Setup;
import client.GUI.users.UserPane;
import client.GUI.whiteboard.Whiteboard;
import client.connection.Connection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import remote.serializable.Action;
import server.Server;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Client extends Application {
  public static ArrayList<Action> recentActions = new ArrayList<>();

  // UI sections
  private Whiteboard wb;
  private UserPane userPane;

  // Stage components
  private Stage primaryStage;
  private HBox root;
  private Scene main;

  private Server server;
  private Connection connection;

  public void setRecentActions(ArrayList<Action> actions) {
    recentActions = actions;
  }

  protected void startGUI(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    initialise();
  }

  private void initialise() {
    wb = new Whiteboard(this);
    userPane = new UserPane(this);

    Setup setup = new Setup(this);

    primaryStage.setTitle("Whiteboard Application");

    root = new HBox();
    main = new Scene(root);

    setup.startup();

    setup.onCreate();
    setup.onLaunch();

    setup.onConnect();
    setup.onServerSelect();
    setup.onJoin();

    wb.getCanvas().setOnMouseDragged(wb::draw);
    wb.getCanvas().setOnMousePressed(wb::click);
    wb.getCanvas().setOnMouseReleased(wb::release);
  }

  public void startConnection(String username, String serverName, int port) throws AlreadyBoundException, RemoteException, UnknownHostException {
    server = new Server(serverName, port);
    server.run(username);
    connection = new Connection(this, username, serverName, Inet4Address.getLocalHost().getHostAddress(), port);
    connection.start();
  }

  public void joinConnection(String username, String serverName, String address, int port) {
    connection = new Connection(this, username, serverName, address, port);
    connection.start();
  }

  public Stage getStage() {
    return primaryStage;
  }

  public HBox getRoot() {
    return root;
  }

  public Scene getMain() {
    return main;
  }

  public Whiteboard getWhiteboard() {
    return wb;
  }

  public UserPane getUserPane() {
    return userPane;
  }

  public ArrayList<Action> getRecentActions() {
    return recentActions;
  }

  public Connection getConnection() {
    return connection;
  }

  public void addUsers(List<String> users) {
    Platform.runLater(() -> {
      userPane.updateUsers(users);
    });
  }

  public void restart() {
    connection = null;
    Platform.runLater(this::initialise);
  }

  @Override
  public void stop() throws Exception {
    if (connection != null) {
      connection.closeConnection();
    }
    if (server != null) {
      server.closeConnection();
    }
  }
}
