package com.brightoil.mapdemo.action;

/**
 * Created by JongLim on 2018-03-06.
 */

public interface Config {
    String server = "http://192.168.48.107:8080/geoserver/gis/wms?";
    String service = "WMS";
    String ver1 = "1.3.0";
    String CRS = "EPSG%3A3857";
    String ver2 = "1.1.1";
    String srs= "EPSG%3A4326";
    String req = "GetFeatureInfo";
    String format = "image%2Fpng";
    String query_layer = "gis%3Aall_ships";
    String layer = "gis%3Aall_ships";
    String info_fmt = "application%2Fjson";
}
