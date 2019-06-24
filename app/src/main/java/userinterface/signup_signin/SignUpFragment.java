package userinterface.signup_signin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appiapi.R;


import org.json.JSONException;

import helpers.*;
import networklayer.ServiceLayer;
import networklayer.interfaces.ServiceLayerCallback;
import userinterface.fragments.AlertMessage;
import userinterface.fragments.SignUpDialog;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.PreferenceConnector;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private EditText username, password, confirmpassword, email;
    private String str_username, str_password, str_confirmpassword, str_email;
    String emailPattern = ConfigurationConstant.A_Z_A_Z_0_9_A_Z_A_Z;
    Button signUp;
    Fragment fragment;
    private ProgressDialog dialog;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up_fragment, container, false);

        //initializing resources
        InitializingResources(view);

        return view;
    }

    /*
    * Initializing view and setting on click Listeners*/
    private void InitializingResources(View view) {

        //initializing edittext
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        confirmpassword = view.findViewById(R.id.confirmpassword);
        email = view.findViewById(R.id.email);
        signUp = view.findViewById(R.id.sign_up_btn);

        new LetterSpacingTextView(getContext());

        //setting click listner
        signUp.setOnClickListener(this);
        //SignUpDialog.createdInstance().dismiss();
    }

    public void addFragment(Fragment fragment1) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main, fragment1)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_up_btn:
                submit_signup();
                break;
        }
    }



    public void dismissDialog(int time, Dialog dialog){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(time);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void submit_signup() {

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        str_username = username.getText().toString();
        str_password = password.getText().toString();
        str_confirmpassword = confirmpassword.getText().toString();
        str_email = email.getText().toString();

        if (str_username.equals("")) {
            username.setError(ConfigurationConstant.ENTER_CORRECT_USER_NAME);
            dismissDialog(1200, dialog);
        } else if (str_password.equals("")) {
            password.setError(ConfigurationConstant.ENTER_CORRECT_PASSWORD);
            dismissDialog(1200, dialog);
        } else if (!(str_password.equals(str_confirmpassword))) {
            password.setError(ConfigurationConstant.PASSWORD_NOT_MATCHING);
            confirmpassword.setError(ConfigurationConstant.PASSWORD_NOT_MATCHING);
            dismissDialog(1200, dialog);
        }
//                    else if(!(Patterns.EMAIL_ADDRESS.matcher(str_email).matches()))
//                    {
//                        email.setError("Enter Correct Email");
//                    }
        else if (str_username.length() > 0 && str_password.length() > 4 && str_password.equals(str_confirmpassword) && str_email.length() > 0) {
            ServiceLayer.GetUserByNickname( str_username, getContext(), new ServiceLayerCallback() {
                @Override
                public void onSuccess(String obj) throws JSONException {
                    if( obj == null || obj.equals("") ) {
                        fragment = new RegistrationActivity();
                        Bundle bundle = new Bundle();
                        bundle.putString(ConfigurationConstant.nickname, str_username);   //parameters are (key, value).
                        bundle.putString(ConfigurationConstant.password, str_password);
                        bundle.putString(ConfigurationConstant.email, str_email);
                        fragment.setArguments( bundle );
                        /*PreferenceConnector.writeString(getContext(), ConfigurationConstant.nickname, str_username);
                        PreferenceConnector.writeString(getContext(), ConfigurationConstant.password, str_password);
                        PreferenceConnector.writeString(getContext(), ConfigurationConstant.email, str_email);*/
                        addFragment(fragment);
                        dismissDialog(1200, dialog);
                    }else {
                        Bundle bundle = new Bundle();
                        bundle.putString("TITLE", "Sorry");   //parameters are (key, value).
                        bundle.putString("MESSAGE", "Your Username already exists please select a different one.");   //parameters are (key, value).

                        DialogFragment alertMessage = new AlertMessage();
                        alertMessage.setCancelable(false);
                        alertMessage.setArguments(bundle);
                        alertMessage.show(getFragmentManager(), "USER_EXISTS");
                        dismissDialog(1000, dialog);
                    }
                }

                @Override
                public void onFailure(String obj) {
                    dismissDialog(1200, dialog);
                    username.setError(ConfigurationConstant.USERNAME_ALREADY_EXISTS);
                }
            });

        }else{
            dismissDialog(1200, dialog);
            Toast.makeText(getContext(), "Your password must be greater than 4 characters", Toast.LENGTH_SHORT).show();
        }
    }
}
