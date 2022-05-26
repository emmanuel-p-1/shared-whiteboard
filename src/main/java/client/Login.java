package client;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Login {
  private final Scene scene;
  private final TextField username = new TextField();
  private final TextField serverName = new TextField();
  private final TextField address = new TextField();
  private final Button create = new Button("Create Server");
  private final Button connect = new Button("Connect to Server");

  Login() {
    VBox layout = new VBox(10);
    scene = new Scene(layout, 400, 600);

    layout.getChildren().add(username);
    layout.getChildren().add(serverName);
    layout.getChildren().add(address);
    layout.getChildren().add(create);
    layout.getChildren().add(connect);

    layout.setAlignment(Pos.CENTER);
    create.setMaxWidth(400);
    connect.setMaxWidth(400);
    username.setPromptText("Username");
    serverName.setPromptText("Server Name");
    address.setPromptText("IP Address");
  }

  Scene getScene() {
    return scene;
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

  Button getCreate() {
     return create;
  }

  Button getConnect() {
    return connect;
  }
}
