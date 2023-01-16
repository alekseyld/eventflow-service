package com.alekseyld.eventflowbackend.events;

import java.text.SimpleDateFormat;

public class DateUtils {

    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public static final SimpleDateFormat defaultDatetimeFormatter =
            new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
    public static final SimpleDateFormat defaultDateFormatter =
            new SimpleDateFormat(DEFAULT_DATE_FORMAT);
}
