package com.brightoil.mapdemo.activity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.URLEncoder;

/**
 * Created by JongLim on 2018-02-28.
 */

public abstract class WMSTileProvider extends UrlTileProvider {
    // Web Mercator n/w corner of the map. 20037508.34789244
    private static final double[] TILES_ORIGIN = {-20037508.35, 20037508.35};

    // array indexes for that data
    private static final int ORIG_X = 0;
    private static final int ORIG_Y = 1;

    // Size of square world map in meters, using WebMerc projection.
    private static final double MAP_SIZE = 20037508.35 * 2;

    // array indexes for array to hold bounding boxes.
    protected static final int MINX = 0;
    protected static final int MINY = 1;
    protected static final int MAXX = 2;
    protected static final int MAXY = 3;

    //tile size in pix
    protected int width = 256; //normally 256
    protected int height = 256; //normally 256

    // Construct with tile size in pixels, normally 256, see parent class.
    WMSTileProvider(int x, int y) {
        super(x, y);
        this.width = x;
        this.height = y;
    }

    // cql filters
    private String cqlString = "";

    protected String getCql() {
        return URLEncoder.encode(cqlString);
    }

    public void setCql(String c) {
        cqlString = c;
    }

    // Return a web Mercator bounding box given tile x/y indexes and a zoom level.
    protected double[] getBoundingBox(int x, int y, int zoom) {
        double tileSize = MAP_SIZE / Math.pow(2, zoom);
        double minx = TILES_ORIGIN[ORIG_X] + x * tileSize;
        double maxx = TILES_ORIGIN[ORIG_X] + (x + 1) * tileSize;
        double miny = TILES_ORIGIN[ORIG_Y] - (y + 1) * tileSize;
        double maxy = TILES_ORIGIN[ORIG_Y] - y * tileSize;

        double[] bbox = new double[4];
        bbox[MINX] = minx;
        bbox[MINY] = miny;
        bbox[MAXX] = maxx;
        bbox[MAXY] = maxy;

        return bbox;
    }


    public int[] getBoundingBoxIJ(com.mapbox.mapboxsdk.geometry.LatLng latLng, int zoom){
        LatLng newLl = new LatLng(latLng.getLatitude(), latLng.getLongitude());
        return getBoundingBoxIJ(newLl, zoom);
    }

    public int[] getBoundingBoxIJ(LatLng latLng, int zoom){
        double x = WMSTileFactory.lon2x(latLng.longitude);
        double y = WMSTileFactory.lat2y(latLng.latitude);
        //Log.i("JongLim", String.format("Click XY = (%f, %f)", x, y));

        // Each tile's size weight in metres
        double tileSize = MAP_SIZE / Math.pow(2, zoom);

        // Current x and y belong to which tile.
        int i = (int) ((x - TILES_ORIGIN[ORIG_X]) / tileSize);
        int j = (int) ((TILES_ORIGIN[ORIG_Y] - y) / tileSize);
        //Log.i("JongLim", String.format("index of bbox = (%d, %d)", i, j));

        double[] bbox = getBoundingBox(i, j, zoom);
        // Current latitude and longitude mapping to y and x in current box.
        int boxX = (int) (Math.abs(x - bbox[0]) / (MAP_SIZE / Math.pow(2, zoom)) * height);
        int boxY = (int) (Math.abs((bbox[3] - y)) / (MAP_SIZE / Math.pow(2, zoom)) * width);
        //Log.i("JongLim", String.format("bbox border  = (%f, %f)", bbox[0], bbox[3]));
        //Log.i("JongLim", String.format("XY in the box = (%d, %d)", boxX, boxY));

        int[] output = new int[6];
        output[0] = boxX;
        output[1] = boxY;
        output[2] = i;
        output[3] = j;
        output[4] = (int) x;
        output[5] = (int) y;

        return output;
    }


}