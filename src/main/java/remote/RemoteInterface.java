package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RemoteInterface extends Remote {
  ArrayList<Action> receiveActions() throws RemoteException;

  void sendActions(ArrayList<Action> actions) throws RemoteException;
}
