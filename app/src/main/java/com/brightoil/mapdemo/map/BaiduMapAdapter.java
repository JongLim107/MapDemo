package com.brightoil.mapdemo.map;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BaiduMapAdapter extends MapAdapter implements BaiduMapFragment.BaiMapReadyCallback, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener {
    private BaiduMap map;
    private BaiduMapFragment mapFragment;
    private InfoWindow mInfoWindow;
    private Set<MapMarker> markerSet;

    private BaiduMapAdapter(@Nullable MapSettingOptions options, @Nullable OnLoadCompletedListener loadListener) {
        this.mLoadCompletedListener = loadListener;
        this.mapProvider = MapAdapterFactory.BAIDU_MAP;
        markerSet = new HashSet<MapMarker>();
        BaiduMapOptions mapOptions = new BaiduMapOptions();
        MapStatus.Builder builder = new MapStatus.Builder();
        double lat = 1.274301;
        double lng = 103.799760;
        if (options != null) {
            CustomLocation customLocation = options.getLocation();
            if (customLocation != null) {
                lat = customLocation.latitude;
                lng = customLocation.longitude;
            }
            builder.overlook(0).zoom(3 + (options.getMapZoom() * 4) / 5);
        } else {
            builder.overlook(0).zoom(11);
        }
        LatLng p = new LatLng(lat, lng);
        builder.target(p);
        BaiduMapOptions bo = new BaiduMapOptions().mapStatus(builder.build()).compassEnabled(true).zoomControlsEnabled(true);
        mapFragment = BaiduMapFragment.newInstance(bo, this);

    }

    public static BaiduMapAdapter newInstance(@Nullable MapSettingOptions options, @Nullable OnLoadCompletedListener loadListener) {
        return new BaiduMapAdapter(options, loadListener);
    }

    @Override
    public Fragment getMapFragment() {
        return mapFragment;
    }

    @Override
    public boolean setCenter(CustomLocation location) {
        if (map != null && location != null) {
            LatLng center = new LatLng(location.latitude, location.longitude);
            MapStatus mapStatus = new MapStatus.Builder().target(center).build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            map.animateMapStatus(mapStatusUpdate);
        }
        return false;
    }

    @Override
    public void addMarker(MapMarker mapMarker) {
        Marker marker;
        if (mapMarker == null || map == null) {
            return;
        }
        MarkerOptions options = new MarkerOptions().position(new LatLng(mapMarker.getLocation().latitude, mapMarker.getLocation().longitude));
        options.icon(BitmapDescriptorFactory.fromResource(mapMarker.getDrawableResId()));
        marker = (Marker) map.addOverlay(options);
        mapMarker.setObject(marker);
        markerSet.add(mapMarker);
    }

    @Override
    public void clearMarkers() {
        markerSet.clear();
        if (map != null) {
            map.clear();
        }

    }

    @Override
    public void dismissMarkerInfoWindow(MapMarker mapMarker) {
        if (map != null) {
            map.hideInfoWindow();
        }
    }

    @Override
    public void showMarkerInfoWindow(final MapMarker mapMarker, View windowView) {
        if (mapMarker != null && mapMarker.getObject() != null && windowView != null) {
            if (mInfoWindowClickListener != null) {
                windowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mInfoWindowClickListener.onInfoWindowClickListener(mapMarker);
                    }
                });
                mInfoWindow = new InfoWindow(windowView, new LatLng(mapMarker.getLocation().latitude, mapMarker.getLocation().longitude), -40);
            } else {
                mInfoWindow = new InfoWindow(windowView, new LatLng(mapMarker.getLocation().latitude, mapMarker.getLocation().longitude), -40);

            }
            map.showInfoWindow(mInfoWindow);
        }
    }

    @Override
    public void setMapZoom(int zoom) {
        if (map != null) {
            MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(3 + (zoom * 4) / 5);
            map.setMapStatus(msu);
        }
    }

    @Override
    public void moveTO(CustomLocation location, int zoom) {
        if (map != null && location != null) {
            LatLng center = new LatLng(location.latitude, location.longitude);
            MapStatus mapStatus = new MapStatus.Builder().target(center).overlook(0).zoom(3 + (zoom * 4) / 5).build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            map.animateMapStatus(mapStatusUpdate);
        }
    }

    @Override
    public void setMapBounds(List<CustomLocation> locations) {
        if (map != null && locations != null && !locations.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (CustomLocation customLocation : locations) {
                if (customLocation != null) {
                    builder.include(new LatLng(customLocation.latitude, customLocation.longitude));
                }
            }
            map.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
        }
    }

    @Override
    public void onMapReady(BaiduMap map) {
        this.map = map;
        this.map.setOnMarkerClickListener(this);
        this.map.setOnMapClickListener(this);
        if (mLoadCompletedListener != null) {
            mLoadCompletedListener.onMapLoadCompletedListener();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mInfoWindow = null;
        map.hideInfoWindow();
        if (markerSet != null && !markerSet.isEmpty()) {
            for (MapMarker mapMarker : markerSet) {
                Object omarker = mapMarker.getObject();
                if (omarker != null && omarker instanceof Marker) {
                    Marker temp = (Marker) omarker;
                    if (marker.equals(temp)) {
                        if (mapMarker.getClickListener() != null) {
                            mapMarker.getClickListener().onMarkerClickListener(mapMarker);
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMapClickListener != null) {
            mMapClickListener.onMapClickListener(new CustomLocation(latLng.latitude, latLng.longitude));
        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        //        LatLng latLng = mapPoi.getPosition();
        //        if (mMapListener != null) {
        //            mMapListener.onMapMarkerClicked(new MapMarker(-100,new CustomLocation(mapPoi.getPosition().latitude,mapPoi.getPosition().longitude),0,
        // null));
        //        }
        return false;
    }
}
