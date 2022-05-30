package server.rInstance;

import client.GUI.whiteboard.File;
import remote.serializable.Action;
import remote.rInterface.ISession;
import remote.serializable.Message;
import server.Data;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Remote object for each connection session.
 *
 */

public class Session extends UnicastRemoteObject implements ISession,
        Unreferenced {
  private final Data data;                    // Shared data.

  private final ArrayList<Action> actions;    // Received actions.
  private final ArrayList<Message> messages;  // Received messages.

  private int actionIndex = 0;                // Index for all actions.
  private int messageIndex = 0;               // Index for all messages.

  private final String username;              // Username
  private final boolean isAdmin;              // If user is admin.
  private boolean approved = false;           // If approved to join.
  private static boolean LOCK = false;        // Locked when cleared.

  // Creates session for user.
  protected Session(String username, boolean isAdmin, Data data) throws
          RemoteException {
    data.addSession(this);

    if (isAdmin) approved = true;

    actions = new ArrayList<>();
    messages = new ArrayList<>();

    this.username = username;
    this.isAdmin = isAdmin;

    this.data = data;
  }

  // Gets username
  public String getUsername() {
    return username;
  }

  // Unexport when unreferenced.
  @Override
  public void unreferenced() {
    try {
      close();
      data.removeSession(this);
      unexportObject(this, true);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  // Get unimplemented actions.
  @Override
  public ArrayList<Action> receiveActions() throws RemoteException {
    if (!approved) return null;

    ArrayList<Action> allActions = data.getActions();

    List<Action> subList = new ArrayList<>(allActions.subList(actionIndex,
            allActions.size()));
    ArrayList<Action> unperformedActions = new ArrayList<>(subList);

    unperformedActions.removeAll(actions);
    actions.addAll(subList);
    actionIndex += subList.size();

    return unperformedActions;
  }

  // Add actions to shared list.
  @Override
  public void sendActions(ArrayList<Action> actions) throws RemoteException {
    if (!approved) return;

    for (Action action : actions) {
      if (isAdmin && action.getOption() != null) {
        if (LOCK) {
          if (action.getOption().equals(File.OPEN) ||
                  action.getOption().equals(File.NEW)) {
            data.addAction(action);
            LOCK = false;
          }
        } else {
          if (action.getOption().equals(File.CLOSE)) {
            LOCK = true;
          }
          data.addAction(action);
        }
      }
      if (action.getTool() != null && !LOCK) data.addAction(action);
    }
  }

  // Get if user is admin.
  @Override
  public boolean isAdmin() throws RemoteException {
    return isAdmin;
  }

  // Get all users.
  @Override
  public ArrayList<String> getSessions() throws RemoteException {
    if (!approved) return null;

    return data.getUsernames();
  }

  // Leave connection.
  @Override
  public void logout() throws RemoteException {
    close();
    data.removeSession(this);
    unexportObject(this, true);
  }

  // Get unread messages.
  @Override
  public ArrayList<Message> getMessages() throws RemoteException {
    if (!approved) return null;

    ArrayList<Message> allMessages = data.getMessages();

    List<Message> subList = new ArrayList<>(allMessages.subList(messageIndex,
            allMessages.size()));
    ArrayList<Message> unsentMessages = new ArrayList<>(subList);

    unsentMessages.removeAll(messages);
    messages.addAll(subList);
    messageIndex += subList.size();

    return unsentMessages;
  }

  // Add messages to shared list.
  @Override
  public void sendMessage(Message message) throws RemoteException {
    if (!approved) return;

    data.addMessage(message);
  }

  // Remove connection.
  @Override
  public void kick(String username) throws RemoteException {
    if (!isAdmin || !approved) return;

    ArrayList<Session> sessions = data.getSessions();

    for (Session session : sessions) {
      if (session.username.equals(username)) {
        session.logout();
        return;
      }
    }
  }

  // Allow user to enter room.
  @Override
  public void approve(String username) throws RemoteException {
    if (!isAdmin || !approved) return;

    ArrayList<Session> sessions = data.getSessions();

    for (Session session : sessions) {
      if (session.username.equals(username)) {
        session.approved = true;
        data.approve(username);
        return;
      }
    }
  }

  // Deny user from entering room.
  @Override
  public void reject(String username) throws RemoteException {
    if (!isAdmin || !approved) return;

    ArrayList<Session> sessions = data.getSessions();

    for (Session session : sessions) {
      if (session.username.equals(username)) {
        data.reject(username);
        session.logout();
        return;
      }
    }
  }

  // Get usernames of waiting users.
  public ArrayList<String> getWaiting() throws RemoteException {
    if (!isAdmin || !approved) return null;

    return data.getWaiting();
  }

  // Check if user is approved.
  @Override
  public boolean isApproved() {
    return approved;
  }

  private void close() throws RemoteException {
    if (!isAdmin || !approved) return;

    ArrayList<Session> sessions = data.getSessions();

    Iterator<Session> is = sessions.iterator();
    while (is.hasNext()) {
      Session s = is.next();
      if (!s.isAdmin) s.logout();
    }
  }
}
