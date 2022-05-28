package server.rInstance;

import remote.rInterface.ILogin;
import remote.rInterface.ISession;
import server.Data;

import javax.security.auth.login.LoginException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Login extends UnicastRemoteObject implements ILogin {
  private final String admin;
  private final Data data;

  public Login(String admin, Data data) throws RemoteException {
    this.admin = admin;
    this.data = data;
  }

  @Override
  public ISession login(String username) throws LoginException, RemoteException {
    if (data.hasUsername(username)) {
      throw new LoginException("Username taken.");
    }
    data.addUsername(username);
    if (username.equals(admin)) return new Session(username, true, data);
    return new Session(username,false, data);
  }
}
