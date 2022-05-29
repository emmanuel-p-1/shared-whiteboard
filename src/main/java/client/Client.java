package client;

import client.GUI.setup.Setup;
import client.GUI.users.UserPane;
import client.GUI.whiteboard.Whiteboard;
import client.connection.Connection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Client extends Application {
  private static ArrayList<Action> recentActions = new ArrayList<>();

  // UI sections
  private Whiteboard wb;
  private UserPane userPane;

  // Stage components
  private Stage primaryStage;
  private HBox root;
  private Scene main;

  private Server server;
  private Connection connection;

  private static TextField error;

  private static final ReadWriteLock lock = new ReentrantReadWriteLock();

  public static TextField getError() {
    return error;
  }

  public static void setError(String s) {
    error.setText(s);
  }

  public static ArrayList<Action> getActions() {
    ArrayList<Action> tmp;

    lock.readLock().lock();
    try {
      tmp = new ArrayList<>(recentActions);
    } finally {
      lock.readLock().unlock();
    }
    return tmp;
  }

  public void clearRecentActions() {
    lock.writeLock().lock();
    try {
      recentActions = new ArrayList<>();
    } finally {
      lock.writeLock().unlock();
    }
  }

  public static void addAction(Action action) {
    lock.writeLock().lock();
    try {
      recentActions.add(action);
    } finally {
      lock.writeLock().unlock();
    }
  }

  protected void startGUI(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    error = new TextField();

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

    setup.onJoinLocalHost();
    setup.onJoinRemoteHost();

    setup.onRegistrySelect();
    setup.onNewRegistry();
    setup.onNewServer();

    setup.onJoinServer();

    wb.getCanvas().setOnMouseDragged(wb::draw);
    wb.getCanvas().setOnMousePressed(wb::click);
    wb.getCanvas().setOnMouseReleased(wb::release);

    error.setDisable(true);
    error.setBackground(Background.EMPTY);
    error.setAlignment(Pos.CENTER);
  }

  public void startConnection(String username, String serverName, String address, int port) throws AlreadyBoundException, RemoteException, UnknownHostException {
    server = new Server(serverName, address, port);
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

  public Connection getConnection() {
    return connection;
  }

  public void addUsers(List<String> users) {
    Platform.runLater(() -> {
      userPane.updateUsers(users);
    });
  }

  public void addWaiting(List<String> waiting) {
    Platform.runLater(() -> {
      userPane.updateWaiting(waiting);
    });
  }

  public void approved() {
    Platform.runLater(() -> {
      getStage().setScene(getMain());
      getStage().show();
      getStage().centerOnScreen();
    });
  }

  public void restart() {
    try {
      closeConnection();
    } catch (RemoteException e) {
      setError("Error communicating to server");
    } catch (NotBoundException e) {
      setError("Registry not bound");
    }
    Platform.runLater(this::initialise);
  }

  @Override
  public void stop() throws Exception {
    closeConnection();
    Platform.exit();
    System.exit(0);
  }

  private synchronized void closeConnection() throws RemoteException, NotBoundException {
    if (connection != null) {
      connection.closeConnection();
      connection = null;
    }
    if (server != null) {
      server.closeConnection();
      server = null;
    }
  }
}
