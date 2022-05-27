package server;

import remote.Action;
import remote.ISession;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.ArrayList;
import java.util.List;

class Session extends UnicastRemoteObject implements ISession, Unreferenced {
  private static final ArrayList<Action> allActions = new ArrayList<>();
  private static final ArrayList<Session> sessions = new ArrayList<>();

  private final ArrayList<Action> actions;
  private int index = 0;

  private final String username;
  private final boolean isAdmin;

  protected Session(String username, boolean isAdmin) throws RemoteException {
    sessions.add(this);
    actions = new ArrayList<>();
    this.username = username;
    this.isAdmin = isAdmin;
  }

  @Override
  public void unreferenced() {
    try {
      unexportObject(this, true);
    } catch (NoSuchObjectException e) {
      // Unhandled Exception
      e.printStackTrace();
    }
  }

  @Override
  public ArrayList<Action> receiveActions() throws RemoteException {
    // TODO: Turn into synchronized
    List<Action> subList = new ArrayList<>(allActions.subList(index, allActions.size()));
    ArrayList<Action> unperformedActions = new ArrayList<>(subList);

    unperformedActions.removeAll(actions);
    actions.addAll(subList);
    index += subList.size();

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
    unexportObject(this, true);
  }
}
