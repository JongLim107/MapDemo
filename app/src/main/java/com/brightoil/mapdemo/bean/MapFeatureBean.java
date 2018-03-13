package com.brightoil.mapdemo.bean;

import java.util.ArrayList;

/**
 * A model object representing a Vessel info get from geoService server
 *
 * Created by JongLim on 2018-03-08.
 */

public class MapFeatureBean {

    private class Crs {

        private class Properties{
            private String name;
            private String mmsi;
        }

        private String type;
        private Properties properties;
    }

    private String type;
    private String totalFeatures;
    private ArrayList<Feature> features;
    private Crs crs;

    public String getType() {
        return type;
    }

    public String getTotalFeatures() {
        return totalFeatures;
    }

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public String getCrsType() {
        return crs.type;
    }

    public String getCrsName() {
        return crs.properties.name;
    }
}
