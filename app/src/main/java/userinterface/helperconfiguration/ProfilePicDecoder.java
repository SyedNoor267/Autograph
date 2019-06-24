package userinterface.helperconfiguration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfilePicDecoder {

    public static final String DRAWABLE = "drawable";
    public static final String R = "r";

    public static String encodeImage(Bitmap selectedImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    public static Bitmap getBitmapProfilePicture(String url_string) {
        URL imageURL = null;
        try {
            imageURL = new URL(url_string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap myBitmap = null;
//        HttpURLConnection.setFollowRedirects(true);
        InputStream input = null;

        try {
            URL url = new URL(imageURL.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            HttpURLConnection.setFollowRedirects(true);
            connection.connect();

            try {
                input = connection.getInputStream();
            } catch (Exception e) {
                e.getStackTrace();
            }

            myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }


    }

    public static Bitmap uriToBitmap(Uri selectedFileUri,Context context) {
        Bitmap image = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(selectedFileUri, R);
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image= BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    
//    public static Uri getUrl(String res){
//        return Uri.parse("android.resource://com.example.appiapi/drawable/" + res.toLowerCase());
//    }

    public static int getImage(String imageName, Context context) {

        return context.getResources().getIdentifier(imageName.toLowerCase(), DRAWABLE, context.getPackageName());
    }


    public static String parseNameFromJID(String jid){
        String[] parts = jid.split("@");
        return parts[0];
    }
}
