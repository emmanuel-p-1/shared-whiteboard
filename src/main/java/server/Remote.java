package server;

import remote.Action;
import remote.RemoteInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Remote extends UnicastRemoteObject implements RemoteInterface {
  private final ArrayList<Action> actions;

  protected Remote() throws RemoteException {
    actions = new ArrayList<>();
  }

  @Override
  public ArrayList<Action> receiveActions() throws RemoteException {
    return actions;
  }

  @Override
  public void sendActions(ArrayList<Action> actions) throws RemoteException {
    this.actions.addAll(actions);
  }
}
