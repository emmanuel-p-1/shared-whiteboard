package server.rInstance;

import remote.rInterface.ILogin;
import remote.rInterface.ISession;
import server.Data;

import javax.security.auth.login.LoginException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Remote object for user login.
 *
 */

public class Login extends UnicastRemoteObject implements ILogin {
  private final String admin;
  private final Data data;

  public Login(String admin, Data data) throws RemoteException {
    this.admin = admin;
    this.data = data;
  }

  // Login to receive a session.
  @Override
  public ISession login(String username) throws LoginException,
          RemoteException {
    if (data.hasUsername(username)) {
      throw new LoginException();
    }
    if (username.equals(admin)) {
      data.addUsername(username);
      return new Session(username, true, data);
    }
    data.addWaiting(username);
    return new Session(username,false, data);
  }
}
