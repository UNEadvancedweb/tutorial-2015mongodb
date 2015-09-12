package model;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonWriter;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


import java.util.concurrent.ConcurrentHashMap;

public class UserService {

    public static final UserService instance = new UserService();

    protected MongoClient mongoClient;
    protected UserService() {
        mongoClient = new MongoClient("127.0.0.1", 27017);
    }

    protected MongoDatabase getDB() {
        // TODO: Change your database name, to avoid clashing with others on turing
        return mongoClient.getDatabase("comp391_yourusername");
    }

    protected MongoCollection<Document> getChitterCollection() {
        return getDB().getCollection("chitterUser");
    }

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

    /**
     * Get the user who is logged in with this session, if there is one
     */
    public User getUserFromSession(String sessionId) {
        for (User u : table.values()) {
            if (u.hasSession(sessionId)) {
                return u;
            }
        }
        return null;
    }

    protected static Document userToBson(User u) {
        // TODO: You need to implement this
        Document d = new Document();
        throw new NotImplementedException();
    }

    protected static User userFromBson(Document d) {
        // TODO: You need to implement this
        throw new NotImplementedException();
    }

    protected static void save(User u) {
        // TODO: You need to implement this
        throw new NotImplementedException();
    }
}
