package server.rInstance;

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

public class Session extends UnicastRemoteObject implements ISession, Unreferenced {
  private final Data data;

  private final ArrayList<Action> actions;
  private final ArrayList<Message> messages;

  private int actionIndex = 0;
  private int messageIndex = 0;

  private final String username;
  private final boolean isAdmin;

  protected Session(String username, boolean isAdmin, Data data) throws RemoteException {
    data.addSession(this);

    actions = new ArrayList<>();
    messages = new ArrayList<>();

    this.username = username;
    this.isAdmin = isAdmin;

    this.data = data;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public void unreferenced() {
    try {
      close();
      data.removeSession(this);
      unexportObject(this, true);
    } catch (RemoteException e) {
      // Unhandled Exception
      e.printStackTrace();
    }
  }

  @Override
  public ArrayList<Action> receiveActions() throws RemoteException {
    ArrayList<Action> allActions = data.getActions();

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
      if (isAdmin && action.getTool() == null) data.addAction(action);
      if (action.getTool() != null) data.addAction(action);
    }
  }

  @Override
  public boolean isAdmin() throws RemoteException {
    return isAdmin;
  }

  @Override
  public ArrayList<String> getSessions() throws RemoteException {
    return data.getUsernames();
  }

  @Override
  public void logout() throws RemoteException {
    close();
    data.removeSession(this);
    unexportObject(this, true);
  }

  @Override
  public ArrayList<Message> getMessages() throws RemoteException {
    ArrayList<Message> allMessages = data.getMessages();

    List<Message> subList = new ArrayList<>(allMessages.subList(messageIndex, allMessages.size()));
    ArrayList<Message> unsentMessages = new ArrayList<>(subList);

    unsentMessages.removeAll(messages);
    messages.addAll(subList);
    messageIndex += subList.size();

    return unsentMessages;
  }

  @Override
  public void sendMessage(Message message) throws RemoteException {
    data.addMessage(message);
  }

  @Override
  public void kick(String username) throws RemoteException {
    if (!isAdmin) return;

    ArrayList<Session> sessions = data.getSessions();

    for (Session session : sessions) {
      if (session.username.equals(username)) {
        session.logout();
        return;
      }
    }
  }

  private void close() throws RemoteException {
    if (!isAdmin) return;

    ArrayList<Session> sessions = data.getSessions();

    Iterator<Session> is = sessions.iterator();
    while (is.hasNext()) {
      Session s = is.next();
      if (!s.isAdmin) s.logout();
    }
  }
}
