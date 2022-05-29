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
  private final String address;
  private Registry registry;

  private final Data data = new Data();

  public Server(String serverName, String address, int port) {
    this.serverName = serverName;
    this.port = port;
    this.address = address;
  }

  public void run(String admin) throws RemoteException, AlreadyBoundException {
    ILogin login = new Login(admin, data);
    registry = LocateRegistry.getRegistry(address, port);
    try {
      registry.bind(serverName, login);
    } catch (ConnectException | NoSuchObjectException e) {
      registry = LocateRegistry.createRegistry(port);
      registry.bind(serverName, login);
    } catch (AlreadyBoundException e) {
      e.printStackTrace();
    }
  }

  public void closeConnection() throws RemoteException, NotBoundException {
    if (registry.list().length > 1) {
      registry.unbind(serverName);
    } else {
      UnicastRemoteObject.unexportObject(registry, true);
    }
  }
}
