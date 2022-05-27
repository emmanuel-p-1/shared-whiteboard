package remote;

import java.io.Serializable;

public class Message implements Serializable {
  private final String username;
  private final String datetime;
  private final String message;

  public Message(String username, String datetime, String message) {
    this.username = username;
    this.datetime = datetime;
    this.message = message;
  }

  public String getUsername() {
    return username;
  }

  public String getDatetime() {
    return datetime;
  }

  public String getMessage() {
     return message;
  }
}
