package remote;

import javax.security.auth.login.LoginException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILogin extends Remote {
  ISession login(String username) throws LoginException, RemoteException;
}
