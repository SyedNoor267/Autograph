package userinterface.helperconfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    public static String convertDate(String dateOnline){
        String lastOnline = dateOnline;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateOnline);
            lastOnline = new SimpleDateFormat("dd/MM/yyyy").format(date);
        }catch (Exception exc){
            exc.printStackTrace();
        }
        return lastOnline;
    }
}
