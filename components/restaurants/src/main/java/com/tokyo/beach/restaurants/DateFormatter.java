package com.tokyo.beach.restaurants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    public static String formatDateForSerialization(String dateToFormat) {
        String result = "";

        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            Date date = format.parse(dateToFormat);
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            result = format.format(date);
        } catch (ParseException e) {
        }

        return result;
    }
}
