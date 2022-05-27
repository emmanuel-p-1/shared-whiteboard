package server;

import remote.ILogin;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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
    registry = LocateRegistry.createRegistry(port);
    registry.bind(serverName, login);
    System.err.println("server ready");
  }

  public String getServerName() {
    return serverName;
  }

  public void unbind() throws NotBoundException, RemoteException {
    registry.unbind(serverName);
  }

  public void closeRegistry() throws NoSuchObjectException {
    UnicastRemoteObject.unexportObject(registry, true);
  }
}
