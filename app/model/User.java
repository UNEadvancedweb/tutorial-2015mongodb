package model;

import java.util.concurrent.ConcurrentHashMap;

public class User {

    String id;

    String email;

    String hash;


    /**
     * We need to hold the user's active sessions; this stands in for our database table
     */
    ConcurrentHashMap<String, Session> activeSessions = new ConcurrentHashMap<>();

    public User(String id, String email, String hash) {
        this.id = id;
        this.email = email;
        this.hash = hash;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getHash() {
        return hash;
    }

    /**
     * Is a particular session active on this user?
     */
    public boolean hasSession(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }

    /**
     * Record that a particular session is logged in as this user
     */
    public void pushSession(Session s) {
        activeSessions.put(s.id, s);
    }

    /**
     * Remove an active session from this user
     */
    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
}
