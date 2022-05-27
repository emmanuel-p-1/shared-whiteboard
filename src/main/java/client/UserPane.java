package client;

import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

class UserPane {
  private VBox userPane = new VBox(10);
  private TextField input = new TextField();
  private static TextArea output = new TextArea();
  private static ListView<String> users = new ListView<>();

  UserPane() {
    output.setPrefHeight(600);
    output.setEditable(false);
    output.setFocusTraversable(false);

    users.setPrefHeight(300);

    userPane.getChildren().add(users);
    userPane.getChildren().add(output);
    userPane.getChildren().add(input);
  }

  VBox getUserPane() {
    return userPane;
  }

  static ListView<String> getUsers() {
    return users;
  }

  static TextArea getOutput() {
    return output;
  }
}
