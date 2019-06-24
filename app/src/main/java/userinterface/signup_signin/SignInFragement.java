package userinterface.signup_signin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import helpers.LetterSpacingTextView;
import instagramlogin.Constants;
import instagramlogin.InstagramApp;
import networklayer.AsyncServiceCall;
import networklayer.interfaces.Callback;
import networklayer.ServiceLayer;
import networklayer.interfaces.ServiceLayerCallback;
import userinterface.HomeActivity;
import userinterface.addgadget.BeaconActivity;
import userinterface.fragments.SignUpDialog;
import userinterface.helperconfiguration.DateConverter;
import userinterface.helperconfiguration.EncryptDecrypt;
import userinterface.responsemodel.BeaconResponse;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.PreferenceConnector;
import userinterface.responsemodel.UserResponse;

import com.example.appiapi.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class SignInFragement extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private String dbNickname,decryptPassword, username, password, firstName="", lastName="", facebookId, name, encode, facebookUrl, googleId,email, url, nickname,image;
    public CallbackManager callbackManager;
    private EditText etUsername, etPassword;

    private Button google,sign_in,fb;
    private GoogleSignInClient mGoogleSignInClient;
    private final int GS_SIGN_IN = 1;
    private InstagramApp mApp;
    private Dialog myDialog;
    private ProgressDialog dialog;
    private TextView gotosignup;
    private SignInButton signInButton;
    private ImageView instagram;
    private LoginButton loginButton;
    TextView closeDialog;
    //private LetterSpacingTextView text;
//    String lastgooglename = "", lastinstaname = "";
    public static SignInFragement signInFragement;

    public static SignInFragement newInstance(){
        signInFragement = new SignInFragement();
        return signInFragement;
    }

    public static SignInFragement getInstance(){
        return signInFragement;
    }

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getApplicationContext());
        View view = inflater.inflate(R.layout.sign_in_activity, container, false);
        //setInitialSavedState(savedInstanceState.);

        callbackManager = CallbackManager.Factory.create();

        //function call for setting resource and all their listner....
        InitiliazeResourceAndClickListner(view);

        //To prevent Network on main thread exception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //instagram
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {



//                Toast.makeText(getApplicationContext(), userInfoHashmap.toString(), Toast.LENGTH_SHORT).show();
                String name = mApp.getName();
                LinkedHashMap<String, String> userInfo = (LinkedHashMap<String, String>) mApp.getUserInfo();
                String image = userInfo.get(ConfigurationConstant.PROFILE_PICTURE);
                String id = mApp.getId();
                nickname = ConfigurationConstant.INSTAGRAM + id;

                String[] parts = name.split(ConfigurationConstant.S);
                String fname = parts[0];


                for (int i = 1; i < parts.length; i++) {
                    lastName = lastName.concat(parts[i]).concat(" ");

                }
                PreferenceConnector.writeString(getContext(), ConfigurationConstant.NICKNAME, nickname);
                //Uri image = account.getPhotoUrl();


                //*************************************
                String url = ConfigurationConstant.HTTP_INDB_01_DYNDNS_ORG_8963_API_USER_GET_USER;
                HashMap<Object, Object> data = new HashMap<>();

//                EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
                data.put(ConfigurationConstant.NICKNAME, nickname);

                AsyncServiceCall asyncTask = new AsyncServiceCall(url, getContext(), new Callback() {
                    @Override
                    public void onProgress() {

                    }

                    @Override
                    public void onResult(String result) {
                        Gson gson = new Gson();
                        UserResponse name = gson.fromJson(result, UserResponse.class);
                        if (name != null) {
                            Log.d(ConfigurationConstant.ACCESS, ConfigurationConstant.ACCESSEC);

                            String decryptpass = name.getPassword();
//                            String pass = encryptDecrypt.Decrypt(decryptpass, EncryptDecrypt.SECRET_KEY);
                            String nicknames = name.getNickname();
                            if (decryptpass.equals(id) && nickname.equals(nicknames)) {
                                PreferenceConnector.writeString(getContext(), ConfigurationConstant.nickname, nicknames);
                                PreferenceConnector.writeString(getContext(), ConfigurationConstant.password, decryptpass);

                                startFragment(HomeActivity.newInstance(), Constants.MAIN_FRAGMENT_PAGE);
                            } else {
                                Toast.makeText(getApplicationContext(), ConfigurationConstant.INCORRECT_USERNAME_OR_PASSWORD1, Toast.LENGTH_SHORT).show();
                                startFragment(new SignInFragement(), Constants.SIGN_IN_FRAGMENT_PAGE);
                            }
                        } else {
                            String urlpost = ConfigurationConstant.HTTP_INDB_01_DYNDNS_ORG_8963_API_USER_POST_USER;
                            JSONObject postData = new JSONObject();
//                            HashMap<Object, Object> data = new HashMap<>();

                            //nickname, password, facebookid, twittered, linkedinid, xmppid, name, surname, displayName,
                            //           age, birthDate, sex, workid, email, phoneNumber, nationId, img_link, img_link_thumb, datacreation, dataupdate, status
                            try {
                                postData.put(ConfigurationConstant.NICKNAME, nickname);
                                postData.put(ConfigurationConstant.PASSWORD, id);
                                postData.put(ConfigurationConstant.NAME, fname);
                                postData.put(ConfigurationConstant.SURNAME, lastName);
//                if(!image.toString().equals("")){
//                    postData.put("img_link",image.toString());
//                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            AsyncServiceCall asyncTask = new AsyncServiceCall(urlpost, getContext(), new Callback() {
                                @Override
                                public void onProgress() {

                                }

                                @Override
                                public void onResult(String result) {
                                    switch (result) {
                                        case ConfigurationConstant.HTTP_1_1_201_CREATED:
                                            PreferenceConnector.writeString(getContext(), ConfigurationConstant.nickname, nickname);
                                            PreferenceConnector.writeString(getContext(), ConfigurationConstant.password, id);
                                            myDialog.dismiss();
                                            startFragment(new BeaconActivity(), Constants.BEACON_FRAGMENT_PAGE);
                                            break;
                                        case ConfigurationConstant.HTTP_1_1_409_CONFLICT:
                                            Toast.makeText(getContext(), ConfigurationConstant.USER_ALREADY_EXIST, Toast.LENGTH_SHORT).show();
                                            startFragment(HomeActivity.newInstance(), Constants.MAIN_FRAGMENT_PAGE);
                                            break;
                                        default:
                                            mApp.resetAccessToken();
                                            startFragment(new SignInFragement(), Constants.SIGN_IN_FRAGMENT_PAGE);
                                            break;
                                    }

                                }

                                @Override
                                public void onCancel() {

                                }
                            }, null, postData, ConfigurationConstant.POST);

                            asyncTask.execute();

                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                }, data, null, ConfigurationConstant.GET);

                asyncTask.execute();


                //***************************************


            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return view;
    }



    private void InitiliazeResourceAndClickListner(View view) {
        etUsername = view.findViewById(R.id.username);
        etPassword = view.findViewById(R.id.password);
        sign_in = view.findViewById(R.id.sign_in);
        gotosignup = view.findViewById(R.id.donthave);
        fb = view.findViewById(R.id.fb);
        google = view.findViewById(R.id.google);
        signInButton = view.findViewById(R.id.sign_in_button);
        instagram = view.findViewById(R.id.instagram);
        loginButton = view.findViewById(R.id.login_button);
        //text = view.findViewById(R.id.hooke_sign_in);

        //text.setLetterSpacing(0.00f);
        google.setOnClickListener(cor);
        signInButton.setOnClickListener(cor);
        fb.setOnClickListener(cor);
        sign_in.setOnClickListener(cor);
        gotosignup.setOnClickListener(cor);
        instagram.setOnClickListener(cor);

        //insta
        mApp = new InstagramApp(getActivity(), Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.CALLBACK_URL);

        //initilize dialogue
        myDialog = new Dialog(getContext());

        //google sign in initilization
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

    }


//    public void FacebookButton(View view) {
//        if (view == fb) {
//
//            ShowPopup();
//
//        }
//    }

//    public void GoogleButton(View view) {
//        if (view == google) {
//            signIn();
//        }
//    }


    final View.OnClickListener cor = v -> {
        switch (v.getId()) {
            case R.id.sign_in:
                login();
                break;
            case R.id.donthave:
                signUpPopup();
                break;
            case R.id.fb:
                signInWithFacebook();
                break;

            case R.id.google:
                signInWithGooglePlus();
                break;
            case R.id.instagram:
                Toast.makeText(getContext(), ConfigurationConstant.COMMING_SOON, Toast.LENGTH_SHORT).show();
//                    mApp.resetAccessToken();
//                    if (!mApp.hasAccessToken()) {
//                        mApp.authorize();
//                    }
                break;
        }
    };

    private void login() {

        dialog = ProgressDialog.show(getContext(), ConfigurationConstant.PLEASE_WAIT, ConfigurationConstant.PAGE_IS_LOADING);
        dialog.setIndeterminate(true);
        dialog.setIcon(R.mipmap.autograph_icon_new);
        dialog.setCancelable(false);

        //get value from edittext to string
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
        password = encryptDecrypt.Encrypt(password, "P@ss");

        if (username.equals("")) {
            etUsername.setError(ConfigurationConstant.ENTER_CORRECT_USER_NAME);
            dismissDialog(1200, dialog);
        }
        else if (password.equals("")) {
            etPassword.setError(ConfigurationConstant.ENTER_CORRECT_PASSWORD);
            dismissDialog(1200, dialog);
        }
        else if (username.length() > 0 && password.length() > 0) {

        ServiceLayer.GetUserByNickname(username, getContext(), new ServiceLayerCallback() {
            @Override
            public void onSuccess(String obj) {
                Gson gson = new Gson();
                UserResponse userdetails = gson.fromJson(obj, UserResponse.class);

                if (userdetails != null){
                    dbNickname=userdetails.getNickname();
                    decryptPassword=userdetails.getPassword();
                    PreferenceConnector.writeString(getContext(), ConfigurationConstant.nickname, dbNickname);
                    PreferenceConnector.writeString(getContext(), ConfigurationConstant.password, decryptPassword);

                    String dOB = (userdetails.getBirthDate()!=null) ? DateConverter.convertDate(userdetails.getBirthDate().substring(0,10)) : "N/A";

                    String displayName = userdetails.getName()+" "+userdetails.getSurname();
                    displayName = displayName.contains("@") ? displayName.substring(0, displayName.indexOf("@")) : displayName;
                    PreferenceConnector.writeString( getContext(), ConfigurationConstant.name, displayName );
                    PreferenceConnector.writeString( getContext(), ConfigurationConstant.birthDate, dOB );
                    PreferenceConnector.writeString( getContext(), ConfigurationConstant.sex, userdetails.getSex() );

                    if ( decryptPassword.equals( password ) && dbNickname.equals( username ) ){
                        //credientials match

                        ServiceLayer.GetBeaconByNickname( dbNickname, getContext(), new ServiceLayerCallback() {
                            @Override
                            public void onSuccess(String obj) {

                                Gson gson = new Gson();
                                BeaconResponse beaconResponse = gson.fromJson(obj, BeaconResponse.class);
                                if (beaconResponse.getNickname() != null) {
                                    //this user already assign uuid major minor in beacon table . take it to home page
                                    startFragment(HomeActivity.newInstance(), Constants.MAIN_FRAGMENT_PAGE);
                                } else {
                                    //nickname present in user table but not in beacon , go to beacon table for assigning uuid major minor
                                    startFragment(new BeaconActivity(), Constants.BEACON_FRAGMENT_PAGE);
                                }
                                dismissDialog(1200, dialog);

                            }

                            @Override
                            public void onFailure(String obj) {
                                dismissDialog(1200, dialog);
                            }
                        });

                    }
                    else {
                        //enter details do not match
                        dismissDialog(1200, dialog);
                        etUsername.setError(ConfigurationConstant.INCORRECT_USERNAME_OR_PASSWORD);
                    }

                }
                else{
                    //user not exist show message
                    dismissDialog(1200, dialog);
                    Toast.makeText(getApplicationContext(), ConfigurationConstant.USER_NOT_EXIST, Toast.LENGTH_SHORT).show();
                }

                }

                @Override
                public void onFailure(String obj) {
                    dismissDialog(1200, dialog);
                }
            });
        }
    }


    public void signUpPopup() {
        DialogFragment signUpDialog = new SignUpDialog();
        signUpDialog.show(getFragmentManager(), "SIGN_UP_DIALOG");
        signUpDialog.setCancelable(false);
        /*myDialog.setContentView(R.layout.signup_dialog);
        Button signUpButton = myDialog.findViewById(R.id.button_sign_up);
        TextView txtclose = myDialog.findViewById(R.id.close_dialog);
        txtclose.setOnClickListener(v1 -> myDialog.dismiss());
        signUpButton.setOnClickListener(cor);
        myDialog.show();*/
    }


    public void signInWithFacebook() {

        dialog = ProgressDialog.show(getContext(), ConfigurationConstant.PLEASE_WAIT, ConfigurationConstant.PAGE_IS_LOADING);
        dialog.setIndeterminate(true);
        dialog.setIcon(R.mipmap.autograph_icon_new);
        dialog.setCancelable(false);

        //myDialog.setContentView(R.layout.dialogfragment);
        LoginManager.getInstance().logOut();
        //LoginButton loginButton = myDialog.findViewById(R.id.login_button);
        loginButton.setReadPermissions(ConfigurationConstant.email, "public_profile", "user_friends");
        loginButton.setFragment(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //OnCompleted is invoked once the GraphRequest is successful
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    try {
                        Log.e("TTTT", "Login Successful");
                        firstName=object.getString(ConfigurationConstant.first_name);
                        lastName=object.getString(ConfigurationConstant.last_name);
                        facebookId=object.getString(ConfigurationConstant.id);
                        name = object.getString(ConfigurationConstant.NAME);
                        try {
                            email = object.getString(ConfigurationConstant.EMAIL);
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        nickname = ConfigurationConstant.FB + facebookId;
                        PreferenceConnector.writeString(getContext(), ConfigurationConstant.nickname, nickname);
                        PreferenceConnector.writeString(getContext(), ConfigurationConstant.password, facebookId);
                        facebookUrl= ConfigurationConstant.HTTPS_GRAPH_FACEBOOK_COM + facebookId + ConfigurationConstant.PICTURE_TYPE_LARGE;
//                            bitmap = getBitmapProfilePicture(fburl);
//                            encode = encodeImage(bitmap);

                        ServiceLayer.GetUserByNickname(nickname, getContext(), new ServiceLayerCallback() {
                            @Override
                            public void onSuccess(String obj) {
                                //parsing string to object
                                Gson gson = new Gson();
                                UserResponse userResponse = gson.fromJson(obj, UserResponse.class);

                                //if object is not null it means we get the data for specific nickname.
                                if (userResponse != null){

                                    String dOB = (userResponse.getBirthDate()!=null) ? DateConverter.convertDate(userResponse.getBirthDate().substring(0,10)) : "N/A";

                                    String displayName = userResponse.getDisplayName();
                                    displayName = displayName.contains("@") ? displayName.substring(0, displayName.indexOf("@")) : displayName;
                                    PreferenceConnector.writeString( getContext(), ConfigurationConstant.name, displayName );
                                    PreferenceConnector.writeString( getContext(), ConfigurationConstant.birthDate, dOB );
                                    PreferenceConnector.writeString( getContext(), ConfigurationConstant.sex, userResponse.getSex() );

                                    //set username and password for already exist user
                                    decryptPassword = userResponse.getPassword();
                                    dbNickname = userResponse.getNickname();
                                    if (decryptPassword.equals(facebookId) && nickname.equals( dbNickname )){

                                        //myDialog.dismiss();
                                        ServiceLayer.GetBeaconByNickname(dbNickname, getContext(), new ServiceLayerCallback() {
                                            @Override
                                            public void onSuccess(String obj) {
                                                //parse the result to object model
                                                Gson gson1 = new Gson();
                                                BeaconResponse beaconResponse = gson1.fromJson(obj, BeaconResponse.class);

                                                //if beaconResponse.getNickname() is not null its means this nickname already exist in database and UUID , major,minor is already assign
                                                if (beaconResponse.getNickname() != null){
                                                    //in this case move to home page which is HomeActivity
                                                    startFragment(HomeActivity.newInstance(), Constants.MAIN_FRAGMENT_PAGE);
                                                    try {
                                                        if (dialog.isShowing()) {
                                                            dismissDialog(1200, dialog);
                                                        }
                                                    } catch (Exception e) {
                                                        e.getStackTrace();
                                                    }
                                                }
                                                else {
//                                                  //otherwise go to this class to Assign UUID,major,minor for that nickname
                                                    startFragment(new BeaconActivity(), Constants.BEACON_FRAGMENT_PAGE);
                                                    try {
                                                        if (dialog.isShowing()) {
                                                            dismissDialog(1200, dialog);
                                                        }
                                                    } catch (Exception e) {
                                                        e.getStackTrace();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(String obj) {
                                                try {
                                                    if (dialog.isShowing()) {
                                                        dismissDialog(1200, dialog);
                                                    }
                                                } catch (Exception e) {
                                                    e.getStackTrace();
                                                }
                                            }
                                        });

                                    }


                                } else {
                                    HashMap<String, Object> dataMap = new HashMap<>();
                                    dataMap.put(ConfigurationConstant.nickname, username);
                                    dataMap.put(ConfigurationConstant.password, password);
                                    dataMap.put(ConfigurationConstant.email, email);
                                    dataMap.put(ConfigurationConstant.name, firstName);
                                    dataMap.put(ConfigurationConstant.surname, lastName);
                                    dataMap.put(ConfigurationConstant.img_link, image);
                                    ServiceLayer.CreateUser(dataMap, getContext(), new ServiceLayerCallback() {
                                    //ServiceLayer.CreateUser(nickname, fbid, email, fname, lname, fburl, getContext(), new ServiceLayerCallback() {
                                        @Override
                                        public void onSuccess(String obj) {
                                            startFragment(new BeaconActivity(), Constants.BEACON_FRAGMENT_PAGE);
                                        }

                                        @Override
                                        public void onFailure(String obj) {
                                            Log.e("TTTT", obj+" Failure");
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onFailure(String obj) {
                                Log.e("TTTT", obj+" Failure");
                            }
                        });


                    } catch (JSONException e) {
                        Log.e("TTTT", e.getMessage()+" Exception.");
                    }finally {

                    }


                });
                // We set parameters to the GraphRequest using a Bundle.
                Bundle parameters = new Bundle();
                parameters.putString(ConfigurationConstant.FIELDS, ConfigurationConstant.ID_NAME_EMAIL_PICTURE_WIDTH_200_FIRST_NAME_LAST_NAME_BIRTHDAY);
                request.setParameters(parameters);
                // Initiate the GraphRequest
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Log.e("TTTT", "Cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("TTTT", exception.getMessage()+" exception.");
            }
        });


        //ImageView txtclose = myDialog.findViewById(R.id.iv_closehappydlgmain);

        //txtclose.setOnClickListener(v1 -> myDialog.dismiss());
        //myDialog.show();
        loginButton.performClick();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                    if(dialog.isShowing())
                        dialog.dismiss();
                    //Toast.makeText(getContext(), "Connection is taking longer than expected, please check your network connectivity.", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    public void dismissDialog(int time, Dialog dialog){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(time);
                    if(dialog.isShowing())
                        dialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void signInWithGooglePlus() {
        dialog = ProgressDialog.show(getContext(), ConfigurationConstant.PLEASE_WAIT, ConfigurationConstant.PAGE_IS_LOADING);
        dialog.setIndeterminate(true);
        dialog.setIcon(R.mipmap.autograph_icon_new);
        dialog.setCancelable(false);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GS_SIGN_IN);
    }


    public void addFragment(Fragment fragment1) {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, fragment1)
                    .addToBackStack(null)
                    .commit();
        }
    }
    private void startFragment(Fragment fragment1, String FragmentTag)
    {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, fragment1, FragmentTag)
                    .commit();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GS_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult( task );
            }
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            Toast.makeText(getApplicationContext(), account.getDisplayName() + account.getGivenName() + "\n" + account.getEmail(), Toast.LENGTH_LONG).show();
            googleId = account != null ? account.getId() : null;
            nickname = ConfigurationConstant.GOOGLE + googleId;
            name = account != null ? account.getDisplayName() : null;
            String[] fullname = name.split(ConfigurationConstant.S);
            firstName = fullname[0];


            for (int i = 1; i < fullname.length; i++) {
                lastName = lastName.concat(fullname[i]).concat(" ");

            }

            try {
                image = account != null ? account.getPhotoUrl().toString() : null;

            }
            catch (Exception e) { e.getStackTrace(); }


            PreferenceConnector.writeString(getContext(), ConfigurationConstant.img_link, image);


            //Uri image = account.getPhotoUrl();
            try {
                email = account != null ? account.getEmail() : null;
            } catch (Exception e) {
                e.getStackTrace();
            }
            //***********************************Get User**************************************************************
            ServiceLayer.GetUserByNickname(nickname, getContext(), new ServiceLayerCallback() {
                @Override
                public void onSuccess(String obj) {
                    Gson gson = new Gson();
                    UserResponse userResponse = gson.fromJson(obj, UserResponse.class);
                    if (userResponse != null)
                    {

                        //user exist already
                        decryptPassword = userResponse.getPassword();
                        dbNickname = userResponse.getNickname();
                        PreferenceConnector.writeString(getContext(), ConfigurationConstant.nickname, dbNickname);
                        PreferenceConnector.writeString(getContext(), ConfigurationConstant.password, decryptPassword);

                        String dOB = (userResponse.getBirthDate()!=null) ? DateConverter.convertDate(userResponse.getBirthDate().substring(0,10)) : "N/A";

                        String displayName = userResponse.getDisplayName();
                        displayName = displayName.contains("@") ? displayName.substring(0, displayName.indexOf("@")) : displayName;
                        PreferenceConnector.writeString( getContext(), ConfigurationConstant.name, displayName );
                        PreferenceConnector.writeString( getContext(), ConfigurationConstant.birthDate, dOB );
                        PreferenceConnector.writeString( getContext(), ConfigurationConstant.sex, userResponse.getSex() );

                        //checking crediantials
                        if ( decryptPassword.equals(googleId) && nickname.equals( dbNickname ) ){
                            //check user exist in beacon table
                            ServiceLayer.GetBeaconByNickname( dbNickname, getContext(), new ServiceLayerCallback() {
                                @Override
                                public void onSuccess(String obj) {
                                    Gson gson = new Gson();
                                    BeaconResponse beaconResponse = gson.fromJson(obj, BeaconResponse.class);

                                    if (beaconResponse.getNickname() != null) {
                                        //user exist in beacon table go to home
                                        startFragment(HomeActivity.newInstance(), Constants.MAIN_FRAGMENT_PAGE);
                                    } else {
                                        //if not exist go to beacon create
                                        startFragment(new BeaconActivity(), Constants.BEACON_FRAGMENT_PAGE);
                                    }
                                    try {
                                        if (dialog.isShowing()) {
                                            dismissDialog(1200, dialog);
                                        }
                                    } catch (Exception e) {
                                        e.getStackTrace();
                                    }

                                }

                                @Override
                                public void onFailure(String obj) {
                                    try {
                                        if (dialog.isShowing()) {
                                            dismissDialog(1200, dialog);
                                        }
                                    } catch (Exception e) {
                                        e.getStackTrace();
                                    }
                                }
                            });

                        }
                    }

                    else{
                        //user not exist create
                        HashMap<String, Object> dataMap = new HashMap<>();
                        dataMap.put(ConfigurationConstant.nickname, nickname);
                        dataMap.put(ConfigurationConstant.password, googleId);
                        dataMap.put(ConfigurationConstant.email, email);
                        dataMap.put(ConfigurationConstant.name, firstName);
                        dataMap.put(ConfigurationConstant.surname, lastName);
                        dataMap.put(ConfigurationConstant.img_link, image);
                        ServiceLayer.CreateUser(dataMap, getContext(), new ServiceLayerCallback() {
                        //ServiceLayer.CreateUser(nickname, googleId, email, firstName, lastName, image, getContext(), new ServiceLayerCallback() {
                            @Override
                            public void onSuccess(String obj) {
                                PreferenceConnector.writeString(getContext(), ConfigurationConstant.nickname, nickname);

                                PreferenceConnector.writeString(getApplicationContext(), ConfigurationConstant.password, googleId);

                                startFragment(new BeaconActivity(), Constants.BEACON_FRAGMENT_PAGE);
                                try {
                                    if (dialog.isShowing()) {
                                        dismissDialog(1200, dialog);
                                    }
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String obj) {
                                try {
                                    if (dialog.isShowing()) {
                                        dismissDialog(1200, dialog);
                                    }
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }

                                Toast.makeText(getContext(), obj, Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                }

                @Override
                public void onFailure(String obj) {
                    try {
                        if (dialog.isShowing()) {
                            dismissDialog(1200, dialog);
                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                    }

                }
            });


            //**************************************

        } catch (ApiException e) {
            try {
                if (dialog.isShowing()) {
                    dismissDialog(1200, dialog);
                }
            } catch (Exception exc) {
                exc.getStackTrace();
            }
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



}