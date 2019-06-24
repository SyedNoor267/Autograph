package helpers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.appiapi.R;
import com.facebook.login.LoginManager;

import userinterface.MainActivity;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.PreferenceConnector;

public class SignoutFunction {


    public static void addFragment(Context context, Fragment fragment, FragmentManager fragmentManager){
        if (fragmentManager != null) {
            String nickname = PreferenceConnector.readString(context, ConfigurationConstant.nickname, null);
            if(nickname!= null) {
                PreferenceConnector.RemoveItem(context, ConfigurationConstant.nickname);
                PreferenceConnector.RemoveItem(context, ConfigurationConstant.password);

                PreferenceConnector.RemoveItem( context, ConfigurationConstant.name );
                PreferenceConnector.RemoveItem( context, ConfigurationConstant.birthDate);
                PreferenceConnector.RemoveItem( context, ConfigurationConstant.sex);

                if (nickname.startsWith("fb"))
                    LoginManager.getInstance().logOut();
            }
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }
}
