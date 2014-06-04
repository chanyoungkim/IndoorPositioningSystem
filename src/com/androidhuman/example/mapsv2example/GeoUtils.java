package com.androidhuman.example.mapsv2example;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Library for some use useful latitude/longitude math
 */
public class GeoUtils {
    private static int EARTH_RADIUS_KM = 6371;

    public static int MILLION = 1000000;

    /**
     * Computes the distance in kilometers between two points on Earth.
     * 
     * @param lat1 Latitude of the first point
     * @param lon1 Longitude of the first point
     * @param lat2 Latitude of the second point
     * @param lon2 Longitude of the second point
     * @return Distance between the two points in kilometers.
     */
    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLonRad = Math.toRadians(lon2 - lon1);
        
        return Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.cos(deltaLonRad))
                * EARTH_RADIUS_KM;
    }
    
    /**
     * Computes the distance in kilometers between two points on Earth.
     * 
     * @param p1 First point
     * @param p2 Second point
     * @return Distance between the two points in kilometers.
     */
    public static double distanceKm(Location p1, Location p2) {
        double lat1 = p1.getLatitude() / (double)MILLION;
        double lon1 = p1.getLongitude() / (double)MILLION;
        double lat2 = p2.getLatitude() / (double)MILLION;
        double lon2 = p2.getLongitude() / (double)MILLION;

        return distanceKm(lat1, lon1, lat2, lon2);
    }
    
    /**
     * Computes the bearing in degrees between two points on Earth.
     * 
     * @param p1 First point
     * @param p2 Second point
     * @return Bearing between the two points in degrees. A value of 0 means due
     *         north.
     */
    public static double bearing(Location p1, Location p2) {
        double lat1 = p1.getLatitude() / (double) MILLION;
        double lon1 = p1.getLongitude() / (double) MILLION;
        double lat2 = p2.getLatitude() / (double) MILLION;
        double lon2 = p2.getLongitude() / (double) MILLION;

        return bearing(lat1, lon1, lat2, lon2);
    }
    
    /**
     * Computes the bearing in degrees between two points on Earth.
     * 
     * @param lat1 Latitude of the first point
     * @param lon1 Longitude of the first point
     * @param lat2 Latitude of the second point
     * @param lon2 Longitude of the second point
     * @return Bearing between the two points in degrees. A value of 0 means due
     *         north.
     */
    public static double bearing(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLonRad = Math.toRadians(lon2 - lon1);

        double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad)
                * Math.cos(deltaLonRad);
        return radToBearing(Math.atan2(y, x));
    }
    
    /**
     * Converts an angle in radians to degrees
     */
    public static double radToBearing(double rad) {
        return (Math.toDegrees(rad) + 360) % 360;
    }
}