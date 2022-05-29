package remote.rInterface;

import remote.serializable.Action;
import remote.serializable.Message;

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

  void sendMessage(Message message) throws RemoteException;

  void kick(String username) throws RemoteException;

  void approve(String username) throws RemoteException;

  void reject(String username) throws RemoteException;

  List<String> getWaiting() throws RemoteException;

  boolean isApproved() throws RemoteException;
}
