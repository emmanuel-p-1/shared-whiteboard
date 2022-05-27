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

  void logout() throws RemoteException;

  List<Message> getMessages() throws RemoteException;

  void sendMessages(ArrayList<Message> messages) throws RemoteException;

  void kick(String username) throws RemoteException;
}
