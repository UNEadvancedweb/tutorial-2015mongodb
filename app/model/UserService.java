package model;

import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.ConcurrentHashMap;

public class UserService {

    public static final UserService instance = new UserService();

    /**
     * This stands in for our database at the moment
     */
    protected ConcurrentHashMap<String, User> table = new ConcurrentHashMap<>();

    public User registerUser(User u) {
        // Let's first check the user isn't already registered
        for (User uInTable : table.values()) {
            if (u.getEmail().equals(uInTable.getEmail())) {
                throw new IllegalArgumentException("That email address has already been registered");
            }
        }

        return table.put(u.id, u);
    }

    public User getUser(String id) {
        return table.get(id);
    }

    /**
     * Get the user by email and password, returning null if they don't exist (or the password is wrong)
     */
    public User getUser(String email, String password) {
        for (User u : table.values()) {
            if (u.email.equals(email) && BCrypt.checkpw(password, u.getHash())) {
                return u;
            }
        }
        return null;
    }

    public User getUserFromSession(String sessionId) {
        throw new IllegalArgumentException("Not implemented yet");
    }

}
