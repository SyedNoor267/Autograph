package helpers;

import android.graphics.Bitmap;
import android.net.Uri;

/*
* This class is for storing top menu in in a list before they are added into adapter*/
public class HeaderMenu {
    private Uri image;
    private String title;
    private String imageType;

    public HeaderMenu(Uri image, String title, String imageType){
        this.image = image;
        this.title = title;
        this.imageType = imageType;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}
