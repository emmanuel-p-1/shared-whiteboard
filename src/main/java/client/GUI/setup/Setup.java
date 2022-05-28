package client.GUI.setup;

import client.Client;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Setup {
  private final VBox selectPane = new VBox(10);
  private final Scene selectScene = new Scene(selectPane, 400, 600);
  private final Button create = new Button("Create new Server");
  private final Button connect = new Button("Connect to Server");

  private final VBox createPane = new VBox(10);
  private final Scene createScene = new Scene(createPane, 400, 600);
  private final Button createServer = new Button("Create Server");
  // Include fields for: username (admin), server name, port

  private final VBox connectServerPane = new VBox(10);
  private final Scene connectServerScene = new Scene(connectServerPane, 400, 600);
  private final Button connectServer = new Button("Connect to Server");
  // Include fields for: address, port

  private final VBox connectSelectPane = new VBox(10);
  private final Scene connectSelectScene = new Scene(connectSelectPane, 400, 600);
  // Server names appear as buttons

  private final VBox connectPane = new VBox(10);
  private final Scene connectScene = new Scene(connectPane, 400, 600);
  private final Button joinServer = new Button("Connect to Server");
  // Include fields for: username

  private final TextField username = new TextField();
  private final TextField serverName = new TextField();
  private final TextField address = new TextField();
  private final TextField port = new TextField();

  private final Client client;

  public Setup(Client client) {
    this.client = client;

    username.setPromptText("Username");
    serverName.setPromptText("Server Name");
    address.setPromptText("IP Address");
    port.setPromptText("Port");
  }

  public void startup() {
    selectPane.getChildren().addAll(create, connect);

    selectPane.setAlignment(Pos.CENTER);
    create.setMaxWidth(400);
    connect.setMaxWidth(400);

    client.getStage().setScene(selectScene);
    client.getStage().show();
  }

  public void onCreate() {
    create.setOnAction(e -> {
      createPane.getChildren().addAll(username, serverName, port, createServer);

      createPane.setAlignment(Pos.CENTER);
      createServer.setMaxWidth(400);

      client.getStage().setScene(createScene);
      client.getStage().show();
      client.getStage().centerOnScreen();
    });
  }

  public void onLaunch() {
    createServer.setOnAction(e -> {
      try {
        client.startConnection(getUsername(), getServerName(), getPort());
      } catch (AlreadyBoundException | RemoteException | UnknownHostException ex) {
        // Unhandled Exception
        ex.printStackTrace();
      }

      client.getRoot().getChildren().add(client.getWhiteboard().getAdminToolbox());
      client.getRoot().getChildren().add(client.getWhiteboard().getCanvas());
      client.getRoot().getChildren().add(client.getUserPane().getUserPane());

      client.getUserPane().sendMessage();
      client.getUserPane().onDisconnect();

      client.getStage().setScene(client.getMain());
      client.getStage().show();
      client.getStage().centerOnScreen();
    });
  }

  public void onConnect() {
    connect.setOnAction(e -> {
      connectServerPane.getChildren().addAll(address, port, connectServer);

      connectServerPane.setAlignment(Pos.CENTER);
      connectServer.setMaxWidth(400);

      client.getStage().setScene(connectServerScene);
      client.getStage().show();
      client.getStage().centerOnScreen();
    });
  }

  public void onServerSelect() {
    connectServer.setOnAction(e -> {
      try {
        Registry registry = LocateRegistry.getRegistry(getAddress(), getPort());
        for (String reg : registry.list()) {
          Button btn = new Button(reg);
          connectSelectPane.getChildren().add(btn);
          btn.setMaxWidth(400);

          btn.setOnAction(ev -> {
            setServerName(reg);

            connectPane.getChildren().addAll(username, joinServer);

            connectPane.setAlignment(Pos.CENTER);
            joinServer.setMaxWidth(400);

            client.getStage().setScene(connectScene);
            client.getStage().show();
            client.getStage().centerOnScreen();
          });
        }
      } catch (RemoteException ev) {
        // Unhandled Exception
        ev.printStackTrace();
      }

      connectSelectPane.setAlignment(Pos.CENTER);

      client.getStage().setScene(connectSelectScene);
      client.getStage().show();
      client.getStage().centerOnScreen();
    });
  }

  public void onJoin() {
    joinServer.setOnAction(e -> {
      client.joinConnection(getUsername(), getServerName(), getAddress(), getPort());

      client.getRoot().getChildren().add(client.getWhiteboard().getToolbox());
      client.getRoot().getChildren().add(client.getWhiteboard().getCanvas());
      client.getRoot().getChildren().add(client.getUserPane().getUserPane());

      client.getUserPane().sendMessage();

      client.getStage().setScene(client.getMain());
      client.getStage().show();
      client.getStage().centerOnScreen();
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
