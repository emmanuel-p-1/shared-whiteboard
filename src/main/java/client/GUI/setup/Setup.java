package client.GUI.setup;

import client.Client;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Setup {
  private final VBox networkSelectPane = new VBox(10);
  private final Scene networkSelectScene = new Scene(networkSelectPane, 400, 600);
  private final Button joinLocalButton = new Button("Use Local Host");
  private final Button joinRemoteButton = new Button("Join Remote Host");

  private final VBox localServerPane = new VBox(10);
  private final Scene localServerScene = new Scene(localServerPane, 400, 600);
  private final Button localServerButton = new Button("Connect");

  private final VBox remoteServerPane = new VBox(10);
  private final Scene remoteServerScene = new Scene(remoteServerPane, 400, 600);
  private final Button remoteServerButton = new Button("Connect");

  private final VBox selectNamePane = new VBox(10);
  private final Scene selectNameScene = new Scene(selectNamePane, 400, 600);
  private final Button selectNameButton = new Button("Add New");

  private final VBox newServerPane = new VBox(10);
  private final Scene newServerScene = new Scene(newServerPane, 400, 600);
  private final Button newServerButton = new Button("Create");

  private final VBox usernamePane = new VBox(10);
  private final Scene usernameScene = new Scene(usernamePane, 400, 600);
  private final Button joinServer = new Button("Join");

  private final VBox waitingPane = new VBox(10);
  private final Scene waitingScene = new Scene(waitingPane, 400, 600);
  private final TextField waitingText = new TextField("Waiting for approval...");

  private final TextField username = new TextField();
  private final HBox userBox = new HBox(new Label("Username: "), username);
  private final TextField serverName = new TextField();
  private final HBox serverBox = new HBox(new Label("Whiteboard Name: "), serverName);
  private final TextField address = new TextField();
  private final HBox addressBox = new HBox(new Label("IP Address: "), address);
  private final TextField port = new TextField();
  private final HBox portBox = new HBox(new Label("Port: "), port);

  private final Client client;
  private boolean newServer = false;

  public Setup(Client client) {
    this.client = client;
  }

  public void startup() {
    networkSelectPane.getChildren().addAll(joinLocalButton, joinRemoteButton, Client.getError());

    networkSelectPane.setAlignment(Pos.CENTER);
    joinLocalButton.setMaxWidth(400);
    joinRemoteButton.setMaxWidth(400);

    client.getStage().setScene(networkSelectScene);
    client.getStage().show();
    client.getStage().centerOnScreen();
  }

  public void onJoinLocalHost() {
    joinLocalButton.setOnAction(e -> {
      Client.setError("");
      try {
        address.setText(Inet4Address.getLocalHost().getHostAddress());
      } catch (UnknownHostException ex) {
        Client.setError("Local host address not found");
      }

      localServerPane.getChildren().addAll(portBox, localServerButton, Client.getError());

      HBox.setHgrow(port, Priority.ALWAYS);
      portBox.setMaxWidth(300);

      localServerPane.setAlignment(Pos.CENTER);
      localServerButton.setMaxWidth(400);

      client.getStage().setScene(localServerScene);
      client.getStage().show();
    });
  }

  public void onJoinRemoteHost() {
    joinRemoteButton.setOnAction(e -> {
      Client.setError("");
      remoteServerPane.getChildren().addAll(addressBox, portBox, remoteServerButton, Client.getError());

      HBox.setHgrow(address, Priority.ALWAYS);
      HBox.setHgrow(port, Priority.ALWAYS);
      portBox.setMaxWidth(300);
      addressBox.setMaxWidth(300);

      remoteServerPane.setAlignment(Pos.CENTER);
      remoteServerButton.setMaxWidth(400);

      client.getStage().setScene(remoteServerScene);
      client.getStage().show();
    });
  }

  public void onRegistrySelect() {
    localServerButton.setOnAction(e -> {
      Client.setError("");
      showRegistry();
    });

    remoteServerButton.setOnAction(e -> {
      Client.setError("");
      showRegistry();
    });
  }

  public void showRegistry() {
    try {
      Registry registry = LocateRegistry.getRegistry(getAddress(), getPort());
      for (String reg : registry.list()) {
        Button btn = new Button(reg);
        selectNamePane.getChildren().add(btn);
        btn.setMaxWidth(400);

        btn.setOnAction(ev -> {
          newServer = false;
          setServerName(reg);
          onUserCreate();
        });
      }
    } catch (RemoteException ev) {
      Client.setError("Empty Registry");
    }

    selectNamePane.getChildren().addAll(Client.getError(), selectNameButton);

    selectNamePane.setAlignment(Pos.CENTER);
    selectNameButton.setMaxWidth(400);

    client.getStage().setScene(selectNameScene);
    client.getStage().show();
  }

  public void onNewRegistry() {
    selectNameButton.setOnAction(e -> {
      Client.setError("");
      newServer = true;

      newServerPane.getChildren().addAll(serverBox, newServerButton, Client.getError());

      HBox.setHgrow(serverName, Priority.ALWAYS);
      serverBox.setMaxWidth(300);

      newServerPane.setAlignment(Pos.CENTER);
      newServerButton.setMaxWidth(400);

      client.getStage().setScene(newServerScene);
      client.getStage().show();
    });
  }

  public void onNewServer() {
    newServerButton.setOnAction(e -> {
      Client.setError("");
      onUserCreate();
    });
  }

  public void onUserCreate() {
    Client.setError("");
    usernamePane.getChildren().addAll(userBox, joinServer, Client.getError());

    HBox.setHgrow(username, Priority.ALWAYS);
    userBox.setMaxWidth(300);

    usernamePane.setAlignment(Pos.CENTER);
    joinServer.setMaxWidth(400);

    client.getStage().setScene(usernameScene);
    client.getStage().show();
  }

  public void onJoinServer() {
    joinServer.setOnAction(e -> {
      Client.setError("");
      if (newServer) {
        try {
          client.startConnection(getUsername(), getServerName(), getAddress(), getPort());
          client.getRoot().getChildren().add(client.getWhiteboard().getAdminToolbox());
        } catch (AlreadyBoundException ex) {
          Client.setError("Server name taken");
        } catch (UnknownHostException ex) {
          Client.setError("Host address not found");
        } catch (RemoteException ex) {
          Client.setError(ex.getMessage());
        }
      } else {
        client.joinConnection(getUsername(), getServerName(), getAddress(), getPort());
        client.getRoot().getChildren().add(client.getWhiteboard().getToolbox());
      }

      client.getRoot().getChildren().add(client.getWhiteboard().getCanvas());
      client.getRoot().getChildren().add(client.getUserPane().getUserPane());

      client.getUserPane().sendMessage();
      client.getUserPane().onDisconnect();

      waitingText.setDisable(true);
      waitingText.setBackground(Background.EMPTY);
      waitingText.setAlignment(Pos.CENTER);

      waitingPane.getChildren().addAll(waitingText, Client.getError());
      waitingPane.setAlignment(Pos.CENTER);

      client.getStage().setScene(waitingScene);
      client.getStage().show();
    });
  }

  public void setServerName(String name) {
    serverName.setText(name);
  }

  public String getUsername() {
    return username.getText();
  }

  public String getServerName() {
    return serverName.getText();
  }

  public String getAddress() {
    return address.getText();
  }

  public int getPort() {
    return Integer.parseInt(port.getText());
  }
}
