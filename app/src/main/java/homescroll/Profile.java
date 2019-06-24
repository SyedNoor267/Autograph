package homescroll;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.appiapi.R;

import java.util.ArrayList;
import java.util.HashMap;

import chatbox.ChatLocalStorage;
import helpers.SignoutFunction;
import ibeacon.detection.ChatContact;
import instagramlogin.Constants;
import userinterface.HomeActivity;
import userinterface.ProfileScreens;
import userinterface.TermsAndConditions;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.DateConverter;
import userinterface.helperconfiguration.PreferenceConnector;
import userinterface.signup_signin.SignInFragement;


//import com.example.beacon.R;

public class Profile extends Fragment {

    TextView numberOfFriendsTextView, nameTextView, dateOfBirthTextView, genderTextView;
    Button editProfileButton, privacyButton, familiesHookeButton, termsAndConditionsButton, logOutButton;
    View view;

    public static Profile newInstance(){
        return new Profile();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile, container, false);

        userInterfaceDeclaration(view);

        return view;
    }

    /*Declaration of all non static elements in the interface*/
    public void userInterfaceDeclaration(View view){
        //Declaring all dynamic textViews
        numberOfFriendsTextView = view.findViewById( R.id.number_of_friends_text_view );
        nameTextView            = view.findViewById( R.id.name_text_view );
        dateOfBirthTextView     = view.findViewById( R.id.date_of_birth_text_view );
        genderTextView          = view.findViewById( R.id.gender_text_view );

        //Declaring all buttons requiring user interaction
        editProfileButton        = view.findViewById(R.id.edit_profile_button);
        privacyButton            = view.findViewById(R.id.privacy_button);
        familiesHookeButton      = view.findViewById(R.id.families_hooke_button);
        termsAndConditionsButton = view.findViewById(R.id.terms_and_conditions_button);
        logOutButton             = view.findViewById(R.id.log_out_button);
        getProfile();

        userInterfaceActionListener();
    }

    /*
    * Get The Saved Profile Details From Preference Connector*/
    private void getProfile(){
        String dob = PreferenceConnector.readString(getContext(), ConfigurationConstant.birthDate, "N/A");
        dob = dob!="N/A" ? DateConverter.convertDate(dob) : dob;
        nameTextView.setText(PreferenceConnector.readString(getContext(), ConfigurationConstant.name, "N/A"));
        dateOfBirthTextView.setText(dob);
        genderTextView.setText(PreferenceConnector.readString(getContext(), ConfigurationConstant.sex, "N/A"));

        ChatLocalStorage chatLocalStorage = new ChatLocalStorage(getContext());
        chatLocalStorage.createTableChatList();
        ArrayList<String> chatList = chatLocalStorage.getDistinctNickname();
        int totalContacts = chatList.size();
        numberOfFriendsTextView.setText(totalContacts+"");
    }

    /*
     * Listen to user interaction with the application*/
    private void userInterfaceActionListener() {
        editProfileButton.setOnClickListener(clickListener);
        privacyButton.setOnClickListener(clickListener);
        familiesHookeButton.setOnClickListener(clickListener);
        termsAndConditionsButton.setOnClickListener(clickListener);
        logOutButton.setOnClickListener(clickListener);
    }

    /*
     * Execute an action on user interaction with the application*/
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.edit_profile_button:

                    break;
                case R.id.privacy_button:

                    break;
                case R.id.families_hooke_button:

                    break;
                case R.id.terms_and_conditions_button:
                    Intent intent = new Intent(getContext(), ProfileScreens.class);
                    intent.putExtra(Constants.LOAD_SCREEN, Constants.TERMS_AND_CONDITIONS);
                    startActivity(intent);
                    break;
                case R.id.log_out_button:
                    //getFragmentManager().beginTransaction().remove(HomeActivity.getInstance()).commit();
                    SignoutFunction.addFragment(getContext(), new SignInFragement(), getFragmentManager());
                    break;
            }
        }
    };

    public void addFragment(Fragment fragment){
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onResume() {
        super.onResume();
        userInterfaceDeclaration(view);
    }
}
