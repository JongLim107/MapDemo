package com.brightoil.mapdemo.bean;

/**
 * Created by JongLim on 2018-03-08.
 */

public class Feature {

    private class Geometry {
        private String type;
        private float[] coordinates;
    }

    private class Properties{
        private String name;
        private String mmsi;
    }

    private String type;
    private String id;
    private Geometry geometry;
    private String geometry_name;
    private Properties properties;

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public float[] getGeometryCoordinate() {
        return geometry.coordinates;
    }

    public void setGeometryCoordinate(float lat, float lng) {
        this.geometry.coordinates[0] = lng;
        this.geometry.coordinates[1] = lat;
    }

    public String getGeometryName() {
        return geometry_name;
    }

    public String getFeatureName() {
        return properties.name;
    }
    public String getFeatureMmsi() {
        return properties.mmsi;
    }
}
