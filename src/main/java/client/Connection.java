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
    long time = System.currentTimeMillis();

    while (!isInterrupted()) {
      try {
        if (System.currentTimeMillis() - time >= 5000) {
          for (String user : Client.kickUsers) {
            remote.kick(user);
          }
          Client.addUsers(remote.getSessions());

          time = System.currentTimeMillis();
        }

        ArrayList<Action> copy = new ArrayList<>(Client.recentActions);
        Client.recentActions = new ArrayList<>();
        remote.sendActions(copy);

        Client.wb.processActions((ArrayList<Action>) remote.receiveActions());

        sleep(100);
      } catch (Exception e) {
        // Unhandled Exception
        interrupt();
      }
    }
  }

  public void closeConnection() throws RemoteException {
    interrupt();
    remote.logout();
  }
}
