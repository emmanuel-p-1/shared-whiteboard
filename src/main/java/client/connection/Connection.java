package client.connection;

import client.Client;
import remote.serializable.Action;
import remote.rInterface.ILogin;
import remote.rInterface.ISession;
import remote.serializable.Message;

import javax.security.auth.login.LoginException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Connection between local and remote - calls for data from remote.
 *
 */

public class Connection extends Thread {
  private final ISession remote;    // Stub for calling requests.
  private final Client client;      // Client driver.
  private final String serverName;  // Server (register) name.
  private final String username;    // User's name.
  private final String address;     // IP address of remote.
  private final int port;           // Remote port,
  private boolean approved = false; // Prevent unapproved user calling data.

  // Constructs connection between local and remote.
  public Connection(
          Client client,
          String username,
          String serverName,
          String address,
          int port
  ) throws RemoteException, NotBoundException, LoginException {
    this.client = client;
    this.serverName = serverName;
    this.username = username;
    this.address = address;
    this.port = port;

    // Gets the remote stub.
    Registry registry = LocateRegistry.getRegistry(address, port);
    remote = ((ILogin) registry.lookup(serverName)).login(username);
  }

  // Asks for data every 100 milliseconds, users every second.
  @Override
  public void run() {
    long time = System.currentTimeMillis();

    while (!isInterrupted()) {
      try {
        // Block unapproved users.
        while (!remote.isApproved()) {
          sleep(1000);
        }
        if (!approved) {
          client.approved();
          approved = true;
        }

        // Get connected user list, waiting queue.
        if (System.currentTimeMillis() - time >= 5000) {
          client.addUsers(remote.getSessions());
          client.addWaiting(remote.getWaiting());

          time = System.currentTimeMillis();
        }

        // Get and send actions created recently.
        ArrayList<Action> copy = new ArrayList<>(Client.getActions());
        client.clearRecentActions();
        remote.sendActions(copy);

        // Update action and message data.
        client.getWhiteboard().processActions((ArrayList<Action>) remote.receiveActions());
        client.getUserPane().processMessages((ArrayList<Message>) remote.getMessages());

        sleep(100);
      } catch (Exception e) {
        Client.setError("Disconnected from server");
        client.restart();
        interrupt();
      }
    }
  }

  // Close connection with remote.
  public void closeConnection() throws RemoteException {
    interrupt();
    if (remote != null) remote.logout();
  }

  // Get the stub to call requests.
  public ISession getRemote() {
    return remote;
  }

  // Get assigned server name.
  public String getServerName() {
    return serverName;
  }

  // Get assigned username.
  public String getUsername() {
    return  username;
  }

  // Get assigned IP address.
  public String getAddress() {
    return address;
  }

  // Get assigned port.
  public int getPort() {
    return port;
  }
}
