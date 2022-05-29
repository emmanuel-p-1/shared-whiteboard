package remote.serializable;

import java.io.Serializable;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Session serialization.
 *
 */

public class Message implements Serializable {
  private final String username;
  private final String message;

  // Constructs message
  public Message(String username, String message) {
    this.username = username;
    this.message = message;
  }

  // Getters
  public String getUsername() {
    return username;
  }

  public String getMessage() {
     return message;
  }
}
