package userinterface;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.appiapi.R;
import com.facebook.appevents.AppEventsConstants;


import java.net.HttpURLConnection;
import java.net.URL;

import helpers.LetterSpacingTextView;
import instagramlogin.Constants;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.PreferenceConnector;
import userinterface.signup_signin.SignInFragement;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LetterSpacingTextView textView = new LetterSpacingTextView(getApplicationContext());
        textView.setLetterSpacing(10);
        setContentView(R.layout.main_layout);
        checkPreferences(savedInstanceState);
    }

    private void checkPreferences(Bundle bundle) {
        String user = PreferenceConnector.readString(getApplicationContext(), ConfigurationConstant.nickname, null);
        String pass = PreferenceConnector.readString(getApplicationContext(), ConfigurationConstant.password, null);
        if(bundle == null) {
            if (user == null && pass == null) {
                addFragment( SignInFragement.newInstance() );
            } else {
                addFragment( HomeActivity.newInstance() );
            }
        }
    }



    private void addFragment(Fragment fragment1)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, fragment1)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putBoolean();
    }
}
