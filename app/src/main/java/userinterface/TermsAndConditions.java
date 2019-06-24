package userinterface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.appiapi.R;

import helpers.SignoutFunction;
import userinterface.signup_signin.SignInFragement;

public class TermsAndConditions extends Fragment {

    Button logOutButton, backButton;
    TextView termsAndConditionsTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.terms_and_conditions, container, false);

        userInterfaceDeclaration( view );

        return view;
    }

    /*Declaration of all non static elements in the interface*/
    public void userInterfaceDeclaration(View view){
        //Declaring all dynamic textViews
        termsAndConditionsTextView = view.findViewById( R.id.terms_and_conditions_text_view );

        //Declaring all buttons requiring user interaction
        logOutButton = view.findViewById(R.id.log_out_button);
        backButton   = view.findViewById(R.id.back_button);

        userInterfaceActionListener();
    }


    /*
     * Listen to user interaction with the application*/
    private void userInterfaceActionListener() {
        logOutButton.setOnClickListener(clickListener);
        backButton.setOnClickListener(clickListener);
    }

    /*
     * Execute an action on user interaction with the application*/
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.log_out_button:
                    SignoutFunction.addFragment(getContext(), new SignInFragement(), getFragmentManager());
                    break;
                case R.id.back_button:
                    getActivity().onBackPressed();
                    break;
            }
        }
    };
}
