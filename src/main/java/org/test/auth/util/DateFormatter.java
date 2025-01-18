package org.test.auth.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateFormatter {
    public static String getDateTimeStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static  String getMediumDateTimeStamp() {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(LocalDateTime.now());
    }
}
