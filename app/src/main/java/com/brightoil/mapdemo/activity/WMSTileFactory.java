package com.brightoil.mapdemo.activity;

import android.support.annotation.StringDef;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by JongLim on 2018-02-28.
 */

public class WMSTileFactory {

    static final String vessels = "all_ships";
    static final String tanker = "live_tanker";
    static final String cargo = "live_cargo";
    static final String tug = "live_tug";

    @StringDef({vessels, tanker, cargo, tug})
    public @interface GeoLayers{}

    //geo service for mapbox
    private static final String mapboxUrl = "http://192.168.48.107:8080/geoserver/gis/wms?service=WMS" +
            "&version=1.1.0&request=GetMap" +
            "&layers=gis:%s" +
            "&width=%d&height=%d" +
            "&bbox={bbox-epsg-3857}" +
            "&srs=EPSG:3857" +
            "&format=image/png" +
            "&transparent=true";

    // This is configured for: OUR OWN service.
    // if it doesn't, find another one that supports EPSG:900913(/3857/)
    private static final String GM_WMS_FORMAT = "http://192.168.48.107:8080/geoserver/gis/wms?service=WMS" +
            "&version=1.1.1&request=GetMap" +
            "&layers=gis:%s" +
            "&bbox=%f,%f,%f,%f" +
            "&width=%d&height=%d" +
            "&srs=EPSG:3857" +
            "&format=image/png" +
            "&transparent=true";


    public static String getMapboxTile(@GeoLayers String layer, int xy_size){
        return String.format(mapboxUrl, layer, xy_size, xy_size);
    }

    public static WMSTileProvider getTileProvider(int xy) {
        return new WMSTileProvider(xy, xy) {
            @Override
            public URL getTileUrl(int i, int i1, int i2) {
                return null;
            }
        };
    }

    public static WMSTileProvider getTileProvider(@GeoLayers String layer, int xy) {
        final String layerName = layer;
        return new WMSTileProvider(xy, xy) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                final double[] bbox = getBoundingBox(x, y, zoom);
                Log.d("JongLim", String.format("(x, y, zoom) = (%d, %d, %d)", x, y, zoom));
                /* Define the url pattern for the tile images */
                String s = String.format(Locale.US, GM_WMS_FORMAT, layerName, bbox[MINX], bbox[MINY], bbox[MAXX], bbox[MAXY], width, height);
                Log.d("JongLim", s);
                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }
        };
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
