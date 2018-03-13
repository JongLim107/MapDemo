package com.brightoil.mapdemo.action;

/**
 * Created by JongLim on 2018-03-06.
 */

public interface Config {
    String server = "http://192.168.65.43:8080/geoserver/myWorkspace/wms?";
    String service = "WMS";
    String ver1 = "1.3.0";
    String CRS = "EPSG%3A3857";
    String ver2 = "1.1.1";
    String srs= "EPSG%3A4326";
    String req = "GetFeatureInfo";
    String format = "image%2Fpng";
    String query_layer = "myWorkspace%3Aais_shape";
    String layer = "myWorkspace%3Aais_shape";
    String info_fmt = "application%2Fjson";
}
