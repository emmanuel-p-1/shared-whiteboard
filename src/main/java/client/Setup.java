package client;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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

  Setup() {
    username.setPromptText("Username");
    serverName.setPromptText("Server Name");
    address.setPromptText("IP Address");
    port.setPromptText("Port");
  }

  Scene getSelectScene() {
    selectPane.getChildren().addAll(create, connect);

    selectPane.setAlignment(Pos.CENTER);
    create.setMaxWidth(400);
    connect.setMaxWidth(400);

    return selectScene;
  }

  Button getCreate() {
    return create;
  }

  Button getConnect() {
    return connect;
  }

  Scene getCreateScene() {
    createPane.getChildren().addAll(username, serverName, port, createServer);

    createPane.setAlignment(Pos.CENTER);
    createServer.setMaxWidth(400);

    return createScene;
  }

  Button getCreateServer() {
    return createServer;
  }

  Scene getConnectServerScene() {
    connectServerPane.getChildren().addAll(address, port, connectServer);

    connectServerPane.setAlignment(Pos.CENTER);
    connectServer.setMaxWidth(400);

    return connectServerScene;
  }

  Button getConnectServer() {
    return connectServer;
  }

  VBox getConnectSelectPane() {
    return connectSelectPane;
  }

  void setServerName(String name) {
    serverName.setText(name);
  }

  Scene getConnectSelectScene() {
    connectSelectPane.setAlignment(Pos.CENTER);
    return connectSelectScene;
  }

  Scene getConnectScene() {
    connectPane.getChildren().addAll(username, joinServer);

    connectPane.setAlignment(Pos.CENTER);
    joinServer.setMaxWidth(400);

    return connectScene;
  }

  Button getJoinServer() {
    return joinServer;
  }

  String getUsername() {
    return username.getText();
  }

  String getServerName() {
    return serverName.getText();
  }

  String getAddress() {
    return address.getText();
  }

  int getPort() {
    return Integer.parseInt(port.getText());
  }
}
