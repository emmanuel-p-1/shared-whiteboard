package client.connection;

import client.Client;
import remote.serializable.Action;
import remote.rInterface.ILogin;
import remote.rInterface.ISession;
import remote.serializable.Message;

import javax.security.auth.login.LoginException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Connection extends Thread {
  private ISession remote;
  private final Client client;
  private final String serverName;
  private final String username;
  private final String address;
  private final int port;
  private boolean approved = false;

  public Connection(Client client, String username, String serverName, String address, int port) {
    this.client = client;
    this.serverName = serverName;
    this.username = username;
    this.address = address;
    this.port = port;

    try {
      Registry registry = LocateRegistry.getRegistry(address, port);
      remote = ((ILogin) registry.lookup(serverName)).login(username);
    } catch (AccessException e) {
      Client.setError("Access denied");
    } catch (LoginException | RemoteException e) {
      Client.setError(e.getMessage());
    } catch (NotBoundException e) {
      Client.setError("No registry found");
    }
  }

  @Override
  public void run() {
    long time = System.currentTimeMillis();

    while (!isInterrupted()) {
      try {
        while (!remote.isApproved()) {
          sleep(1000);
        }
        if (!approved) {
          client.approved();
          approved = true;
        }

        if (System.currentTimeMillis() - time >= 5000) {
          client.addUsers(remote.getSessions());
          client.addWaiting(remote.getWaiting());

          time = System.currentTimeMillis();
        }

        ArrayList<Action> copy = new ArrayList<>(Client.getActions());
        client.clearRecentActions();
        remote.sendActions(copy);

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

  public void closeConnection() throws RemoteException {
    interrupt();
    remote.logout();
  }

  public ISession getRemote() {
    return remote;
  }

  public String getServerName() {
    return serverName;
  }

  public String getUsername() {
    return  username;
  }

  public String getAddress() {
    return address;
  }

  public int getPort() {
    return port;
  }
}
