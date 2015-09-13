## Tutorial wk9: MongoDB

The starting point for this tutorial is the solution to the user management tutorial.

In that tutorial, we had a `UserService` that stored user data in memory. In this tutorial, we'll put that into the 
database.

As we're getting quite late into term, the solution will be posted immediately -- you can have a look at what you 
need to do by comparing the `master` branch with the `solution` branch. I recommend checking out the `master` branch,
and occasionally looking at the `solution` branch on github.com when you are stuck.

### Adding the driver

The first step has already been done for you -- adding the database driver to the dependencies in `build.sbt`

```scala
libraryDependencies ++= Seq(
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.mongodb" % "mongodb-driver" % "3.0.2"
)
```

### Connecting to the database

In `UserService.java`, we make a connection to the database. In this case, we've hardcoded the connection string, but
normally we would look this up from a configuration variable (which in turn usually looks up an environment variable)

```java
protected MongoClient mongoClient;
protected UserService() {
    mongoClient = new MongoClient("127.0.0.1", 27017);
}
```

We've also added a protected method for accessing your database; you should change the name of the database you access
to `comp391_username`, where `username` is your username on turing.

```java
protected MongoDatabase getDB() {
    // TODO: Change your database name, to avoid clashing with others on turing
    return mongoClient.getDatabase("comp391_yourusername");
}
```

I've then written a protected method that uses this to get the `chitterUser` collection.

```java
protected MongoCollection<Document> getChitterCollection() {
    return getDB().getCollection("chitterUser");
}
```

### Serialising to BSON

The next thing we need is a method that can translate the `User` class to BSON and back, to save it in the database.

I recommend changing how the ID is allocated -- at the moment, we've allocated a Java UUID and stored it as a String in
the ID field. If instead, you generate BSON ObjectIDs, you should still be able to hold them as Strings in Java, but they
will be convertible to ObjectIds to store in the database. In `UserService`, expose a method to allocate IDs, as you'll
probably also want to use an ObjectId for the session key.

**NB:** If you do change to ObjectIDs, clear the cookie from your browser -- otherwise you'll still have a Java UUID as
your session key, and they do not translate into ObjectIds happily!

(If you're stuck, peek at the solution!)

Note that to save `User`s, you will also need to be able to translate `Session` objects to BSON and back.

You're going to need to implement these. The [Java Driver Quick Tour](http://mongodb.github.io/mongo-java-driver/3.0/driver/getting-started/quick-tour/)
has examples for you.
 
```java
protected static Document userToBson(User u) {
    // TODO: You need to implement this
    Document d = new Document();
    throw new NotImplementedException();
}
```

```java
protected static User userFromBson(Document d) {
    // TODO: You need to implement this
    throw new NotImplementedException();
}
```


### Saving users

Once you can serialise Users, you can save them. I've added methods `UserService.insert` and `UserService.update` to 
help with this.

### Migrating the rest to MongoDB

Now you need to modify the `UserService` to use the database. I recommend starting with changing the `UserService.registerUser`
method -- change it to save the user in the database. Then try it, and using `mongo` at the command line, check that
the user really turned up in the database

```bash
use "comp391_username"
db.chitterUser.find()
```

Now that you've done this, you should migrate the rest of the `UserService` class to use the database instead of an
in-memory data structure.

One you're done, check that you can still log in, log out, and remote log out.

** Something to remember **

While we were working in memory, we pushed users into the session using `User.pushSession`. But the Java `User` objects
are now transient -- the data is persisted by putting it into the DB, not by hanging onto the object between requests.

So, you're going to need to put a method into `UserService` that pushes a `Session` into the `User` in the database. 
This could work by pushing it into the `User` Java object, and then calling `UserService.update` to save the object. Or
you could do an update query directly on the database entry.

### A note with the solution

To keep things simple for students who might not have much programming experience, I implemented the login/logout using
a call to save. Actually, though, I don't recommend you do this.

The web is a parallel environment -- you could have multiple calls happening on the same objects on different threads.
If each of those calls overwrites *the whole record*, then you can find that you can lose data -- one request treads on
the data from another.

Generally, I recommend using the update operations, such as `$push` and `$addToSet` for pushing sessions into users, as
these do not write the whole record. 

And ideally, change the settings in the driver so that you only return when the data has been written, not just when the
driver has sent the update request to the database.
