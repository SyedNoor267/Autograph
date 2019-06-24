package networklayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import networklayer.interfaces.Callback;
import networklayer.interfaces.ServiceLayerCallback;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.ProfilePicDecoder;
import userinterface.responsemodel.UserImage;

public class ServiceLayer {



    /**
     * Call Getuser By Nickname
     *
     * @param callback  -> Request and Response for the service Callback
     * @param nickname     -> String nickname from the user Input
     *
     */
    public static void GetUserByNickname(String nickname, Context context, final ServiceLayerCallback callback)
    {
        String url = ConfigurationConstant.url_user_get;
        HashMap<Object, Object> data = new HashMap<>();

//        EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
        data.put(ConfigurationConstant.nickname, nickname);

        AsyncServiceCall asyncTask = new AsyncServiceCall(url, context, new Callback() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onResult(String result) {
                try {
                    callback.onSuccess(result);
                } catch (JSONException e) {
                    callback.onFailure(e.getLocalizedMessage());
                }

            }

            @Override
            public void onCancel() {
                callback.onFailure(ConfigurationConstant.onCancel);
            }
        }, data, null, ConfigurationConstant.GET);

        asyncTask.execute();
    }



    /**
     * Call Getuser By Nickname
     *
     * @param callback  -> Request and Response for the service Callback
     * @param nickname     -> String nickname from the user Input
     *
     */
    public static void GetBeaconByNickname(String nickname, Context context, final ServiceLayerCallback callback)
    {
        String url = ConfigurationConstant.url_beacon_get;
        HashMap<Object, Object> data = new HashMap<>();

//        EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
        data.put(ConfigurationConstant.nickname, nickname);

        AsyncServiceCall asyncTask = new AsyncServiceCall(url, context, new Callback() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onResult(String result) {
                try {
                    callback.onSuccess(result);
                } catch (JSONException e) {
                    callback.onFailure(e.getLocalizedMessage());
                }

            }

            @Override
            public void onCancel() {
                callback.onFailure(ConfigurationConstant.onCancel);
            }
        }, data, null, ConfigurationConstant.GET);

        asyncTask.execute();
    }


    /**
     * Call Getuser By Nickname
     *
     * @param callback  -> Request and Response for the service Callback
     * @param userData     -> String nickname from the user Input
     *
     */
    //public static void CreateUser( String nickname, String password, String email, String firstName, String lastName, String image,  Context context, final ServiceLayerCallback callback)
    public static void CreateUser( HashMap<String, Object> userData,  Context context, final ServiceLayerCallback callback)
    {

        String nickname  = userData.containsKey(ConfigurationConstant.nickname) && (userData.get(ConfigurationConstant.nickname) != null || !userData.get(ConfigurationConstant.nickname).equals("")) ? userData.get(ConfigurationConstant.nickname).toString() : "";
        String password  = userData.containsKey(ConfigurationConstant.password) && (userData.get(ConfigurationConstant.password) != null || !userData.get(ConfigurationConstant.password).equals("")) ? userData.get(ConfigurationConstant.password).toString() : "";
        String email     = userData.containsKey(ConfigurationConstant.email) && (userData.get(ConfigurationConstant.email) != null || !userData.get(ConfigurationConstant.email).equals("")) ?
                userData.get(ConfigurationConstant.email).toString() :
                "";
        String firstName = userData.containsKey(ConfigurationConstant.name) && userData.get(ConfigurationConstant.name) != null ?
                userData.get(ConfigurationConstant.name).toString() : "";
        String lastName  = userData.containsKey(ConfigurationConstant.surname) &&  userData.get(ConfigurationConstant.surname)!= null ?
                userData.get(ConfigurationConstant.surname).toString()+"" : "";
        String image     = userData.containsKey(ConfigurationConstant.img_link) && userData.get(ConfigurationConstant.img_link)!= null ?
                userData.get(ConfigurationConstant.img_link).toString()+"" : "";
        String birthDate = userData.containsKey(ConfigurationConstant.birthDate) && userData.get(ConfigurationConstant.birthDate) != null ?
                userData.get(ConfigurationConstant.birthDate).toString() : "";
        String sex       = userData.containsKey(ConfigurationConstant.sex) && userData.get(ConfigurationConstant.sex) != null ?
                userData.get(ConfigurationConstant.sex).toString()+"" : "";
        String nationId  = userData.containsKey(ConfigurationConstant.nationId) && userData.get(ConfigurationConstant.nationId) != null ?
                userData.get(ConfigurationConstant.nationId).toString()+"" : "";

        String urlpost = ConfigurationConstant.url_user_post;
        JSONObject postData = new JSONObject();


        String image_base64;
        Bitmap bitmap;
        try {
            //set image if it is provided
            bitmap = ProfilePicDecoder.getBitmapProfilePicture(image);
            image_base64 = ProfilePicDecoder.encodeImage(bitmap);
        }
        catch (Exception e){
            //if any exception occur in converting image then set default image
            String imageString = "";
            String name = firstName+" "+lastName;
            String[] names = name.trim().split(" ");

            for(int count = 0; count<names.length; count++){
                imageString = imageString+names[count].charAt(0)+"";
                if(count==1){
                    break;
                }
            }
            imageString = imageString.toString().toUpperCase();
            bitmap = UserImage.getBitmapFromString(imageString, 32.0f, context);
            image_base64 = ProfilePicDecoder.encodeImage(bitmap);
        }


        //setting the data which you want to post
        try {
//            String fbiddecrypt = encryptDecrypt.Decrypt(fbid, EncryptDecrypt.SECRET_KEY);
            postData.put(ConfigurationConstant.nickname, nickname);
            postData.put(ConfigurationConstant.password, password);
            postData.put(ConfigurationConstant.email, email);
            postData.put(ConfigurationConstant.name, firstName);
            postData.put(ConfigurationConstant.surname, lastName);
            postData.put(ConfigurationConstant.birthDate, birthDate);
            postData.put(ConfigurationConstant.sex, sex );
            postData.put(ConfigurationConstant.nationId, nationId );
            try {
                postData.put(ConfigurationConstant.img_link, image_base64);
                postData.put(ConfigurationConstant.img_link_thumb, image_base64);
            } catch (Exception e) {
                e.getStackTrace();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ending

        //calling service

        AsyncServiceCall asyncTask = new AsyncServiceCall(urlpost, context, new Callback() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onResult(String result) {
                switch (result) {
                    case ConfigurationConstant.post_success:
                        try {
                            callback.onSuccess(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case ConfigurationConstant.post_duplicate:
                        callback.onFailure(result);

                        break;
                    default:
                        callback.onFailure(result);
                        break;
                }

            }

            @Override
            public void onCancel() {

            }
        }, null, postData, ConfigurationConstant.POST);

        asyncTask.execute();
    }


    /**
     * Call Getuser By Nickname
     *
     * @param callback  -> Request and Response for the service Callback
     * @param nickname     -> String nickname from the user Input
     *
     */
    public static void CreateBeacon(String nickname,String uuid,String major,String minor,  Context context, final ServiceLayerCallback callback)
    {
        String urlpost = ConfigurationConstant.url_beacon_post;
        JSONObject postData = new JSONObject();


        //setting the data which you want to post
        try {
//          String fbiddecrypt = encryptDecrypt.Decrypt(fbid, EncryptDecrypt.SECRET_KEY);
            postData.put(ConfigurationConstant.nickname, nickname);
            postData.put(ConfigurationConstant.UUID, uuid);
            postData.put(ConfigurationConstant.MAJOR, major);
            postData.put(ConfigurationConstant.MINOR, minor);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ending

        //calling service

        AsyncServiceCall asyncTask = new AsyncServiceCall(urlpost, context, new Callback() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onResult(String result) {
                switch (result) {
                    case ConfigurationConstant.post_success:
                        try {
                            callback.onSuccess(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case ConfigurationConstant.post_duplicate:
                        callback.onFailure(result);

                        break;
                    default:
                        callback.onFailure(result);
                        break;
                }

            }

            @Override
            public void onCancel() {

            }
        }, null, postData, ConfigurationConstant.POST);

        asyncTask.execute();
    }


  /**
     * Call Getuser By Nickname
     *
     * @param callback  -> Request and Response for the service Callback
     * @param uuid-> Request and Response for the service Callback
     * @param major-> Request and Response for the service Callback
     * @param minor     -> String nickname from the user Input
     *
     */
    public static void GetUserDetails(String uuid,String major,String minor,  Context context, final ServiceLayerCallback callback)
    {
        String urlpost = ConfigurationConstant.url_GetUserList;
        JSONObject postData = new JSONObject();
        HashMap<Object, Object> data = new HashMap<>();

        //setting the data which you want to post
        try {
//          String fbiddecrypt = encryptDecrypt.Decrypt(fbid, EncryptDecrypt.SECRET_KEY);

            postData.put(ConfigurationConstant.UUID, uuid);
            postData.put(ConfigurationConstant.MAJOR, major);
            postData.put(ConfigurationConstant.MINOR, minor);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ending

        //calling service

        AsyncServiceCall asyncTask = new AsyncServiceCall(urlpost, context, new Callback() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onResult(String result) {
                switch (result) {
                    case ConfigurationConstant.post_success:
                        try {
                            callback.onSuccess(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case ConfigurationConstant.post_duplicate:
                        callback.onFailure(result);

                        break;
                    default:
                        callback.onFailure(result);
                        break;
                }

            }

            @Override
            public void onCancel() {

            }
        }, null, postData, ConfigurationConstant.POSTLIST);

        asyncTask.execute();
    }





}
