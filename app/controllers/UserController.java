package controllers;

import model.Session;
import model.User;
import model.UserService;
import org.mindrot.jbcrypt.BCrypt;
import play.mvc.Result;
import play.mvc.Controller;

public class UserController extends Controller {

    protected static UserService getUserService() {
        return UserService.instance;
    }

    protected static final String SESSIONVAR = "mySession";

    /**
     * Returns our generated session ID for this user, creating one if necessary
     */
    protected static String getSessionId() {
        String id = session(SESSIONVAR);
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
            session(SESSIONVAR, id);
        }
        return id;
    }

    public static Result loginView() {
        return ok(views.html.application.login.render(null));
    }

    public static Result registerView() {
        return ok(views.html.application.register.render(null));
    }

    public static Result sessionsView() {
        return ok(views.html.application.sessions.render(getUserService().getUserFromSession(getSessionId())));
    }

    public static Result doLogin() {
        String sessionId = getSessionId();
        String email;
        String password;

        // We're doing this very basically, as Play forms are not in scope for the course
        // (The unit prefers to teach things that are a little closer to the HTTP, rather than convenience wrappers)
        try {
            email = request().body().asFormUrlEncoded().get("email")[0];
            password = request().body().asFormUrlEncoded().get("password")[0];
        } catch (Exception e) {
            return badRequest(views.html.application.login.render("Email and password could not be found in the request"));
        }

        if (getUserService().getUserFromSession(sessionId)!= null) {
            return badRequest(views.html.application.login.render("You're already logged in!"));
        }

        User u = getUserService().getUser(email, password);
        if (u != null) {
            u.pushSession(new Session(sessionId, request().remoteAddress(), System.currentTimeMillis()));
            return redirect("/");
        } else {
            return forbidden(views.html.application.login.render("Wrong email address or password"));
        }
    }

    public static Result doRegister() {
        String sessionId = getSessionId();
        String email;
        String password;

        // We're doing this very basically, as Play forms are not in scope for the course
        // (The unit prefers to teach things that are a little closer to the HTTP, rather than convenience wrappers)
        try {
            email = request().body().asFormUrlEncoded().get("email")[0];
            password = request().body().asFormUrlEncoded().get("password")[0];
        } catch (Exception e) {
            return badRequest(views.html.application.login.render("Email and password could not be found in the request"));
        }

        if (getUserService().getUserFromSession(sessionId)!= null) {
            return badRequest(views.html.application.login.render("You're already logged in!"));
        }

        // Create a new user object
        User u = new User(java.util.UUID.randomUUID().toString(), email, BCrypt.hashpw(password, BCrypt.gensalt()));

        // Try to register them
        try {
            getUserService().registerUser(u);
        } catch (Exception ex) {
            return badRequest(views.html.application.register.render(ex.getMessage()));
        }

        // Log them in
        u.pushSession(new Session(sessionId, request().remoteAddress(), System.currentTimeMillis()));
        return redirect("/");
    }

    public static Result doLogout() {
        String sessionId = getSessionId();
        User u = getUserService().getUserFromSession(sessionId);
        if (u != null) {
            u.removeSession(sessionId);
        }

        return ok(views.html.application.login.render(null));
    }

    public static Result doRemoteLogout() {
        String sessionId = getSessionId();
        String toRemove;

        try {
            toRemove = request().body().asFormUrlEncoded().get("remove")[0];
        } catch (Exception e) {
            return badRequest(views.html.application.login.render("Session to remove could not be found in the request"));
        }

        User u = getUserService().getUserFromSession(sessionId);
        if (u != null) {
            u.removeSession(toRemove);
        }

        return redirect("/sessions");
    }
}
