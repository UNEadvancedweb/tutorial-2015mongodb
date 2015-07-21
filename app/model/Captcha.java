package model;

public class Captcha {

    /**
     * URLs of photos the user should select
     */
    public static String[] yesPhotos = {

    };


    /**
     * URLs of photos the user should not select
     */
    public static String[] noPhotos = {

    };

    /**
     * How many photos there are in total
     */
    public static int numPhotos() {
        return yesPhotos.length + noPhotos.length;
    }

    /**
     * The index of a random photo
     */
    public static int randomPhotoIdx() {
        return (int)(Math.random() * numPhotos());
    }

    /**
     * Get a photo with the specified index
     */
    public static String getPhoto(int idx) {

        if (idx < 0 || idx >= numPhotos()) {
            throw new IllegalArgumentException("index out of range");
        }

        if (idx < yesPhotos.length) {
            return yesPhotos[idx];
        } else {
            return noPhotos[idx - yesPhotos.length];
        }
    }

    /**
     * Whether a photo selected by the user was in the "yes" set
     */
    public boolean isCorrect(int idx) {
        return idx >= 0 && idx < yesPhotos.length;
    }

}
