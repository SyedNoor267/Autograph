package userinterface;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appiapi.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import helpers.UserDetails;
import networklayer.ServiceLayer;
import networklayer.interfaces.HttpContactLayer;
import networklayer.interfaces.ServiceLayerCallback;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import userinterface.chat.ChattingStart;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.responsemodel.UserImage;
import userinterface.responsemodel.UserResponse;

public class NearYouProfile extends Fragment {

    TextView nameTextView, locationTextView, ageTextView, languagTextView, hobbyTextView;
    Button connectButton;
    ImageView profileImageView;

    String nickname, imageThumbnail, displayName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.near_you_profile, container, false);
        userInterfaceDeclaration(view);
        return view;
    }

    /*Declaration of all non static elements in the interface*/
    public void userInterfaceDeclaration(View view){
        //Declaring all dynamic textViews
        nameTextView     = view.findViewById( R.id.name_text_view );
        locationTextView = view.findViewById( R.id.location_text_view );
        ageTextView      = view.findViewById( R.id.age_text_view );
        languagTextView  = view.findViewById( R.id.language_text_view );
        hobbyTextView    = view.findViewById( R.id.hobby_text_view );

        //Declaring all buttons requiring user interaction
        connectButton    = view.findViewById(R.id.connect_button);

        //Image view in the application
        profileImageView = view.findViewById(R.id.profile_image_view);

        Intent intent    = getActivity().getIntent();
        nickname  = intent.getStringExtra(ConfigurationConstant.NICKNAME);
        if(nickname!= null)
            getProfileInformation(nickname);

        userInterfaceActionListener();
    }

    /*
     * Listen to user interaction with the application*/
    private void userInterfaceActionListener() {
        connectButton.setOnClickListener(clickListener);
    }

    /*
    * Execute an action on user interaction with the application*/
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.connect_button:
                    Intent intent=new Intent(getContext(), ChattingStart.class);
                    intent.putExtra(ConfigurationConstant.NICKNAME, nickname);
                    intent.putExtra(ConfigurationConstant.NAME, displayName);
                    intent.putExtra("image", imageThumbnail);

                    startActivity(intent);
                    getActivity().finish();
                    break;
            }
        }
    };

    private void getProfileInformation(String nickname){
        ServiceLayer.GetUserByNickname(nickname, getContext(), new ServiceLayerCallback() {
            @Override
            public void onSuccess(String obj) throws JSONException {
                Gson gson = new Gson();
                UserResponse userDetails = gson.fromJson(obj, UserResponse.class);
                try {
                    if (userDetails != null) {
                        //get display name and trim it
                        displayName = userDetails.getDisplayName();
                        displayName = displayName.contains("@") ? displayName.substring(0, displayName.indexOf("@")) : displayName;
                        nameTextView.setText(displayName);
                        //get Birthdate and calculate the age
                        String birthDate = userDetails.getBirthDate();
                        String age = birthDate != null ? birthDate : "N/A";
                        ageTextView.setText(age + "");

                        imageThumbnail = userDetails.getImg_link_thumb();
                        if (!imageThumbnail.equals("") && imageThumbnail != null) {
                            Glide.with(getContext()).asBitmap().load(imageThumbnail).into(profileImageView);
                        } else {

                            String[] names = displayName.trim().split(" ");
                            String imagedb = "";

                            for (int count = 0; count < names.length; count++) {
                                imagedb = imagedb + names[count].charAt(0) + "";
                                if (count == 1) {
                                    break;
                                }
                            }
                            Bitmap image = UserImage.getBitmapFromString(imagedb, 32.0f, getContext());
                            profileImageView.setImageBitmap(image);
                        }
                    }
                }catch (Exception exc){
                    Log.e("TTT", exc.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(String obj) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ConfigurationConstant.nickname, nickname);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        try {
            nickname = savedInstanceState.getString(ConfigurationConstant.nickname);
            if(nickname != null)
                getProfileInformation(nickname);
        }catch (Exception exc){
            Log.e("TTT", exc.getLocalizedMessage());
        }
    }
}
