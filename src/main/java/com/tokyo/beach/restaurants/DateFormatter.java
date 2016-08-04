package com.tokyo.beach.restaurants;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    public static String formatDateForSerialization(ZonedDateTime dateToFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return formatter.format(dateToFormat);
    }
}
