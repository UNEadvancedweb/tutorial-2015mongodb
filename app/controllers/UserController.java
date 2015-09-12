package controllers;

import model.Session;
import model.UserService;
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
        return ok(views.html.application.login.render(null));
    }

    public static Result doLogin() {
        return ok(views.html.application.login.render(null));
    }

    public static Result doRegister() {
        return ok(views.html.application.login.render(null));
    }
}
