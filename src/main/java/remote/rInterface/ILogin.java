package remote.rInterface;

import javax.security.auth.login.LoginException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Remote reference module for user login.
 *
 */

public interface ILogin extends Remote {
  // Login to receive a session.
  ISession login(String username) throws LoginException, RemoteException;
}
