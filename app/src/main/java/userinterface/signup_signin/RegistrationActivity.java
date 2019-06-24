package userinterface.signup_signin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.appiapi.R;

import java.security.Key;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import networklayer.ServiceLayer;
import networklayer.interfaces.ServiceLayerCallback;
import userinterface.addgadget.BeaconActivity;
import userinterface.fragments.SignUpCompleteDialog;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.EncryptDecrypt;
import userinterface.helperconfiguration.PreferenceConnector;

//import java.util.Calendar;


public class RegistrationActivity extends Fragment {


    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog datePickerDialog;
    private EditText firstName, lastName, dateOfBirth;
    private Button submit;
    private RadioGroup gender;
    private ProgressDialog dialog;

    private String url, str_gender, username, password, email, image;
    private Spinner nationality;
    private Button skip;
    private Calendar calendar;
    private Context mContext=getContext();



    private final String[] nationalities = {ConfigurationConstant.UAE, ConfigurationConstant.USA,};

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.registration,container,false);


        /*username= PreferenceConnector.readString(getContext(),ConfigurationConstant.nickname,null);
        password=PreferenceConnector.readString(getContext(),ConfigurationConstant.password,null);
        email=PreferenceConnector.readString(getContext(),ConfigurationConstant.email,null);*/
        Bundle bundle = getArguments();
        username = bundle.getString( ConfigurationConstant.nickname );
        password = bundle.getString( ConfigurationConstant.password );
        email    = bundle.getString( ConfigurationConstant.email );

        EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
        password = encryptDecrypt.Encrypt(password, "P@ss");

        calendar = Calendar.getInstance();

        firstName   = v.findViewById(R.id.first_name);
        lastName    = v.findViewById(R.id.last_name);
        dateOfBirth = v.findViewById(R.id.date_of_birth);
        nationality = v.findViewById(R.id.nationality);
        gender = v.findViewById(R.id.gender);

        submit = v.findViewById(R.id.submit);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, nationalities);
        nationality.setAdapter(arrayAdapter);

        dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            dateOfBirth.setText((datePickerDialog.getDatePicker().getMonth()+1)+"/"+datePickerDialog.getDatePicker().getDayOfMonth()+"/"+datePickerDialog.getDatePicker().getYear());
        };

        gender.setOnCheckedChangeListener(checkedChangeListener);
        dateOfBirth.setOnClickListener(clickListener);
        submit.setOnClickListener(clickListener);

        return v;
    }



    final View.OnClickListener clickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.date_of_birth:
                    dateOfBirth.setEnabled(false);
//                    Log.d(TTT, DATE_OF_BIRTH);
                    //Toast.makeText(getContext(), "Date of Birth", Toast.LENGTH_SHORT).show();
                    datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                    datePickerDialog.show();
                    String dateFormat = ConfigurationConstant.DD_MM_YYYY;
                    break;

                case R.id.submit:
                    //submit.setEnabled(false);
                    String str_firstName = firstName.getText().toString();
                    String str_lastName = lastName.getText().toString();
                    String str_dateOfBirth = dateOfBirth.getText().toString();
                    String str_nationality = nationality.getSelectedItem().toString();

                    if(str_firstName.equals(""))
                    {
                        firstName.setError(ConfigurationConstant.ENTER_YOUR_FIRST_NAME);
                    }
                    if(str_lastName.equals(""))
                    {
                        lastName.setError(ConfigurationConstant.ENTER_YOUR_LAST_NAME);
                    }
                    if(dateOfBirth.equals(""))
                    {
                        dateOfBirth.setError(ConfigurationConstant.ENTER_YOUR_DATE_OF_BIRTH);
                    }
                    if(str_nationality.equals(""))
                    {
                        ((TextView)nationality.getSelectedView()).setError(ConfigurationConstant.ENTER_YOUR_NATIONALITY);
                    }
                    if(!(str_firstName.equals(""))&& !(str_lastName.equals(""))&& !(str_dateOfBirth.equals(""))&& !(str_nationality.equals(""))&& !(str_gender.equals("")))
                    {
                        dialog = new ProgressDialog(getContext());
                        dialog.setTitle("Loading");
                        dialog.setMessage("Please wait...");
                        dialog.setCancelable(false);
                        dialog.show();

                        HashMap<String, Object> dataMap = new HashMap<>();
                        dataMap.put( ConfigurationConstant.nickname, username );
                        dataMap.put( ConfigurationConstant.password, password );
                        dataMap.put( ConfigurationConstant.email, email );
                        dataMap.put( ConfigurationConstant.name, str_firstName );
                        dataMap.put( ConfigurationConstant.surname, str_lastName );
                        dataMap.put( ConfigurationConstant.img_link, image );
                        dataMap.put( ConfigurationConstant.sex, str_gender );
                        dataMap.put( ConfigurationConstant.birthDate, str_dateOfBirth );
                        dataMap.put( ConfigurationConstant.nationId, str_nationality );
                        //ServiceLayer.CreateUser( username, password, email, str_firstName, str_lastName, image, getContext(), new ServiceLayerCallback() {
                        ServiceLayer.CreateUser( dataMap, getContext(), new ServiceLayerCallback() {
                            @Override
                            public void onSuccess(String obj) {
                                //if created successfully go for beacon assignment uuid,major,minor
                                PreferenceConnector.writeString(getContext(), ConfigurationConstant.nickname, username);
                                PreferenceConnector.writeString(getContext(), ConfigurationConstant.password, password);

                                String displayName = str_firstName+" "+str_lastName;
                                displayName = displayName.contains("@") ? displayName.substring(0, displayName.indexOf("@")) : displayName;
                                PreferenceConnector.writeString( getContext(), ConfigurationConstant.name, displayName );
                                PreferenceConnector.writeString( getContext(), ConfigurationConstant.birthDate, str_dateOfBirth );
                                PreferenceConnector.writeString( getContext(), ConfigurationConstant.sex, str_gender );

                                DialogFragment signUpComplete = new SignUpCompleteDialog();
                                signUpComplete.setCancelable(false);
                                signUpComplete.show( getFragmentManager(), "SIGN_UP_COMPLETE" );
                                try {
                                    if (dialog.isShowing()) {
                                        dismissDialog(1000, dialog);
                                    }
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }
                                //addfragment(new BeaconActivity());
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
                            }
                        });

                    ;}

//                    addfrgment(new BeaconActivity());
                    break;
            }
        }
    };



    final RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.female:
                    //Toast.makeText(getContext(), "Female", Toast.LENGTH_SHORT).show();
                    str_gender="Female";
                    break;
                case R.id.male:
                    //Toast.makeText(getContext(), "Male", Toast.LENGTH_SHORT).show();
                    str_gender="Male";
                    break;
            }
        }
    };

    private void addfragment(Fragment fragment1)
    {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().replace(R.id.main, fragment1).addToBackStack(null).commit();
        }
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
}
