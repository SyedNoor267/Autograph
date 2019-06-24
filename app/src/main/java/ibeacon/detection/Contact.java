package ibeacon.detection;

import android.os.Build;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Contact {
    public final String uuid;

    public final String name;
    public final String nickname;
    public final String image;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Contact(String uuidmajormin,String nicknames,String names,String images)
    {
        uuid=uuidmajormin;
        name = names;
        nickname = nicknames;
        image = images;

    }

    public String getContactName()
    {
        return name;
    }

}
