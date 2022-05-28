package client.GUI.users;

import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import remote.serializable.Message;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class UserPane {
  private final Label label = new Label();
  private final VBox userPane = new VBox(10);
  private final TextField input = new TextField();
  private final TextArea output = new TextArea();
  private final ListView<String> users = new ListView<>();
  private final Button disconnect = new Button("Disconnect");

  private final Client client;

  public UserPane(Client client) {
    this.client = client;

    output.setPrefHeight(600);
    output.setEditable(false);
    output.setFocusTraversable(false);

    users.setPrefHeight(300);

    disconnect.setMaxWidth(Double.MAX_VALUE);
    disconnect.setAlignment(Pos.CENTER);

    userPane.getChildren().add(label);
    userPane.getChildren().add(users);
    userPane.getChildren().add(output);
    userPane.getChildren().add(input);
    userPane.getChildren().add(disconnect);
  }

  public VBox getUserPane() {
    String name = client.getConnection().getServerName();
    String address = client.getConnection().getAddress();
    String port = String.valueOf(client.getConnection().getPort());

    label.setText("Connected to: " + name + " on " + address + ":" + port);
    return userPane;
  }

  public void updateUsers(List<String> userList) {
    users.setItems(null);
    ObservableList<String> list = FXCollections.observableArrayList(userList);
    users.setItems(list);
    users.setCellFactory(stringListView -> {
      try {
        return new UserCell(client);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
      return null;
    });
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

  public void onDisconnect() {
    disconnect.setOnAction(e -> {
      client.restart();
    });
  }

  private static class UserCell extends ListCell<String> {
    HBox kick = new HBox(), nokick = new HBox();
    Label label1 = new Label(), label2 = new Label();
    Pane pane1 = new Pane(), pane2 = new Pane();
    Button button = new Button("X");
    Client client;

    public UserCell(Client client) throws RemoteException {
      super();
      this.client = client;

      kick.getChildren().addAll(label1, pane1, button);
      nokick.getChildren().addAll(label2, pane2);

      HBox.setHgrow(pane1, Priority.ALWAYS);
      HBox.setHgrow(pane2, Priority.ALWAYS);

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
        label1.setText(item);
        label2.setText(item);

        try {
          if (client.getConnection().getRemote().isAdmin()) {
            if (!client.getConnection().getUsername().equals(item)) {
              setGraphic(kick);
            } else {
              setGraphic(nokick);
            }
          } else {
            setGraphic(nokick);
          }
        } catch (RemoteException e) {
          // Unhandled Exception
          e.printStackTrace();
        }
      }
    }
  }
}
