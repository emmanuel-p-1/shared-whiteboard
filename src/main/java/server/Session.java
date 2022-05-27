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
  private static ArrayList<Action> allActions;
  private final ArrayList<Action> actions;
  private int index;

  protected Session() throws RemoteException {
    actions = new ArrayList<>();
    allActions = new ArrayList<>();
  }

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
    List<Action> subList = new ArrayList<>(allActions.subList(index, allActions.size()));
    ArrayList<Action> unperformedActions = new ArrayList<>(subList);

    unperformedActions.removeAll(actions);
    actions.addAll(subList);
    index += subList.size();

    return unperformedActions;
  }

  @Override
  public void sendActions(ArrayList<Action> actions) throws RemoteException {
    allActions.addAll(actions);
  }
}
