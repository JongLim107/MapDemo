package com.brightoil.mapdemo.map;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

public abstract class MapAdapter {
    @MapAdapterFactory.MapProvider
    int mapProvider;

    OnLoadCompletedListener mLoadCompletedListener;
    OnMapClickListener mMapClickListener;
    OnInfoWindowClickListener mInfoWindowClickListener;

    public abstract Fragment getMapFragment();

    public abstract boolean setCenter(CustomLocation location);

    public abstract void addMarker(MapMarker mapMarker);

    public abstract void clearMarkers();

    public abstract void dismissMarkerInfoWindow(MapMarker mapMarker);

    public abstract void showMarkerInfoWindow(MapMarker mapMarker, View windowView);

    public abstract void setMapZoom(int zoom);

    public void setLoadListener(OnLoadCompletedListener loadListener) {
        this.mLoadCompletedListener = loadListener;
    }

    /**
     * set  center point to location and the zoom to zoom
     *
     * @param location center loacation
     * @param zoom     map zoom
     */
    public abstract void moveTO(CustomLocation location, int zoom);

    public abstract void setMapBounds(List<CustomLocation> locations);

    public int getMapProvider() {
        return mapProvider;
    }

    public static void checkPermission(@NonNull AppCompatActivity activity, int pe) {
        //activity.checkSelfPermission()
    }


    public interface OnLoadCompletedListener {
        void onMapLoadCompletedListener();
    }

    public interface OnMapClickListener {
        void onMapClickListener(CustomLocation location);
    }

    public interface OnMarkerClickListener {
        void onMarkerClickListener(MapMarker marker);
    }

    public interface OnMapMarkerCLickListener {
        void onMapMarkerClickListener(MapMarker marker);
    }

    public interface OnInfoWindowClickListener {
        void onInfoWindowClickListener(MapMarker marker);
    }
}