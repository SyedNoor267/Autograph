package adapter_classes;

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import ibeacon.detection.Detecting;

import homescroll.Profile;
import homescroll.Chats;
import userinterface.helperconfiguration.PreferenceConnector;

public class ContentPagerAdapter extends FragmentStatePagerAdapter {

//    public static final String TTT = "TTT";
    private Fragment fragment;
    Context context;
    public ContentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Fragment getItem(int i) {
//        Log.d(TTT, i+"");
        switch (i){
            case 0:
                fragment=Profile.newInstance();
                break;
            case 1:
                fragment= Detecting.newInstance();
                break;
            case 2:
                fragment= Chats.newInstance();
                break;
            default:
                fragment = Profile.newInstance();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
