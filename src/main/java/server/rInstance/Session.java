package server.rInstance;

import remote.serializable.Action;
import remote.rInterface.ISession;
import remote.serializable.Message;
import server.Server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.ArrayList;
import java.util.List;

public class Session extends UnicastRemoteObject implements ISession, Unreferenced {
  private static ArrayList<Action> allActions = new ArrayList<>();
  private static ArrayList<Message> allMessages = new ArrayList<>();

  private static ArrayList<Session> sessions = new ArrayList<>();

  private final ArrayList<Action> actions;
  private final ArrayList<Message> messages;

  private int actionIndex = 0;
  private int messageIndex = 0;

  private final String username;
  private final boolean isAdmin;

  private final Server server;

  protected Session(String username, boolean isAdmin, Server server) throws RemoteException {
    sessions.add(this);
    actions = new ArrayList<>();
    messages = new ArrayList<>();
    this.username = username;
    this.isAdmin = isAdmin;
    this.server = server;
  }

  @Override
  public void unreferenced() {
    try {
      sessions.remove(this);
      unexportObject(this, true);
      reset();
    } catch (RemoteException e) {
      // Unhandled Exception
      e.printStackTrace();
    }
  }

  @Override
  public ArrayList<Action> receiveActions() throws RemoteException {
    // TODO: Turn into synchronized
    List<Action> subList = new ArrayList<>(allActions.subList(actionIndex, allActions.size()));
    ArrayList<Action> unperformedActions = new ArrayList<>(subList);

    unperformedActions.removeAll(actions);
    actions.addAll(subList);
    actionIndex += subList.size();

    return unperformedActions;
  }

  @Override
  public void sendActions(ArrayList<Action> actions) throws RemoteException {
    for (Action action : actions) {
      if (isAdmin && action.getTool() == null) allActions.add(action);
      if (action.getTool() != null) allActions.add(action);
    }
  }

  @Override
  public boolean isAdmin() throws RemoteException {
    return isAdmin;
  }

  @Override
  public ArrayList<String> getSessions() throws RemoteException {
    ArrayList<String> usernames = new ArrayList<>();

    sessions.forEach(s -> {
      usernames.add(s.username);
    });

    return usernames;
  }

  @Override
  public void logout() throws RemoteException {
    sessions.remove(this);
    unexportObject(this, true);
    reset();
  }

  @Override
  public ArrayList<Message> getMessages() throws RemoteException {
    // TODO: Turn into synchronized
    List<Message> subList = new ArrayList<>(allMessages.subList(messageIndex, allMessages.size()));
    ArrayList<Message> unsentMessages = new ArrayList<>(subList);

    unsentMessages.removeAll(messages);
    messages.addAll(subList);
    messageIndex += subList.size();

    return unsentMessages;
  }

  @Override
  public void sendMessage(Message message) throws RemoteException {
    // TODO: Turn into synchronized
    allMessages.add(message);
  }

  @Override
  public void kick(String username) throws RemoteException {
    if (!isAdmin) return;
    for (Session session : sessions) {
      if (session.username.equals(username)) {
        session.logout();
        return;
      }
    }
  }

  private void reset() {
    if (!isAdmin) return;
    try {
      allActions = new ArrayList<>();
      allMessages = new ArrayList<>();
      sessions = new ArrayList<>();
      server.closeConnection();
    } catch (RemoteException | NotBoundException e) {
      // Unhandled Exception
      e.printStackTrace();
    }
  }
}
