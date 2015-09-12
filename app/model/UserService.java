package model;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
     * Allocates an ObjectID and returns it as a hex string; I've exposed this so we can use it also for session IDs.
     */
    public String allocateId() {
        return new ObjectId().toHexString();
    }


    /**
     * Checks if this is a valid ObjectID, as some browsers might have old UUIDs cached
     */
    public boolean isValidId(String id) {
        try {
            ObjectId i = new ObjectId(id);
            return i.toHexString().equals(id);
        } catch (Exception ex) {
            return false;
        }
    }


    public User registerUser(User u) {
        // Let's first check the user isn't already registered
        if (getChitterCollection().find(new Document("email", u.getEmail())).first() != null) {
            throw new IllegalArgumentException("That email address has already been registered");
        }

        insert(u);

        return u;
    }

    public User getUser(String id) {
        Document d = getChitterCollection().find(new Document("_id", new ObjectId(id))).first();
        if (d != null) {
            return userFromBson(d);
        } else {
            return null;
        }
    }

    /**
     * Get the user by email and password, returning null if they don't exist (or the password is wrong)
     */
    public User getUser(String email, String password) {
        Document d = getChitterCollection().find(new Document("email", email)).first();

        // I wrote userFromBson to accept nulls
        User u = userFromBson(d);
        if (u != null && BCrypt.checkpw(password, u.getHash())) {
            return u;
        } else {
            return null;
        }
    }

    /**
     * Get the user who is logged in with this session, if there is one
     */
    public User getUserFromSession(String sessionId) {
        Document d = getChitterCollection().find(new Document("sessions._id", new ObjectId(sessionId))).first();
        return userFromBson(d);
    }

    protected static Document userToBson(User u) {
        List<Document> sessions = new ArrayList<>();
        for (Session s : u.getSessions()) {
            sessions.add(sessionToBson(s));
        }

        return new Document("_id", new ObjectId(u.getId()))
                .append("email", u.email)
                .append("hash", u.getHash())
                .append("sessions", sessions);
    }

    protected static User userFromBson(Document d) {
        // This lets us call this method even if d is null
        if (d == null) {
            return null;
        }

        String id = d.getObjectId("_id").toHexString();
        String email = d.getString("email");
        String hash = d.getString("hash");
        User u =  new User(id, email, hash);

        // This gives an unchecked warning; we'd need to use the safer means of doing this (which we don't cover)
        // to avoid the warning
        List<Document> sessions = d.get("sessions", List.class);

        for (Document sd : sessions) {
            Session s = sessionFromBson(sd);
            u.pushSession(s);
        }

        return u;
    }

    protected static Session sessionFromBson(Document d) {
        // This lets us call this method even if d is null
        if (d == null) {
            return null;
        }

        String id = d.getObjectId("_id").toHexString();
        String ip = d.getString("ipAddress");
        long since = d.getLong("since");
        return new Session(id, ip, since);
    }

    protected static Document sessionToBson(Session s) {
        return new Document("_id", new ObjectId(s.getId()))
                .append("ipAddress", s.getIpAddress())
                .append("since", s.getSince());
    }

    protected void insert(User u) {
        getChitterCollection().insertOne(userToBson(u));
    }

    public void update(User u) {
        getChitterCollection().replaceOne(new Document("_id", new ObjectId(u.getId())), userToBson(u));
    }

}
