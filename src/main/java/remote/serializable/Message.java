package remote.serializable;

import java.io.Serializable;

public class Message implements Serializable {
  private final String username;
  private final String message;

  public Message(String username, String message) {
    this.username = username;
    this.message = message;
  }

  public String getUsername() {
    return username;
  }

  public String getMessage() {
     return message;
  }
}
