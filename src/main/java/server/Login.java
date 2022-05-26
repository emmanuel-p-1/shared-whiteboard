package server;

import remote.ILogin;
import remote.ISession;

import javax.security.auth.login.LoginException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

class Login extends UnicastRemoteObject implements ILogin {
  ArrayList<String> users;

  protected Login() throws RemoteException {
    users = new ArrayList<>();
  }

  @Override
  public ISession login(String username) throws LoginException, RemoteException {
    if (users.contains(username)) {
      throw new LoginException("Username taken.");
    }
    return new Session();
  }
}
