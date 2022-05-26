package client;

import remote.Action;
import remote.RemoteInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Connection extends Thread {
  private RemoteInterface remote;

  Connection() {
    try {
      Registry registry = LocateRegistry.getRegistry("localhost", 1234);
      remote = (RemoteInterface) registry.lookup("server");
      System.err.println("connected");
    } catch (Exception e) {
      // Unhandled Exception
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (!isInterrupted()) {
      try {
        ArrayList<Action> copy = new ArrayList<>(Client.actions);
        Client.actions = new ArrayList<>();
        remote.sendActions(copy);

        ArrayList<Action> actions = remote.receiveActions();
        Client.wb.clearCanvas();
        Client.wb.addActionsToCanvas(actions);

        sleep(100);
      } catch (Exception e) {
        // Unhandled Exception
        e.printStackTrace();
      }
    }
  }
}
