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

import javax.security.auth.login.LoginException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Main GUI driver.
 *
 */

public class Client extends Application {
  private static ArrayList<Action> recentActions = new ArrayList<>();

  // UI sections
  private Whiteboard wb;
  private UserPane userPane;

  // Stage components
  private Stage primaryStage;
  private HBox root;
  private Scene main;

  // Connection components
  private Server server;          // Server instance.
  private Connection connection;  // Connection to server.

  private static TextField error; // To view exception messages

  // Concurrency lock
  private static final ReadWriteLock lock = new ReentrantReadWriteLock();

  // Get exception message
  public static TextField getError() {
    return error;
  }

  // Set exception message
  public static void setError(String s) {
    error.setText(s);
  }

  // Get local actions.
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

  // Clear local actions.
  public void clearRecentActions() {
    lock.writeLock().lock();
    try {
      recentActions = new ArrayList<>();
    } finally {
      lock.writeLock().unlock();
    }
  }

  // Record actions made locally.
  public static void addAction(Action action) {
    lock.writeLock().lock();
    try {
      recentActions.add(action);
    } finally {
      lock.writeLock().unlock();
    }
  }

  // Start program.
  void startGUI(String[] args) {
    launch(args);
  }

  // JavaFX GUI start.
  @Override
  public void start(Stage primaryStage) {
    error = new TextField();

    this.primaryStage = primaryStage;
    initialise();
  }

  // Initialise first GUI panel.
  private void initialise() {
    wb = new Whiteboard(this);
    userPane = new UserPane(this);

    Setup setup = new Setup(this);

    primaryStage.setTitle("Whiteboard Application");

    root = new HBox();
    main = new Scene(root);

    // Actions
    setup.startup();
    setup.onConnect();
    setup.onServerButton();
    setup.onJoinServer();

    wb.getCanvas().setOnMouseDragged(wb::draw);
    wb.getCanvas().setOnMousePressed(wb::click);
    wb.getCanvas().setOnMouseReleased(wb::release);

    error.setDisable(true);
    error.setBackground(Background.EMPTY);
    error.setAlignment(Pos.CENTER);
  }

  // Start and join server.
  public void startConnection(
          String username,
          String serverName,
          String address,
          int port
  ) throws AlreadyBoundException, RemoteException, UnknownHostException,
          NotBoundException, LoginException {
    server = new Server(serverName, address, port);
    server.run(username);
    connection = new Connection(this, username, serverName,
            Inet4Address.getLocalHost().getHostAddress(), port);
    connection.start();
  }

  // Join remote.
  public void joinConnection(
          String username,
          String serverName,
          String address,
          int port) throws NotBoundException, LoginException, RemoteException {
    connection = new Connection(this, username, serverName, address, port);
    connection.start();
  }

  // Get stage.
  public Stage getStage() {
    return primaryStage;
  }

  // Get main panel.
  public HBox getRoot() {
    return root;
  }

  // Get scene.
  public Scene getMain() {
    return main;
  }

  // Get whiteboard component.
  public Whiteboard getWhiteboard() {
    return wb;
  }

  // Get user GUI component.
  public UserPane getUserPane() {
    return userPane;
  }

  // Get connection to remote.
  public Connection getConnection() {
    return connection;
  }

  // Update user list in UserPane.
  public synchronized void addUsers(List<String> users) {
    Platform.runLater(() -> {
      userPane.updateUsers(users);
    });
  }

  // Update waiting list in UserPane.
  public synchronized void addWaiting(List<String> waiting) {
    Platform.runLater(() -> {
      userPane.updateWaiting(waiting);
    });
  }

  // For users allowed access to whiteboard.
  public void approved() {
    Platform.runLater(() -> {
      getStage().setScene(getMain());
      getStage().show();
      getStage().centerOnScreen();
    });
  }

  // Reset all components and start from start.
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

  // Disconnect when GUI is closed.
  @Override
  public void stop() {
    try {
      closeConnection();
    } catch (RemoteException e) {
      setError("Error communicating to server");
    } catch (NotBoundException e) {
      setError("Registry not bound");
    }
    Platform.exit();
    System.exit(0);
  }

  // Close any connections.
  private synchronized void closeConnection() throws RemoteException,
          NotBoundException {
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
