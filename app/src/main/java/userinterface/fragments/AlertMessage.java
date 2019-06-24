package userinterface.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.appiapi.R;

public class AlertMessage extends DialogFragment {

    private View alertView;
    private TextView tvAlertTitle, tvAlertMessage;
    private Button buttonOkay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        alertView = inflater.inflate(R.layout.alert_message, container, false);

        tvAlertTitle = alertView.findViewById(R.id.tv_alert_title);
        tvAlertMessage = alertView.findViewById(R.id.tv_alert_message);
        buttonOkay = alertView.findViewById(R.id.button_okay);

        getActivityInfo();

        buttonOkay.setOnClickListener(clickListener);

        return alertView;
    }

    private void getActivityInfo(){
        Bundle bundle = getArguments();
        String title = bundle.getString("TITLE");
        String message = bundle.getString("MESSAGE");

        tvAlertTitle.setText(title);
        tvAlertMessage.setText(message);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getDialog().dismiss();
        }
    };


}
