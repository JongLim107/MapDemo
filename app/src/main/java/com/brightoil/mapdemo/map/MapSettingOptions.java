package com.brightoil.mapdemo.map;

public class MapSettingOptions {

    private int mapZoom = 4;//设置地图缩放率（1～21s）
    private CustomLocation mLocation = new CustomLocation(1.274301, 103.799760);

    public MapSettingOptions() {
    }

    public int getMapZoom() {
        return mapZoom;
    }

    public void setMapZoom(int mapZoom) {
        this.mapZoom = mapZoom;
    }

    public CustomLocation getLocation() {
        return mLocation;
    }

    public void setLocation(CustomLocation location) {
        mLocation = location;
    }

    public static MapSettingOptions newDefaultInstance() {
        return new MapSettingOptions();
    }

}

