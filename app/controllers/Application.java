package controllers;

import model.BCryptExample;
import model.Captcha;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Arrays;

public class Application extends Controller {

    public static Result index() {
        int arrayLength = 5;
        int[] indexes = new int[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            indexes[i] = (int)(Captcha.numPhotos() * Math.random());
        }

        return ok(views.html.application.index.render(indexes));
    }

    private static int countBeagles(String[] indexes) {
        int i = 0;
        for (String s : indexes) {
            if (Captcha.isCorrect(Integer.valueOf(s))) {
                i++;
            }
        }
        return i;
    }

    public static Result matches() {
        String[] sent = request().body().asFormUrlEncoded().get("sent");
        String[] beagles = request().body().asFormUrlEncoded().get("beagle");

        int numBeagles = countBeagles(sent);
        int numFound = countBeagles(beagles);

        return ok(views.html.application.matches.render(numBeagles, numFound));
    }

}
