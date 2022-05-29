package client.GUI.users;

import client.Client;
import javafx.beans.binding.Bindings;
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

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * GUI component for user management/interaction.
 *
 */

public class UserPane {
  // Shows connection.
  private final Label label = new Label();
  // Pane for all components.
  private final VBox userPane = new VBox(10);
  // Chat input.
  private final TextField input = new TextField();
  // Chat output.
  private final static TextArea output = new TextArea();
  // User list.
  private final ListView<String> users = new ListView<>();
  // Waiting list.
  private final ListView<String> waiting = new ListView<>();
  // Disconnect button.
  private final Button disconnect = new Button("DISCONNECT");

  private final Client client;  // Client driver.

  // Creates right pane for connected users.
  public UserPane(Client client) {
    this.client = client;

    output.setPrefHeight(500);
    output.setEditable(false);
    output.setFocusTraversable(false);

    users.setPrefHeight(200);
    users.setFocusTraversable(false);

    waiting.setPrefHeight(48);
    waiting.setMaxHeight(150);

    disconnect.setMaxWidth(Double.MAX_VALUE);
    disconnect.setAlignment(Pos.CENTER);

    // Add to pane.
    userPane.getChildren().add(label);
    userPane.getChildren().add(users);
    userPane.getChildren().add(waiting);
    userPane.getChildren().add(output);
    userPane.getChildren().add(input);
    userPane.getChildren().add(disconnect);
  }

  // Add exceptions/errors to output
  public static void appendOutput(String s) {
    output.appendText("ERROR: " + s + "\n");
  }

  // Get component pane.
  public VBox getUserPane() {
    if (client.getConnection() != null) {
      String name = client.getConnection().getServerName();
      String address = client.getConnection().getAddress();
      String port = String.valueOf(client.getConnection().getPort());

      label.setText("Connected to: " + name + " on " + address + ":" + port);
    }

    return userPane;
  }

  // Update connected user list.
  public void updateUsers(List<String> userList) {
    users.setItems(null);
    if (userList == null || userList.isEmpty()) return;
    ObservableList<String> list = FXCollections.observableArrayList(userList);
    users.setItems(list);

    // Create buttons/format list.
    users.setCellFactory(stringListView -> {
      try {
        return new UserCell(client);
      } catch (RemoteException e) {
        appendOutput("Error communicating with server");
      }
      return null;
    });
  }

  // Update waiting user list.
  public void updateWaiting(List<String> userList) {
    waiting.setItems(null);
    if (userList == null || userList.isEmpty()) return;
    ObservableList<String> list = FXCollections.observableArrayList(userList);
    waiting.setItems(list);
    waiting.prefHeightProperty().bind(Bindings.size(list).multiply(48));

    // Create buttons/format list.
    waiting.setCellFactory(stringListView -> {
      try {
        return new WaitCell(client);
      } catch (RemoteException e) {
        appendOutput("Error communicating with server");
      }
      return null;
    });
  }

  // Send chat message.
  public void sendMessage() {
    input.setOnAction(e -> {
      Message message = new Message(client.getConnection().getUsername(), input.getText());
      try {
        client.getConnection().getRemote().sendMessage(message);
      } catch (RemoteException ex) {
        appendOutput("Error communicating with server");
      }
      input.clear();
    });
  }

  // Get messages for server.
  public void processMessages(ArrayList<Message> messages) {
    messages.forEach(m -> {
      output.appendText("[" + m.getUsername() + "] " + m.getMessage() + "\n");
    });
  }

  // Disconnect on disconnect.
  public void onDisconnect() {
    disconnect.setOnAction(e -> {
      client.restart();
    });
  }

  // users cell format (with button to kick).
  private static class UserCell extends ListCell<String> {
    HBox kick = new HBox(), noKick = new HBox();
    Label label1 = new Label(), label2 = new Label();
    Pane pane1 = new Pane(), pane2 = new Pane();
    Button button = new Button("X");
    Client client;

    // Create cell for users.
    public UserCell(Client client) throws RemoteException {
      super();
      this.client = client;

      kick.getChildren().addAll(label1, pane1, button);
      noKick.getChildren().addAll(label2, pane2);

      HBox.setHgrow(pane1, Priority.ALWAYS);
      HBox.setHgrow(pane2, Priority.ALWAYS);

      button.setOnAction(event -> {
        try {
          client.getConnection().getRemote().kick(getItem());
        } catch (RemoteException e) {
          appendOutput("Error communicating with server");
        }
      });
    }

    // Update cell contents.
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
              setGraphic(noKick);
            }
          } else {
            setGraphic(noKick);
          }
        } catch (RemoteException e) {
          appendOutput("Error communicating with server");
        }
      }
    }
  }

  // waiting user cell format (with approve/reject).
  private static class WaitCell extends ListCell<String> {
    HBox box = new HBox();
    Label label = new Label();
    Pane pane = new Pane();
    Button approve = new Button("Approve"), reject = new Button("Reject");
    Client client;

    // Create cell for waiting users.
    public WaitCell(Client client) throws RemoteException {
      super();
      this.client = client;

      box.getChildren().addAll(label, pane, approve, reject);

      HBox.setHgrow(pane, Priority.ALWAYS);

      approve.setOnAction(event -> {
        try {
          client.getConnection().getRemote().approve(getItem());
        } catch (RemoteException e) {
          appendOutput("Error communicating with server");
        }
      });
      reject.setOnAction(event -> {
        try {
          client.getConnection().getRemote().reject(getItem());
        } catch (RemoteException e) {
          appendOutput("Error communicating with server");
        }
      });
    }

    // Update cell contents.
    @Override
    protected void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);
      setText(null);
      setGraphic(null);

      if (item != null && !empty) {
        label.setText(item);

        try {
          if (client.getConnection().getRemote().isAdmin()) {
            setGraphic(box);
          }
        } catch (RemoteException e) {
          appendOutput("Error communicating with server");
        }
      }
    }
  }
}
