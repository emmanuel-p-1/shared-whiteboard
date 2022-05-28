package client.GUI.users;

import client.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import remote.serializable.Message;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class UserPane {
  private final Label label = new Label();
  private final VBox userPane = new VBox(10);
  private final TextField input = new TextField();
  private final TextArea output = new TextArea();
  private final ListView<String> users = new ListView<>();

  private Client client;

  public UserPane(Client client) {
    this.client = client;

    output.setPrefHeight(600);
    output.setEditable(false);
    output.setFocusTraversable(false);

    users.setPrefHeight(300);

    userPane.getChildren().add(label);
    userPane.getChildren().add(users);
    userPane.getChildren().add(output);
    userPane.getChildren().add(input);
  }

  public VBox getUserPane() {
    label.setText("Connected to: " + client.getConnection().getServerName());
    return userPane;
  }

  public void updateUsers(List<String> userList) {
    users.setItems(null);
    ObservableList<String> list = FXCollections.observableArrayList(userList);
    users.setItems(list);
    users.setCellFactory(stringListView -> new UserCell(client));
  }

  public void sendMessage() {
    input.setOnAction(e -> {
      Message message = new Message(client.getConnection().getUsername(), input.getText());
      try {
        client.getConnection().getRemote().sendMessage(message);
      } catch (RemoteException ex) {
        // Unhandled Exception
        ex.printStackTrace();
      }
      input.clear();
    });
  }

  public void processMessages(ArrayList<Message> messages) {
    messages.forEach(m -> {
      output.appendText("[" + m.getUsername() + "] " + m.getMessage() + "\n");
    });
  }

  private static class UserCell extends ListCell<String> {
    HBox hbox = new HBox();
    Label label = new Label();
    Pane pane = new Pane();
    Button button = new Button("X");

    public UserCell(Client client) {
      super();

      hbox.getChildren().addAll(label, pane, button);
      HBox.setHgrow(pane, Priority.ALWAYS);
      button.setOnAction(event -> {
        try {
          client.getConnection().getRemote().kick(getItem());
        } catch (RemoteException e) {
          // Unhandled Exception
          e.printStackTrace();
        }
      });
    }

    @Override
    protected void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);
      setText(null);
      setGraphic(null);

      if (item != null && !empty) {
        label.setText(item);
        setGraphic(hbox);
      }
    }
  }
}
