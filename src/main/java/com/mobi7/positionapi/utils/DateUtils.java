package com.mobi7.positionapi.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatInstant(Instant instant) {
        return LocalDate.ofInstant(instant, ZoneId.systemDefault()).format(DATE_FORMATTER);
    }

}
