package userinterface.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appiapi.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.HashMap;

import instagramlogin.Constants;
import networklayer.ServiceLayer;
import networklayer.interfaces.ServiceLayerCallback;
import userinterface.HomeActivity;
import userinterface.addgadget.BeaconActivity;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.DateConverter;
import userinterface.helperconfiguration.PreferenceConnector;
import userinterface.responsemodel.BeaconResponse;
import userinterface.responsemodel.UserResponse;
import userinterface.signup_signin.SignInFragement;
import userinterface.signup_signin.SignUpFragment;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class SignUpDialog extends DialogFragment {
    Button buttonFacebook, buttonGoogle, buttonInstagram, buttonSignUp;
    private String nickname, decryptPassword, usernameText, passwordText, firstName="", lastName="", facebookId, name, encode, facebookUrl, googleId, email, url, image;
    private CallbackManager callbackManager;
    private ProgressDialog dialog;
    private LoginButton buttonFBSignUp;
    private ImageView ivCloseDialog;

    Context context;
    FragmentManager fragmentManager;
    private final int GS_SIGN_IN = 1;
    private GoogleSignInClient googleSignInClient;

    public static SignUpDialog signUpDialog;
    public static SignUpDialog newInstance(){
        signUpDialog = new SignUpDialog();
        return signUpDialog;
    }

    public static SignUpDialog createdInstance(){
        return signUpDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_dialog, container, false);
        buttonFacebook    = view.findViewById(R.id.button_facebook);
        buttonGoogle      = view.findViewById(R.id.button_google);
        buttonInstagram   = view.findViewById(R.id.button_instagram);
        buttonSignUp      = view.findViewById(R.id.button_sign_up);
        buttonFBSignUp    = view.findViewById(R.id.button_fb_sign_up);
        ivCloseDialog = view.findViewById(R.id.iv_close_dialog);

        callbackManager = SignInFragement.getInstance().callbackManager;

        clickClisteners();
        return view;
    }

    /*
    * Button Onclick Listener method call*/
    private void clickClisteners() {
        buttonFacebook.setOnClickListener(clickListener);
        buttonGoogle.setOnClickListener(clickListener);
        buttonInstagram.setOnClickListener(clickListener);
        buttonSignUp.setOnClickListener(clickListener);
        ivCloseDialog.setOnClickListener(clickListener);

        //google sign in initilization
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    /*
    * Implementing button Onclick Listeners*/
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_facebook:
                    signUpWithFacebook();
                    break;
                case R.id.button_google:
                    signUpWithGooglePlus();
                    break;
                case R.id.button_instagram:

                    break;
                case R.id.button_sign_up:
                    addFragment(new SignUpFragment());
                    break;
                case R.id.iv_close_dialog:
                    dismiss();
                    break;
            }
        }
    };

    public void addFragment(Fragment fragment1) {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, fragment1)
                    .addToBackStack(null)
                    .commit();
        }
        dismissDialog(550, this.getDialog());
    }

    public void dismissDialog(int time, Dialog dialog){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        };
        thread.start();
    }

    public void signUpWithFacebook() {

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        //myDialog.setContentView(R.layout.dialogfragment);
        LoginManager.getInstance().logOut();
        buttonFBSignUp.setReadPermissions(ConfigurationConstant.email, "public_profile");
        buttonFBSignUp.setFragment(this);

        buttonFBSignUp.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //OnCompleted is invoked once the GraphRequest is successful
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                    try {

                        firstName = object.getString(ConfigurationConstant.first_name);
                        lastName = object.getString(ConfigurationConstant.last_name);
                        facebookId = object.getString(ConfigurationConstant.id);
                        name = object.getString(ConfigurationConstant.NAME);

                        try {
                            email = object.getString(ConfigurationConstant.EMAIL);
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        nickname = ConfigurationConstant.FB + facebookId;
                        facebookUrl= ConfigurationConstant.HTTPS_GRAPH_FACEBOOK_COM + facebookId + ConfigurationConstant.PICTURE_TYPE_LARGE;

                        ServiceLayer.GetUserByNickname(nickname, getContext(), new ServiceLayerCallback() {
                            @Override
                            public void onSuccess(String obj) {
                                //parsing string to object
                                Gson gson = new Gson();
                                UserResponse responseObject = gson.fromJson(obj, UserResponse.class);

                                //if object is not null it means we get the data for specific nickname.
                                if ( responseObject != null ){

                                    String displayName = responseObject.getDisplayName();
                                    displayName = displayName.contains("@") ? displayName.substring(0, displayName.indexOf("@")) : displayName;
                                    PreferenceConnector.writeString( context, ConfigurationConstant.name, displayName );
                                    PreferenceConnector.writeString( context, ConfigurationConstant.birthDate, responseObject.getBirthDate() );
                                    PreferenceConnector.writeString( context, ConfigurationConstant.sex, responseObject.getSex() );

                                    //set username and password for already exist user
                                    String dbdecryptPassword = responseObject.getPassword();
                                    String dbNickname = responseObject.getNickname();
                                    if (dbdecryptPassword.equals(facebookId) && nickname.equals(dbNickname)){

                                        //myDialog.dismiss();
                                        ServiceLayer.GetBeaconByNickname(dbNickname, getContext(), new ServiceLayerCallback() {
                                            @Override
                                            public void onSuccess(String obj) {
                                                //parse the result to object model
                                                Gson gson1 = new Gson();
                                                BeaconResponse beaconResponse = gson1.fromJson(obj, BeaconResponse.class);

                                                PreferenceConnector.writeString(context, ConfigurationConstant.nickname, nickname);
                                                PreferenceConnector.writeString(context, ConfigurationConstant.password, facebookId);

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
                                    /*Bundle bundle = new Bundle();
                                    bundle.putString("TITLE", "Sorry");   //parameters are (key, value).
                                    bundle.putString("MESSAGE", "Your facebook account is already registered.");   //parameters are (key, value).

                                    DialogFragment alertMessage = new AlertMessage();
                                    alertMessage.setCancelable(false);
                                    alertMessage.setArguments(bundle);
                                    alertMessage.show( fragmentManager, "USER_EXISTS" );
                                    dismissDialog(1000, dialog);*/

                                } else {
                                    HashMap<String, Object> dataMap = new HashMap<>();
                                    dataMap.put(ConfigurationConstant.nickname, nickname);
                                    dataMap.put(ConfigurationConstant.password, facebookId);
                                    dataMap.put(ConfigurationConstant.email, email);
                                    dataMap.put(ConfigurationConstant.name, firstName);
                                    dataMap.put(ConfigurationConstant.surname, lastName);
                                    dataMap.put(ConfigurationConstant.img_link, image);
                                    ServiceLayer.CreateUser( dataMap, context, new ServiceLayerCallback() {
                                    //ServiceLayer.CreateUser( nickname, facebookId, email, firstName, lastName, facebookUrl, getContext(), new ServiceLayerCallback() {
                                        @Override
                                        public void onSuccess(String obj) {

                                            PreferenceConnector.writeString(context, ConfigurationConstant.nickname, nickname);
                                            PreferenceConnector.writeString(context, ConfigurationConstant.password, facebookId);

                                            String displayName = name;
                                            displayName = displayName.contains("@") ? displayName.substring(0, displayName.indexOf("@")) : displayName;
                                            PreferenceConnector.writeString( context, ConfigurationConstant.name, displayName );

                                            DialogFragment signUpComplete = new SignUpCompleteDialog();
                                            signUpComplete.setCancelable(false);
                                            signUpComplete.show( fragmentManager, "SIGN_UP_COMPLETE" );
                                            try {
                                                if (dialog.isShowing()) {
                                                    dismissDialog(1000, dialog);
                                                }
                                            } catch (Exception e) {
                                                e.getStackTrace();
                                            }

                                            //startFragment( new BeaconActivity(), Constants.BEACON_FRAGMENT_PAGE );
                                        }
                                        @Override
                                        public void onFailure(String obj) {
                                            try {
                                                if (dialog.isShowing()) {
                                                    dismissDialog(1000, dialog);
                                                }
                                            } catch (Exception e) {
                                                e.getStackTrace();
                                            }
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
                        dismissDialog(2000, dialog);
                        //Log.e("TTTT", e.getMessage()+" Exception.");
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

        buttonFBSignUp.performClick();
    }

    private void signUpWithGooglePlus() {
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GS_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        context = getContext();
        fragmentManager = getFragmentManager();
        dismiss();

        if (requestCode == GS_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
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
                        String dbDecryptPassword = userResponse.getPassword();
                        String dbNickname = userResponse.getNickname();
                        PreferenceConnector.writeString(context, ConfigurationConstant.nickname, dbNickname);
                        PreferenceConnector.writeString(context, ConfigurationConstant.password, dbDecryptPassword);

                        String dOB = ( userResponse.getBirthDate()!=null ) ? DateConverter.convertDate(userResponse.getBirthDate().substring(0,10)) : "N/A";

                        String displayName = userResponse.getDisplayName();
                        displayName = displayName.contains("@") ? displayName.substring(0, displayName.indexOf("@")) : displayName;
                        PreferenceConnector.writeString( context, ConfigurationConstant.name, displayName );
                        PreferenceConnector.writeString( context, ConfigurationConstant.birthDate, dOB );
                        PreferenceConnector.writeString( context, ConfigurationConstant.sex, userResponse.getSex() );

                        //checking crediantials
                        if ( dbDecryptPassword.equals(googleId) && nickname.equals( dbNickname )){
                            //check user exist in beacon table
                            ServiceLayer.GetBeaconByNickname( nickname, getContext(), new ServiceLayerCallback() {
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
                        HashMap<String, Object> dataHashMap = new HashMap<>();
                        dataHashMap.put( ConfigurationConstant.nickname, nickname );
                        dataHashMap.put( ConfigurationConstant.password, googleId );
                        dataHashMap.put( ConfigurationConstant.email, email );
                        dataHashMap.put( ConfigurationConstant.name, firstName );
                        dataHashMap.put( ConfigurationConstant.surname, lastName );
                        dataHashMap.put( ConfigurationConstant.img_link, image );
                        ServiceLayer.CreateUser( dataHashMap, context, new ServiceLayerCallback() {
                        //ServiceLayer.CreateUser(nickname, googleId, email, firstName, lastName, image, context, new ServiceLayerCallback() {
                            @Override
                            public void onSuccess(String obj) {
                                PreferenceConnector.writeString(context, ConfigurationConstant.nickname, nickname);
                                PreferenceConnector.writeString(context, ConfigurationConstant.password, googleId);

                                String displayName = name;
                                displayName = displayName.contains("@") ? displayName.substring(0, displayName.indexOf("@")) : displayName;
                                PreferenceConnector.writeString( context, ConfigurationConstant.name, displayName );

                                DialogFragment signUpComplete = new SignUpCompleteDialog();
                                signUpComplete.setCancelable(false);
                                signUpComplete.show( fragmentManager, "SIGN_UP_COMPLETE" );

                                try {
                                    if (dialog.isShowing()) {
                                        dismissDialog(1000, dialog);
                                    }
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }
                                startFragment(new BeaconActivity(), Constants.BEACON_FRAGMENT_PAGE);
                            }

                            @Override
                            public void onFailure(String obj) {

                                Toast.makeText(getContext(), obj, Toast.LENGTH_SHORT).show();

                            }

                        });

                    }

                }

                @Override
                public void onFailure(String obj) {

                }
            });


            //**************************************

        } catch (ApiException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startFragment( Fragment fragment1, String FragmentTag )
    {
        if (fragmentManager != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main, fragment1, FragmentTag)
                    .commit();
        }
    }
}
