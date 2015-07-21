package model;

public class Captcha {

    /**
     * URLs of photos the user should select
     */
    public static String[] yesPhotos = {
            "http://assets.dogtime.com/breed/profile/image/4d3771f10106195669001f8c/beagle.jpg",
            "https://gdkennel.files.wordpress.com/2013/09/010.jpg",
            "http://www.perros2.com/wp-content/uploads/2012/06/ddd.jpg",
            "http://dogobedienceadvice.com/images/beagle_training.jpg",
            "http://www.swish-swash.co.uk/images/beagle/beagles.jpg"
    };


    /**
     * URLs of photos the user should not select
     */
    public static String[] noPhotos = {
            "http://www.pets4homes.co.uk/images/articles/1265/large/ten-reasons-why-dogs-make-better-pets-than-cats-52bc3172b4816.jpg",
            "http://www.adweek.com/files/imagecache/node-blog/sa_article/dog_image_0.jpg",
            "http://7-themes.com/data_images/out/36/6891162-dogs.jpg",
            "http://www.boxer-dog.org/sites/default/files/imagecache/500_width/ruby2.jpg",
            "http://www.imagepuppy.com/resized/762ce6cb8241c08d73ec9304b42f6d5f.jpg"
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
