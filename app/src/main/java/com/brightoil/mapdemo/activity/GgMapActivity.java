package com.brightoil.mapdemo.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.brightoil.mapdemo.bean.Feature;
import com.brightoil.mapdemo.bean.MapFeatureBean;
import com.brightoil.mapdemo.network.MyCallback;
import com.brightoil.mapdemo.network.MyRequestManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;

public class GgMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Marker mMapMarker;
    private WMSTileProvider mProvider;
    private MarkerInfoHolder mPopupHolder;
    private MarkerInfoWindowAdapter mInfoWindowAdapter;

    private int tileXSize = 256;
    private int tileYSize = 256;

    class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View windowView;

        @Override
        public View getInfoWindow(Marker marker) {
            return windowView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        void setWindowView(View windowView) {
            this.windowView = windowView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLayout);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mInfoWindowAdapter = new MarkerInfoWindowAdapter();
        mMap.setInfoWindowAdapter(mInfoWindowAdapter);

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(0, 0);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Center"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        mProvider = WMSTileFactory.getWMSTileProvider(WMSTileFactory.TPType.normal, tileXSize, tileYSize);
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(WMSTileFactory.getWMSTileProvider(WMSTileFactory.TPType.tug, tileXSize, tileYSize)));
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(WMSTileFactory.getWMSTileProvider(WMSTileFactory.TPType.cargo, tileXSize, tileYSize)));
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(WMSTileFactory.getWMSTileProvider(WMSTileFactory.TPType.tanker, tileXSize, tileYSize)));

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mMapMarker != null) {
            mMapMarker.remove();
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMapMarker != null) {
            mMapMarker.remove();
        }

        int zoomLevel = (int) mMap.getCameraPosition().zoom;

        /** Get X,Y in meter unit */
        int[] ret = mProvider.getBoundingBoxIJ(latLng, zoomLevel);
        Log.v("JongLim", String.format("ZoomLevel = %d", zoomLevel));
        Log.v("JongLim", String.format("XY inner box = (%d, %d)", ret[0], ret[1]));
        //Log.v("JongLim", String.format("index of box = (%d, %d)", ret[2], ret[3]));
        Log.v("JongLim", String.format("Click Point XY = (%d, %d)", ret[4], ret[5]));//the rang is -20037508.34 to 20037508.34

        double[] bbox = mProvider.getBoundingBox(ret[2], ret[3], zoomLevel);
        Log.v("JongLim", String.format("BBox Left/Bottom, Right/Top = (%.4f, %.4f, %.4f, %.4f)", bbox[0], bbox[1], bbox[2], bbox[3]));

        // resume ret array to store width & height
        ret[2] = tileXSize;
        ret[3] = tileYSize;
        MyRequestManager.getFeatureInfo(ret, bbox, new MyCallback<MapFeatureBean>(this, MapFeatureBean.class, "GetFeature Info...") {
            @Override
            public void onSucceed(MapFeatureBean body, int id) {
                if (body != null && body.getFeatures().size() > 0) {
                    Feature feature = body.getFeatures().get(0);
                    if (feature != null) {
                        addMarker(feature);
                    }
                }
            }

            @Override
            public void onFailed(String exception, int code, int id) {
                Log.e("JongLim", String.format("Call Failed, exception = %s, code = %d, id = %d", exception, code, id));
            }
        });
    }

    private void addMarker(Feature feature) {
        float[] point = feature.getGeometryCoordinate();
        Log.d("JongLim", String.format("Features X,Y = (%f, %f)", point[0], point[1]));

        float lng = (float) WMSTileFactory.x2lon(point[0]);
        float lat = (float) WMSTileFactory.y2lat(point[1]);
        LatLng latLng = new LatLng(lat, lng);
        feature.setGeometryCoordinate(lng, lat);

        if (Math.abs(latLng.latitude) > 82) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 22));
        } else if (Math.abs(latLng.latitude) > 80) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 20));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        mMapMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.vessel_marker)));
        if (mPopupHolder == null) {
            mPopupHolder = new MarkerInfoHolder(this);
        }
        mPopupHolder.setBean(feature);

        mInfoWindowAdapter.setWindowView(mPopupHolder.getRoot());
        if (!mMapMarker.isInfoWindowShown()) {
            mMapMarker.showInfoWindow();
        }
    }

}
