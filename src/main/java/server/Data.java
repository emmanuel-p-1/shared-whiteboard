package server;

import remote.serializable.Action;
import remote.serializable.Message;
import server.rInstance.Session;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * System data storage.
 *
 */

public class Data {
  // Data lists.
  private final ArrayList<String> usernames = new ArrayList<>();
  private final ArrayList<String> waiting = new ArrayList<>();
  private final ArrayList<Action> allActions = new ArrayList<>();
  private final ArrayList<Message> allMessages = new ArrayList<>();
  private final ArrayList<Session> sessions = new ArrayList<>();

  // Locks.
  private final ReadWriteLock userLock = new ReentrantReadWriteLock();
  private final ReadWriteLock waitLock = new ReentrantReadWriteLock();
  private final ReadWriteLock actionLock = new ReentrantReadWriteLock();
  private final ReadWriteLock messageLock = new ReentrantReadWriteLock();
  private final ReadWriteLock sessionLock = new ReentrantReadWriteLock();

  // Check username.
  public boolean hasUsername(String username) {
    boolean matched = false;

    userLock.readLock().lock();
    try {
      for (String u : usernames) {
        if (u.equals(username)) {
          matched = true;
          break;
        }
      }
    } finally {
      userLock.readLock().unlock();
    }

    if (!matched) {
      waitLock.readLock().lock();
      try {
        for (String w : waiting) {
          if (w.equals(username)) {
            matched = true;
            break;
          }
        }
      } finally {
        waitLock.readLock().unlock();
      }
    }

    return matched;
  }

  // Approve user.
  public void approve(String username) {
    waitLock.writeLock().lock();
    try {
      waiting.remove(username);
    } finally {
      waitLock.writeLock().unlock();
    }

    addUsername(username);
  }

  // Reject user.
  public void reject(String username) {
    waitLock.writeLock().lock();
    try {
      waiting.remove(username);
    } finally {
      waitLock.writeLock().unlock();
    }
  }

  // User added to queue.
  public void addWaiting(String username) {
    waitLock.writeLock().lock();
    try {
      waiting.add(username);
    } finally {
      waitLock.writeLock().unlock();
    }
  }

  // Add user.
  public void addUsername(String username) {
    userLock.writeLock().lock();
    try {
      usernames.add(username);
    } finally {
      userLock.writeLock().unlock();
    }
  }

  // Get all usernames.
  public ArrayList<String> getUsernames() {
    userLock.readLock().lock();
    ArrayList<String> tmp;
    try {
      tmp = new ArrayList<>(usernames);
    } finally {
      userLock.readLock().unlock();
    }
    return tmp;
  }

  // get all waiting usernames.
  public ArrayList<String> getWaiting() {
    waitLock.readLock().lock();
    ArrayList<String> tmp;
    try {
      tmp = new ArrayList<>(waiting);
    } finally {
      waitLock.readLock().unlock();
    }
    return tmp;
  }

  // Add action.
  public void addAction(Action action) {
    actionLock.writeLock().lock();
    try {
      allActions.add(action);
    } finally {
      actionLock.writeLock().unlock();
    }
  }

  // Get all actions.
  public ArrayList<Action> getActions() {
    actionLock.readLock().lock();
    ArrayList<Action> actions;
    try {
      actions = new ArrayList<>(allActions);
    } finally {
      actionLock.readLock().unlock();
    }
    return actions;
  }

  // Add message.
  public void addMessage(Message message) {
    messageLock.writeLock().lock();
    try {
      allMessages.add(message);
    } finally {
      messageLock.writeLock().unlock();
    }
  }

  // Get all messages.
  public ArrayList<Message> getMessages() {
    messageLock.readLock().lock();
    ArrayList<Message> messages;
    try {
      messages = new ArrayList<>(allMessages);
    } finally {
      messageLock.readLock().unlock();
    }
    return messages;
  }

  // Get all sessions.
  public ArrayList<Session> getSessions() {
    sessionLock.readLock().lock();
    ArrayList<Session> sessions;
    try {
      sessions = new ArrayList<>(this.sessions);
    } finally {
      sessionLock.readLock().unlock();
    }
    return sessions;
  }

  // Add new user session.
  public void addSession(Session session) {
    sessionLock.writeLock().lock();
    try {
      sessions.add(session);
    } finally {
      sessionLock.writeLock().unlock();
    }
  }

  // Remove session.
  public void removeSession(Session session) {
    sessionLock.writeLock().lock();
    try {
      sessions.remove(session);
    } finally {
      sessionLock.writeLock().unlock();
    }

    userLock.writeLock().lock();
    try {
      usernames.remove(session.getUsername());
    } finally {
      userLock.writeLock().unlock();
    }

    waitLock.writeLock().lock();
    try {
      waiting.remove(session.getUsername());
    } finally {
      waitLock.writeLock().unlock();
    }
  }
}
