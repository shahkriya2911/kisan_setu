package com.project.kisan_setu.util;
import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {

    public static String getTimeAgo(LocalDateTime time) {
        Duration duration = Duration.between(time, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 60) {
            return minutes + " minutes ago";
        }
        else if (hours < 24) {
            return hours + " hours ago";
        }
        else {
            return days + " days ago";
        }
    }
}