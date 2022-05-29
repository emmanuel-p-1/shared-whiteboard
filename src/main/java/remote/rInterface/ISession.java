package remote.rInterface;

import remote.serializable.Action;
import remote.serializable.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Remote reference modules for each connection session.
 *
 */

public interface ISession extends Remote {
  // Get unimplemented actions.
  List<Action> receiveActions() throws RemoteException;

  // Add actions to shared list.
  void sendActions(ArrayList<Action> actions) throws RemoteException;

  // Get if user is admin.
  boolean isAdmin() throws RemoteException;

  // Get all users.
  List<String> getSessions() throws RemoteException;

  // Leave connection.
  void logout() throws RemoteException;

  // Get unread messages.
  List<Message> getMessages() throws RemoteException;

  // Add messages to shared list.
  void sendMessage(Message message) throws RemoteException;

  // Remove connection.
  void kick(String username) throws RemoteException;

  // Allow user to enter room.
  void approve(String username) throws RemoteException;

  // Deny user from entering room.
  void reject(String username) throws RemoteException;

  // Get usernames of waiting users.
  List<String> getWaiting() throws RemoteException;

  // Check if user is approved.
  boolean isApproved() throws RemoteException;
}
