package com.mobi7.positionapi.utils;

import com.mobi7.positionapi.model.Position;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatInstant(Instant instant) {
        return LocalDate.ofInstant(instant, ZoneId.systemDefault()).format(DATE_FORMATTER);
    }

    public static String calculateAndFormatTotalDuration(List<Position> positionList) {
        Duration totalDuration = Duration.ZERO;

        for (int i = 0; i < positionList.size() - 1; i++) {
            Instant currentInstant = positionList.get(i).getDatePosition();
            Instant nextInstant = positionList.get(i + 1).getDatePosition();
            Duration durationBetween = Duration.between(currentInstant, nextInstant);
            totalDuration = totalDuration.plus(durationBetween);
        }

        long days = totalDuration.toDaysPart();
        long hours = totalDuration.toHoursPart();
        long minutes = totalDuration.toMinutesPart();
        long seconds = totalDuration.toSecondsPart();

        return String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds);
    }
}
