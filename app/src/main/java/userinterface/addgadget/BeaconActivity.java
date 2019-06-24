package userinterface.addgadget;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.appiapi.R;

import java.util.Random;

import helpers.SignoutFunction;
import networklayer.ServiceLayer;
import networklayer.interfaces.ServiceLayerCallback;
import userinterface.HomeActivity;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.PreferenceConnector;
import userinterface.signup_signin.SignInFragement;

public class BeaconActivity extends Fragment {

    private static final String UUIDk = ConfigurationConstant.UUID_Broadcast;
    private String nickname;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.beacon_layout, container, false);
        nickname = PreferenceConnector.readString(getContext(), ConfigurationConstant.nickname, null);
        ImageView startCamera = v.findViewById(R.id.start_camera);
        Button addGadget = v.findViewById(R.id.add_gadget);
        Button logOut = v.findViewById(R.id.logout_button);
        Button setAsBeacon = v.findViewById(R.id.set_as_beacon);

        setAsBeacon.setOnClickListener(clickListener);
        addGadget.setOnClickListener(clickListener);
        logOut.setOnClickListener(clickListener);

        return v;

    }


    private final View.OnClickListener clickListener = v -> {
        switch (v.getId()) {
            case R.id.set_as_beacon:
                addBeaconDB();
                break;
            case R.id.logout_button:
                SignoutFunction.addFragment(getContext(), new SignInFragement(), getFragmentManager());
                break;
            case R.id.add_gadget:
//                Intent intent = new Intent(getContext(), ChattingStart.class);
//                startActivity(intent);
                //addfragment(new HomeActivity());
                break;
        }


    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addBeaconDB() {

        checkMajMin();



    }

    private void addfragment(Fragment fragment1) {
        getFragmentManager().beginTransaction().replace(R.id.main, fragment1).commit();
    }

    private int incrementMinor() {
//        Random random = new Random();
        Random random = new Random();

        return random.nextInt(10000)+1;
    }

    private int incrementMajor() {
        Random random = new Random();


        return random.nextInt(10000)+1;


    }


    private void checkMajMin() {
        try {
            int major1 = 0;
            int major2 = incrementMajor() + major1;
            int minor1 = 0;
            int minor2 = incrementMinor() + minor1;


            ServiceLayer.CreateBeacon(nickname, UUIDk, String.valueOf(major2), String.valueOf(minor2), getContext(), new ServiceLayerCallback() {
                @Override
                public void onSuccess(String obj) {
                    //if created successfully go to home page
                    addfragment(HomeActivity.newInstance());
                }

                @Override
                public void onFailure(String obj) {
                    //if fail for any reason generate new random major minor and try to add again
                    checkMajMin();
                }
            });

        } catch (Exception e) {
            e.getStackTrace();
        }

    }
}
