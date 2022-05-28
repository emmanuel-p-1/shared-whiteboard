package server.rInstance;

import remote.rInterface.ILogin;
import remote.rInterface.ISession;
import server.Server;

import javax.security.auth.login.LoginException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.ArrayList;

public class Login extends UnicastRemoteObject implements ILogin, Unreferenced {
  private final ArrayList<String> users;
  private final String admin;
  private final Server server;

  public Login(String admin, Server server) throws RemoteException {
    users = new ArrayList<>();
    this.admin = admin;
    this.server = server;
  }

  @Override
  public void unreferenced() {
    try {
      unexportObject(this, true);
    } catch (NoSuchObjectException e) {
      // Unhandled Exception
      e.printStackTrace();
    }
  }

  @Override
  public ISession login(String username) throws LoginException, RemoteException {
    if (users.contains(username)) {
      throw new LoginException("Username taken.");
    }
    users.add(username);
    if (username.equals(admin)) return new Session(username, true, server);
    return new Session(username,false, server);
  }
}
