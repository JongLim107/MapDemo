package com.brightoil.mapdemo.map;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoogleMapAdapter extends MapAdapter
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener {
    private GoogleMap map;
    private MarkerInfoPopup infoPopup;
    private SupportMapFragment googleMapFragment;

    private Set<MapMarker> markerSet;
    private MarkerInfoWindowAdapter infoWindowAdapter;

    private GoogleMapAdapter(@Nullable MapSettingOptions options, @Nullable OnLoadCompletedListener loadListener) {
        super();
        this.mLoadCompletedListener = loadListener;
        this.mapProvider = MapAdapterFactory.GOOGLE_MAP;
        markerSet = new HashSet<MapMarker>();
        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        double lat = 1.2743012824014568;
        double lng = 103.79976019263268;
        int zoom = 12;
        if (options != null) {
            CustomLocation moLocation = options.getLocation();
            if (moLocation != null) {
                lat = moLocation.latitude;
                lng = moLocation.longitude;
            }
            zoom = options.getMapZoom();
        }
        this.infoWindowAdapter = new MarkerInfoWindowAdapter();
        googleMapOptions.camera(new CameraPosition(new LatLng(lat, lng), zoom, 0.0F, 0.0F));
        googleMapFragment = SupportMapFragment.newInstance(googleMapOptions);
        googleMapFragment.getMapAsync(this);
    }

    public static GoogleMapAdapter newInstance(@Nullable MapSettingOptions options, @Nullable OnLoadCompletedListener loadListener) {
        return new GoogleMapAdapter(options, loadListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        this.map.setOnMarkerClickListener(this);
        this.map.setOnInfoWindowClickListener(this);
        this.map.setInfoWindowAdapter(infoWindowAdapter);
        this.map.setOnMapClickListener(this);
        if (mLoadCompletedListener != null) {
            mLoadCompletedListener.onMapLoadCompletedListener();
        }
    }

    @Override
    public Fragment getMapFragment() {
        return googleMapFragment;
    }

    @Override
    public boolean setCenter(CustomLocation location) {
        if (map != null) {
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.latitude, location.longitude)));
            return true;
        }
        return false;
    }

    @Override
    public void addMarker(MapMarker mapMarker) {
        if (mapMarker == null || map == null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(mapMarker.getLocation().latitude, mapMarker.getLocation().longitude))
                .icon(BitmapDescriptorFactory.fromResource(mapMarker.getDrawableResId()));

        Marker marker = map.addMarker(options);
        mapMarker.setObject(marker);
        markerSet.add(mapMarker);
    }

    @Override
    public void clearMarkers() {
        if (map != null) {
            map.clear();
        }
    }

    @Override
    public void dismissMarkerInfoWindow(MapMarker mapMarker) {
        if (mapMarker != null && mapMarker.getObject() != null) {
            Object oMarker = mapMarker.getObject();
            if (oMarker instanceof Marker) {
                Marker marker = (Marker) oMarker;
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                }
                markerSet.add(mapMarker);
            }
        }
    }

    @Override
    public void showMarkerInfoWindow(MapMarker mapMarker, View windowView) {
        if (mapMarker != null && mapMarker.getObject() != null && windowView != null) {
            infoWindowAdapter.setWindowView(windowView);
            Object oMarker = mapMarker.getObject();
            if (oMarker instanceof Marker) {
                Marker marker = (Marker) oMarker;
                if (!marker.isInfoWindowShown()) {
                    marker.showInfoWindow();
                }
            }
        }
    }

    private void showPopup(View loaction, View windowView) {
        if (infoPopup == null) {
            infoPopup = new MarkerInfoPopup(windowView.getContext());
        }
        infoPopup.setContentView(windowView);
        infoPopup.showAtLocation(loaction, Gravity.NO_GRAVITY, 0, 0);
    }

    @Override
    public void setMapZoom(int zoom) {
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
    }

    @Override
    public void moveTO(final CustomLocation location, int zoom) {
        if (map != null && location != null) {
            map.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.latitude, location.longitude)));
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
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        infoWindowAdapter.windowView = null;
        if (markerSet != null && !markerSet.isEmpty()) {
            for (MapMarker mapMarker : markerSet) {
                Object obj = mapMarker.getObject();
                if (obj != null && obj instanceof Marker) {
                    Marker temp = (Marker) obj;
                    if (marker.getId().equals(temp.getId())) {
                        if (mapMarker.getClickListener() != null) {
                            mapMarker.getClickListener().onMarkerClickListener(mapMarker);
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (mInfoWindowClickListener != null) {
            if (markerSet != null && !markerSet.isEmpty()) {
                for (MapMarker mapMarker : markerSet) {
                    Object obj = mapMarker.getObject();
                    if (obj != null && obj instanceof Marker) {
                        Marker temp = (Marker) obj;
                        if (marker.getId().equals(temp.getId())) {
                            mInfoWindowClickListener.onInfoWindowClickListener(mapMarker);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMapClickListener != null) {
            mMapClickListener.onMapClickListener(new CustomLocation(latLng.latitude, latLng.longitude));
        }
    }

    class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View windowView;
        private View contentsView;

        @Override
        public View getInfoWindow(Marker marker) {
            return windowView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return contentsView;
        }

        public void setWindowView(View windowView) {
            this.windowView = windowView;
        }


        public void setContentsView(View contentsView) {
            this.contentsView = contentsView;
        }
    }


    class MarkerInfoPopup extends PopupWindow {
        private View bootView;

        MarkerInfoPopup(Context context) {
            super(context);
            //            this.setWidth(FrameLayout.LayoutParams.WRAP_CONTENT);
            //            this.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
            this.setBackgroundDrawable(new ColorDrawable(0x00000000));
            this.setOutsideTouchable(true);
            this.setFocusable(true);
        }

        public void setBootView(View bootView) {
            this.bootView = bootView;
            this.setContentView(bootView);
        }
    }
}
