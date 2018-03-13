package com.brightoil.mapdemo.activity;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by JongLim on 2018-02-28.
 */

public class WMSTileFactory {

    //geo service for mapbox
    public static String mapboxUrl = "http://192.168.65.43:8080/geoserver/myWorkspace/wms?bbox={bbox-epsg-3857}&format=image/png&service=WMS&version=1.1.1" +
            "&request=GetMap&srs=EPSG:3857&width=%d&height=%d&layers=myWorkspace:ais_shape&transparent=true";

    // This is configured for: OUR OWN service.
    // if it doesn't, find another one that supports EPSG:900913
    private static final String WMS_FORMAT_STRING = "http://192.168.65.43:8080/geoserver/myWorkspace" + "/wms?service=WMS" + "&version=1.1.1" +
            "&request=GetMap" + "&layers=myWorkspace:ais_shape" + "&bbox=%f,%f,%f,%f" + "&width=%d" + "&height=%d" + "&srs=EPSG:3857" +
            "&format=image/png&transparent=true";

    private static final String WMS_FORMAT_TUG = "http://http://192.168.48.107:8080/geoserver" + "/gis/wms?service=WMS&version=1.1.1&request=GetMap" +
            "&layers=gis:filter_tug" + "&width=%d&height=%d&srs=EPSG:900913&format=image/png&transparent=true";
    private static final String WMS_FORMAT_CARGO = "http://http://192.168.48.107:8080/geoserver" + "/gis/wms?service=WMS&version=1.1.1&request=GetMap" +
            "&layers=gis:filter_cargo" + "&width=%d&height=%d&srs=EPSG:3857&format=image/png&transparent=true";
    private static final String WMS_FORMAT_CARGO1 = "http://192.168.48.107:8080/geoserver" + "/gwc/demo/gis:live_cargo?" +
            "gridSet=EPSG:4326&format=application/x-protobuf;type=mapbox-vector";
    private static final String WMS_FORMAT_TANKER = "http://http://192.168.48.107:8080/geoserver" + "/gis/wms?service=WMS&version=1.1.1&request=GetMap" +
            "&layers=gis:filter_tanker" + "&width=%d&height=%d&srs=EPSG:900913&format=image/png&transparent=true";

    private static String urlString = WMS_FORMAT_STRING;

    public enum TPType {
        normal,
        tug,
        cargo,
        tanker
    }

    private static WMSTileProvider getTileProvider(int x, int y) {
        return new WMSTileProvider(x, y) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                final double[] bbox = getBoundingBox(x, y, zoom);
                Log.d("JongLim", String.format("(x, y, zoom) = (%d, %d, %d)", x, y, zoom));
                /* Define the url pattern for the tile images */
                String s = String.format(Locale.US, WMS_FORMAT_STRING, bbox[MINX], bbox[MINY], bbox[MAXX], bbox[MAXY], width, height);
                Log.d("JongLim", s);
                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }
        };
    }

    private static WMSTileProvider getFilterTileProvider(int x, int y) {
        return new WMSTileProvider(x, y) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                String s = String.format(Locale.US, urlString, width, height);
                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }
        };
    }

    static WMSTileProvider getWMSTileProvider(TPType type, int x, int y) {
        switch (type) {
            case tug:
                urlString = WMS_FORMAT_TUG;
                return getFilterTileProvider(x, y);
            case cargo:
                urlString = WMS_FORMAT_CARGO;
                return getFilterTileProvider(x, y);
            case tanker:
                urlString = WMS_FORMAT_TANKER;
                return getFilterTileProvider(x, y);
            default:
                return getTileProvider(x, y);
        }
    }


    private static final double RADIUS = 6378137.0; /* in meters on the equator */

    /* These functions take their length parameter in meters and return an angle in degrees */
    static double y2lat(double aY) {
        return Math.toDegrees(Math.atan(Math.exp(aY / RADIUS)) * 2 - Math.PI / 2);
    }

    static double x2lon(double aX) {
        return Math.toDegrees(aX / RADIUS);
    }

    /* These functions take their angle parameter in degrees and return a length in meters */
    static double lat2y(double aLat) {
        return Math.log(Math.tan(Math.PI / 4 + Math.toRadians(aLat) / 2)) * RADIUS;
    }

    static double lon2x(double aLong) {
        return Math.toRadians(aLong) * RADIUS;
    }
}
