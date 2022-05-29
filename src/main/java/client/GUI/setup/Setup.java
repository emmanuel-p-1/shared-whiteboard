package client.GUI.setup;

import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.Effect;
import javafx.scene.layout.*;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Setups the JavaFX Stage.
 *
 */

public class Setup {
  // Screen 1 Elements (address/port connection).
  private final VBox connectPane = new VBox(10);
  private final Scene connectScene = new Scene(connectPane, 400, 600);
  private final Button connectButton = new Button("Connect");

  // Screen 2 Elements (registry selection).
  private final VBox serverPane = new VBox(10);
  private final Scene serverScene = new Scene(serverPane, 400, 600);
  private final Button serverButton = new Button("Create");

  // Screen 3 Elements (user creation).
  private final VBox userPane = new VBox(10);
  private final Scene userScene = new Scene(userPane, 400, 600);
  private final Button userButton = new Button("Join");

  // Screen 4 Elements (waiting for approval).
  private final VBox waitingPane = new VBox(10);
  private final Scene waitingScene = new Scene(waitingPane, 400, 600);
  private final TextField waitingText = new TextField(
          "Waiting for approval...");

  // TextFields that collect information required for connection.
  private final TextField username = new TextField();
  private final GridPane userBox = new GridPane();
  private final TextField serverName = new TextField();
  private final GridPane serverBox = new GridPane();
  private final TextField address = new TextField();
  private final GridPane addressBox = new GridPane();
  private final TextField port = new TextField();
  private final GridPane portBox = new GridPane();

  // Reset screen to start.
  private final Button reset = new Button("reset");
  private final Region resetRegion = new Region();
  private final Region upperRegion = new Region();

  private final Client client;        // Client driver.
  private boolean newServer = false;  // Will user create a new server.

  // Initialise setup elements.
  public Setup(Client client) {
    this.client = client;

    // Add labels to TextFields. i.e. Label: [ TextField ]
    ColumnConstraints col1 = new ColumnConstraints();
    col1.setPercentWidth(30);
    userBox.getColumnConstraints().add(0, col1);
    serverBox.getColumnConstraints().add(0, col1);
    addressBox.getColumnConstraints().add(0, col1);
    portBox.getColumnConstraints().add(0, col1);

    ColumnConstraints col2 = new ColumnConstraints();
    col2.setPercentWidth(60);
    userBox.getColumnConstraints().add(1, col2);
    serverBox.getColumnConstraints().add(1, col2);
    addressBox.getColumnConstraints().add(1, col2);
    portBox.getColumnConstraints().add(1, col2);

    // Put labels and TextFields into their GridPanes.
    userBox.add(new Label("Username: "), 0, 0);
    userBox.add(username, 1, 0);
    serverBox.add(new Label("Whiteboard Name: "), 0, 0);
    serverBox.add(serverName, 1, 0);
    addressBox.add(new Label("IP Address: "), 0, 0);
    addressBox.add(address, 1, 0);
    portBox.add(new Label("Port: "), 0, 0);
    portBox.add(port, 1, 0);

    userBox.setAlignment(Pos.CENTER);
    serverBox.setAlignment(Pos.CENTER);
    addressBox.setAlignment(Pos.CENTER);
    portBox.setAlignment(Pos.CENTER);

    // Reset to start.
    reset.setOnAction(e -> {
      Client.setError("");
      client.restart();
    });

    VBox.setVgrow(resetRegion, Priority.ALWAYS);
    VBox.setVgrow(upperRegion, Priority.ALWAYS);
  }

  // Screen 1 - address/port connection.
  public void startup() {
    connectPane.getChildren().addAll(addressBox, portBox, connectButton,
            Client.getError());

    connectPane.setAlignment(Pos.CENTER);
    connectButton.setMaxWidth(400);

    client.getStage().setScene(connectScene);
    client.getStage().show();
    client.getStage().centerOnScreen();
  }

  // Screen 2 - registry selection.
  public void onConnect() {
    connectButton.setOnAction(e -> {
      Client.setError("");

      try {
        Registry registry = LocateRegistry.getRegistry(getAddress(),
                getPort());
        List<String> rList = Arrays.asList(registry.list());
        ObservableList<String> list = FXCollections.observableList(rList);
        ListView<String> lv = new ListView<>(list);
        lv.setOnMouseClicked(ev -> {
          newServer = false;
          setServerName(lv.getSelectionModel().getSelectedItem());
          userCreate();
        });
        serverPane.getChildren().add(lv);
      } catch (RemoteException ev) {
        Client.setError("Empty Registry");
      }

      serverPane.getChildren().addAll(upperRegion, serverBox, serverButton,
              Client.getError(), resetRegion, reset);

      serverPane.setAlignment(Pos.CENTER);
      serverButton.setMaxWidth(400);

      client.getStage().setScene(serverScene);
      client.getStage().show();
    });
  }

  // Screen 2.5 - New user creation with new server creation.
  public void onServerButton() {
    serverButton.setOnAction(e -> {
      try {
        Registry registry = LocateRegistry.getRegistry(getAddress(),
                getPort());
        List<String> rList = Arrays.asList(registry.list());
        if (rList.contains(serverName.getText())) {
          Client.setError("Name in use");
          return;
        }
      } catch (RemoteException ev) {
        Client.setError("Empty Registry");
      }

      newServer = true;
      userCreate();
    });
  }

  // Screen 3 - New user creation.
  private void userCreate() {
    Client.setError("");

    userPane.getChildren().addAll(upperRegion, userBox, userButton,
            Client.getError(), resetRegion, reset);

    HBox.setHgrow(username, Priority.ALWAYS);
    userBox.setMaxWidth(300);

    userPane.setAlignment(Pos.CENTER);
    userButton.setMaxWidth(400);

    client.getStage().setScene(userScene);
    client.getStage().show();
  }

  // Complete connection.
  public void onJoinServer() {
    userButton.setOnAction(e -> {
      Client.setError("");

      // Depending on choice, create or join server.
      if (newServer) {
        try {
          client.startConnection(getUsername(), getServerName(), getAddress(),
                  getPort());
          client.getRoot().getChildren().add(
                  client.getWhiteboard().getAdminToolbox());
        } catch (AlreadyBoundException ex) {
          Client.setError("Server name taken");
          return;
        } catch (UnknownHostException ex) {
          Client.setError("Host address not found");
          return;
        } catch (RemoteException ex) {
          Client.setError("Error communicating with server");
          return;
        } catch (NotBoundException ex) {
          Client.setError("Server not bound");
          return;
        } catch (LoginException ex) {
          Client.setError("Username taken");
          return;
        }
      } else {
        try {
          client.joinConnection(getUsername(), getServerName(), getAddress(),
                  getPort());
        } catch (NotBoundException ex) {
          Client.setError("Server not bound");
          return;
        } catch (LoginException ex) {
          Client.setError("Username taken");
          return;
        } catch (RemoteException ex) {
          Client.setError("Error communicating with server");
          return;
        }
        client.getRoot().getChildren().add(
                client.getWhiteboard().getToolbox());
      }

      client.getRoot().getChildren().add(client.getWhiteboard().getCanvas());
      client.getRoot().getChildren().add(client.getUserPane().getUserPane());

      // Enable messaging and disconnecting.
      client.getUserPane().sendMessage();
      client.getUserPane().onDisconnect();

      // Wait for approval.
      waitingText.setDisable(true);
      waitingText.setBackground(Background.EMPTY);
      waitingText.setAlignment(Pos.CENTER);

      waitingPane.getChildren().addAll(upperRegion, waitingText,
              Client.getError(), resetRegion, reset);
      waitingPane.setAlignment(Pos.CENTER);

      client.getStage().setScene(waitingScene);
      client.getStage().show();
    });
  }

  // Set to specified server name.
  private void setServerName(String name) {
    serverName.setText(name);
  }

  // Get specified username.
  private String getUsername() {
    return username.getText();
  }

  // Get specified server name.
  private String getServerName() {
    return serverName.getText();
  }

  // Get specified address name.
  private String getAddress() {
    return address.getText();
  }

  // Get specified port.
  private int getPort() {
    return Integer.parseInt(port.getText());
  }
}
