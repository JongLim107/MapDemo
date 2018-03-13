package com.brightoil.mapdemo.map;

import java.io.Serializable;

public class CustomLocation implements Serializable {
    public double latitude;
    public double longitude;
    public float speed;
    public float direction;
    public float accuracy;
    public int satellitesNum;

    public CustomLocation(double latitude, double longitude, float var5, float var6, float var7, int var8) {
        this.latitude = latitude > 88.7 ? 88.7 : (latitude < -88.7 ? -88.7 : latitude);
        this.longitude = longitude > 179.99 ? 179.99 : (longitude < -179.99 ? -179.99 : longitude);
        this.speed = var5;
        this.direction = var6;
        this.accuracy = var7;
        this.satellitesNum = var8;
    }

    public CustomLocation() {
        super();
    }

    public CustomLocation(double latitude, double longitude) {
        this.latitude = latitude > 88.7 ? 88.7 : (latitude < -88.7 ? -88.7 : latitude);
        this.longitude = longitude > 179.99 ? 179.99 : (longitude < -179.99 ? -179.99 : longitude);
    }
}
