package userinterface.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.appiapi.R;

import instagramlogin.Constants;
import userinterface.HomeActivity;
import userinterface.addgadget.BeaconActivity;

public class SignUpCompleteDialog extends DialogFragment {

    private TextView tvPeopleAround, tvHookeGadget;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up_complete, container, false);

        tvPeopleAround = view.findViewById(R.id.tv_people_around);
        tvHookeGadget  = view.findViewById(R.id.tv_hooke_gadget);

        onClickListener();

        return view;
    }

    private void onClickListener() {
        tvPeopleAround.setOnClickListener(clickListener);
        tvHookeGadget.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_people_around:
                    startFragment( HomeActivity.newInstance(), Constants.MAIN_FRAGMENT_PAGE );
                    dismissDialog(500);
                    break;
                case R.id.tv_hooke_gadget:
                    startFragment( new BeaconActivity(), Constants.BEACON_FRAGMENT_PAGE );
                    dismissDialog(500);
                    break;
            }
        }
    };



    public void dismissDialog(int time){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        };
        thread.start();
    }


    private void startFragment(Fragment fragment1, String FragmentTag) {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, fragment1, FragmentTag)
                    .commit();
        }
    }

}
