package server;

import remote.ILogin;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
  private final String serverName;

  public Server(String serverName) {
    this.serverName = serverName;
  }

  public static void main(String[] args) throws AlreadyBoundException, RemoteException {
    new Server(args[0]).run();
  }

  public void run() throws RemoteException, AlreadyBoundException {
    ILogin login = new Login();
    Registry registry = LocateRegistry.createRegistry(1234);
    registry.bind(serverName, login);
    System.err.println("server ready");
  }

  public String getServerName() {
    return serverName;
  }
}
