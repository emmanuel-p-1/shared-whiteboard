package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface ISession extends Remote {
  List<Action> receiveActions() throws RemoteException;

  void sendActions(ArrayList<Action> actions) throws RemoteException;

  boolean isAdmin() throws RemoteException;

  List<String> getSessions() throws RemoteException;
}
