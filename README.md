## Tutorial 4: User management

We'll build this functionality into the web project you were working on in last week's tutorial. (This tutorial repo starts with last week's solution).

### Create the domain class

I'd encourage you to think of a web system as a concurrent server program that communicates over HTTP -- in other words, start by developing a good base in the programming language you are using and then wire up controller methods to it. (Rather than just thinking of a web system as a collection of controller methods.)

As we're working with user management in this tutorial, let's start by defining a `User` class in our app's `model` package.

Something like...

```java
public class User {

   String id;

   String email;
   
   String hash;
   
   public User(String id, String email, String hash) {
     this.id = id;
     this.email = email;
     this.hash = hash;
   }
   
   public String getId() {
     return this.id;
   }

   public String getEmail() {
     return this.email;
   }
   
   public String getHash() {
     return this.hash;
   }

}
```

### Singleton service

Now let's create the user manager service. We're going to use a *singleton* pattern.  This is a design pattern that states there will only ever (at runtime) be one of this object. But we'll use an object, rather than static methods on a class because if we wanted to write some tests, we might want to give other services a pretend user management service rather than the real thing to make the tests easier to write.

As this service will always be present, we'll create the instance immediately and put it in a static field. So we'll use a slightly simplified form of the singleton pattern for this.

As we're not dealing with a database yet, you'll need to store some data in memory -- for example a map of user ids to users. For the moment, I recommend using `java.util.concurrent.ConcurrentHashMap`.

```java
public class UserService {

    public static final UserService instance = new UserService();
    
    // You'll need a datastructure for storing your registered users...

    public User registerUser(User u) {
      // implement this; don't forget to check one with this email doesn't already exist
    }

    public User getUser(String id) {
      // implement this
    }
    
    public User getUser(String email, String password) {
      // implement this
    }



}

```

### Create an (empty) controller

Now that we have some core functionality written, let's wire it up to forms and HTTP controller methods.

Create a `UserController` in your app's `controllers` package, and give this controller a protected static method to get the UserService instance.

### Create login and register views

In your app's views, create `login.scala.html` and `register.scala.html`. They each take one String parameter, for an error message, so at the top of the templates put `@(error:String)`. 

Then code up HTML forms that post to actions called `login` and `register`, with the email address and password. 

After the input fields, but before the submit button, put the error message, inside an if construct. eg:

```scala
@if(error != null) {
  <div>@error</div>
}
```

### Create routing entries

In `conf/routes`, create routing entries that will accept `POST`s to `/login` and `/register`, and call `controllers.UserController.doLogin()` and `controllers.UserController.doRegister()`. Note these methods take no parameters -- the data is in the body of the request.

Also create routing entries that will accept `GET`s to `/login` and `/register` and call `controllers.UserController.loginForm()` and `controllers.UserController.registerForm()`. These are the routing entries for showing the form pages.

### Create the controller methods

We now need to create the controller methods.

As Play compiles the templates when a request comes in, I tend to recommend first writing the method definitions, just getting them to return `ok("Not implemented yet")`. Then start the server and make a web request to it. This will trigger Play to recompile the templates. Then in IntelliJ, from the SBT tab, click the refresh icon, so that IntelliJ will now be able to "see" your compiled templates. (Otherwise the IDE can't see the compiled template classes to know that you can call them, and it shows spurious red errors.)

Create the four controller methods you need, and wire them up to your user service. You will also need a way of turning a password into a hash. There is sample code for this in the `model.BCrypt` class.

But, we need a way in the user management service to record the user's logged in sessions...

### Add a session class

Let's create a domain class for sessions.

Something like

```java
public class Session() {
  String id;
  String ipAddress;
  long since;
  
  public Session(String ipAddress) {
    this.id = java.util.UUID.randomUUID().tostring();
    this.since = System.currentTimeMillis();
    this.ipAddress = ipAddress;
  }
  
  public String getIpAddress() {
    return this.ipAddress;
  }
  
  public long getSince() {
    return this.since;
  }  
  
}

```

Add a hashmap of session IDs to sessions to your `User` class. This is where we'll store the user's active sessions.

### Getting the session from the request

We'll store the user's current session ID in a cookie. Particularly, we'll store it as a variable in Play's existing "session" cookie.

Define a method `getSessionId()` on the User Controller that will extract the user's session ID if it is set, and set it if it isn't

eg 

```java
public final static String sessionVar = "MY_SESSION"
```

```java
  String id = session(sessionVar);
  if (id == null) {
    id = java.util.UUID.randomUUID().toString();
    session(sessionVar, id);
  } 
  return id;
```

We can now get the user's session ID from any request.

### Add a method to your UserService

Add a method `getUserFromSession(String sessionId)` to your user service, to find which user (if any) is logged in with this session Id.

For the moment, just iterate through the users until you either find the right one, or reach the end of the users.


### Add behaviour to your controllers

* `doRegister`, if it is successful, creates a new user. But it might give an error that the user is already registered.

* `doLogin`, if it is successful, pushes the user's session into the user record (and pulls it from any other user record it is in). But it might give an error that the password was incorrect.

### Extension -- remote logout

Now that we have a user's sessions recorded against the user, we can produce a view that shows a list of the user's sessions, with a button to log each of them out.

