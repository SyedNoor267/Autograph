package ibeacon.detection;

import android.os.Build;
import android.support.annotation.RequiresApi;

public class ChatContact {

    public final String name;
    public final String nickname;
    public final String image;
    public final String date;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChatContact(String nickname,String name,String image, String date)
    {
        this.name = name;
        this.nickname = nickname;
        this.image = image;
        this.date = date;

    }

    public String getContactName()
    {
        return name;
    }

}
