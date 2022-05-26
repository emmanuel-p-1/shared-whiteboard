package server;

import remote.RemoteInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
  public static void main(String[] args) {
    run();
  }

  public static void run() {
    try {
      RemoteInterface remote = new Remote();
      Registry registry = LocateRegistry.createRegistry(1234);
      registry.bind("server", remote);
      System.err.println("Server ready");
    } catch (Exception e) {
      // Unhandled Exception
      e.printStackTrace();
    }
  }
}
