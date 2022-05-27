package client;

import remote.Action;
import remote.ILogin;
import remote.ISession;

import javax.security.auth.login.LoginException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Connection extends Thread {
  private ISession remote;

  Connection(String username, String serverName, String address, int port) {
    try {
      Registry registry = LocateRegistry.getRegistry(address, port);
      remote = ((ILogin) registry.lookup(serverName)).login(username);
      System.err.println("connected");
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
    while (!isInterrupted()) {
      try {
        ArrayList<Action> copy = new ArrayList<>(Client.recentActions);
        Client.recentActions = new ArrayList<>();
        remote.sendActions(copy);

        Client.wb.processActions((ArrayList<Action>) remote.receiveActions());

//        Client.addUsers(remote.getSessions());

        sleep(100);
      } catch (Exception e) {
        // Unhandled Exception
        e.printStackTrace();
        interrupt();
      }
    }
  }

  public void closeConnection() throws RemoteException {
    interrupt();
    remote.logout();
  }
}
