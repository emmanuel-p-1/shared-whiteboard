package client.connection;

import client.Client;
import remote.serializable.Action;
import remote.rInterface.ILogin;
import remote.rInterface.ISession;
import remote.serializable.Message;

import javax.security.auth.login.LoginException;
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

  public Connection(Client client, String username, String serverName, String address, int port) {
    this.client = client;
    this.serverName = serverName;
    this.username = username;
    this.address = address;
    this.port = port;

    try {
      Registry registry = LocateRegistry.getRegistry(address, port);
      remote = ((ILogin) registry.lookup(serverName)).login(username);
    } catch (LoginException e) {
      // Unhandled Exception
      System.out.println(e.getMessage());
    } catch (Exception e) {
      // Unhandled Exception
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    long time = System.currentTimeMillis();

    while (!isInterrupted()) {
      try {
        if (System.currentTimeMillis() - time >= 5000) {
          client.addUsers(remote.getSessions());

          time = System.currentTimeMillis();
        }

        ArrayList<Action> copy = new ArrayList<>(Client.getActions());
        client.clearRecentActions();
        remote.sendActions(copy);

        client.getWhiteboard().processActions((ArrayList<Action>) remote.receiveActions());
        client.getUserPane().processMessages((ArrayList<Message>) remote.getMessages());

        sleep(100);
      } catch (Exception e) {
        // Unhandled Exception
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
