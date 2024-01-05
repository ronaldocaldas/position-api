package com.mobi7.positionapi.utils;

public class PositionsUtils {

    // Earth radius in meters
    private static final double EARTH_RADIUS = 6371000;

    // Method to check if a point is within the radius
    public static boolean isWithinRadius(double centerLatitude, double centerLongitude, double pointLatitude, double pointLongitude, double radius) {
        // Convert coordinates to radians
        double lat1 = Math.toRadians(centerLatitude);
        double lon1 = Math.toRadians(centerLongitude);
        double lat2 = Math.toRadians(pointLatitude);
        double lon2 = Math.toRadians(pointLongitude);

        // Calculate the differences between coordinates
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Haversine formula to calculate the distance
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS * c;

        // Check if the distance is within the specified radius
        return distance <= radius;
    }
}
