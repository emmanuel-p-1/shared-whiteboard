package server;

import remote.rInterface.ILogin;
import server.rInstance.Login;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
  private final String serverName;
  private final int port;
  private Registry registry;

  public Server(String serverName, int port) {
    this.serverName = serverName;
    this.port = port;
  }

  public static void main(String[] args) throws AlreadyBoundException, RemoteException {
    new Server(args[0], Integer.parseInt(args[1])).run("admin");
  }

  public void run(String admin) throws RemoteException, AlreadyBoundException {
    ILogin login = new Login(admin);
    registry = LocateRegistry.getRegistry(port);
    try {
      registry.bind(serverName, login);
    } catch (ConnectException e) {
      registry = LocateRegistry.createRegistry(port);
      registry.bind(serverName, login);
    } catch (AlreadyBoundException e) {
      registry.rebind(serverName, login);
    }
    System.err.println("server ready");
  }

  public void closeConnection() throws RemoteException, NotBoundException {
    if (registry.list().length > 1) {
      registry.unbind(serverName);
    } else {
      UnicastRemoteObject.unexportObject(registry, true);
    }
  }
}
