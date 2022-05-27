package client;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

class UserPane {
  private VBox userPane = new VBox(10);
  private TextField input = new TextField();
  private TextArea output = new TextArea();
  private GridPane users = new GridPane();

  UserPane() {
    output.setPrefHeight(600);
    output.setEditable(false);
    output.setFocusTraversable(false);

    // Set column width
    ColumnConstraints nameColumn = new ColumnConstraints();
    nameColumn.setPercentWidth(80);
    users.getColumnConstraints().add(0, nameColumn);
    ColumnConstraints kickColumn = new ColumnConstraints();
    kickColumn.setPercentWidth(20);
    users.getColumnConstraints().add(1, kickColumn);

    users.setPrefHeight(300);

    userPane.getChildren().add(users);
    userPane.getChildren().add(output);
    userPane.getChildren().add(input);
  }

  VBox getUserPane() {
    return userPane;
  }

  GridPane getUsers() {
    return users;
  }
}
