package userinterface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appiapi.R;

import helpers.SignoutFunction;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.PreferenceConnector;
import userinterface.signup_signin.SignInFragement;

public class ProfileScreens extends AppCompatActivity {
    Button buttonBack, buttonLogOut;
    TextView tvHeadText, tvContentText;
    LinearLayout llContent;
    RelativeLayout rlUpperLayout;
    private String screen = null, nickname;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        buttonBack   = findViewById(R.id.button_back);
        buttonLogOut = findViewById(R.id.button_log_out);

        tvHeadText    = findViewById(R.id.tv_head_text);
        tvContentText = findViewById(R.id.tv_content_text);
        llContent     = findViewById(R.id.ll_content);
        rlUpperLayout = findViewById(R.id.rl_upper_layout);

        intent = getIntent();
        screen = intent.getStringExtra("LOAD_SCREEN");

        fragmentSelection();

        buttonBack.setOnClickListener(clickListener);
        buttonLogOut.setOnClickListener(clickListener);
    }

    private void fragmentSelection(){
        if(screen.equals("TERMS_AND_CONDITIONS")){
            rlUpperLayout.setVisibility(View.GONE);
            buttonLogOut.setVisibility(View.VISIBLE);
            tvHeadText.setVisibility(View.VISIBLE);
            tvContentText.setVisibility(View.VISIBLE);
            tvHeadText.setText(R.string.terms_and_conditions);
            tvContentText.setText(R.string.terms_and_conditions_of_use);
        }
        if(screen.equals("CONTACT_INFORMATION")){
            nickname = intent.getStringExtra(ConfigurationConstant.NICKNAME);
            rlUpperLayout.setVisibility(View.VISIBLE);
            buttonLogOut.setVisibility(View.VISIBLE);
            tvHeadText.setVisibility(View.GONE);
            tvContentText.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString(ConfigurationConstant.NICKNAME, nickname);
            NearYouProfile nearYouProfile = new NearYouProfile();
            nearYouProfile.setArguments(bundle);
            addFragment( R.id.rl_upper_layout, nearYouProfile );
        }
    }

    /*
    * Implementing onclick listeners for the buttons*/
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_back:
                    onBackPressed();
                    break;
                case R.id.button_log_out:
                    //SignoutFunction.addFragment(getApplicationContext(), new SignInFragement(), getSupportFragmentManager());
                    PreferenceConnector.RemoveItem(getApplicationContext(), ConfigurationConstant.nickname);
                    PreferenceConnector.RemoveItem(getApplicationContext(), ConfigurationConstant.password);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    private void addFragment(int layout, Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(layout, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("LOAD_SCREEN", screen);
        outState.putString(ConfigurationConstant.nickname, nickname);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        screen = savedInstanceState.getString("LOAD_SCREEN");
        nickname = savedInstanceState.getString(ConfigurationConstant.nickname);
    }
}
