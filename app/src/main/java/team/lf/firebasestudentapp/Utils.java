package team.lf.firebasestudentapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String getTime(Date date) {
        String dateTime;
        Date currentDate = new Date();
        SimpleDateFormat formatter;
        if (currentDate.getDay() == date.getDay()) formatter = new SimpleDateFormat("HH:mm:ss", Locale.forLanguageTag("RU")); //так короче :)
        else formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(date);
    }
}
