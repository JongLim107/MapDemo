package com.brightoil.mapdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.brightoil.mapdemo.bean.MapFeature;
import com.brightoil.mapdemo.bean.MapGeoJson;
import com.brightoil.mapdemo.network.MyCallback;
import com.brightoil.mapdemo.network.MyRequestManager;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GgMapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, CompoundButton.OnCheckedChangeListener {

    private GoogleMap mGoogleMap;
    private Marker mMarker;
    private WMSTileProvider mProvider;
    private MarkerInfoHolder mPopupHolder;
    private MarkerInfoWindowAdapter mInfoWindowAdapter;


    private Switch mSwitchDef;

    private final String LAY_KEY = "layer";
    private ArrayList<HashMap<String, Object>> layersList = new ArrayList<>();

    private int tileSize = 256;

    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
        @Override
        public void onWakeUp(AppData appData) {
            //获取渠道数据
            String channelCode = appData.getChannel();
            //获取绑定数据
            String bindData = appData.getData();
            Log.d("OpenInstall", "getWakeUp : wakeupData = " + appData.toString());
        }
    };

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

        mSwitchDef = findViewById(R.id.swShip);
        mSwitchDef.setOnCheckedChangeListener(this);
        Switch switchTug = findViewById(R.id.swTug);
        switchTug.setOnCheckedChangeListener(this);
        Switch switchCargo = findViewById(R.id.swCargo);
        switchCargo.setOnCheckedChangeListener(this);
        Switch switchTanker = findViewById(R.id.swTanker);
        switchTanker.setOnCheckedChangeListener(this);


        //获取唤醒参数
        OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 此处要调用，否则App在后台运行时，会无法截获
        OpenInstall.getWakeUp(intent, wakeUpAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeUpAdapter = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.swTug) {
            addTileOverlay(WMSTileFactory.tug, b);
        } else if (compoundButton.getId() == R.id.swCargo) {
            addTileOverlay(WMSTileFactory.cargo, b);
        } else if (compoundButton.getId() == R.id.swTanker) {
            addTileOverlay(WMSTileFactory.tanker, b);
        } else {
            addTileOverlay(WMSTileFactory.vessels, b);
        }
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
        mGoogleMap = googleMap;
        mInfoWindowAdapter = new MarkerInfoWindowAdapter();
        mGoogleMap.setInfoWindowAdapter(mInfoWindowAdapter);

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(0, 0);
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Center"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);
        mSwitchDef.setChecked(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMarker != null) {
            mMarker.remove();
            mMarker = null;
            return;
        }

        int zoomLevel = (int) mGoogleMap.getCameraPosition().zoom;

        /* Get X,Y in meter unit */
        int[] ret = mProvider.getBoundingBoxIJ(latLng, zoomLevel);
        //Log.v("JongLim", String.format("ZoomLevel = %d", zoomLevel));
        //Log.v("JongLim", String.format("XY inner box = (%d, %d)", ret[0], ret[1]));
        //Log.v("JongLim", String.format("index of box = (%d, %d)", ret[2], ret[3]));
        //Log.v("JongLim", String.format("Click Point XY = (%d, %d)", ret[4], ret[5]));//the rang is -20037508.34 to 20037508.34

        double[] bbox = mProvider.getBoundingBox(ret[2], ret[3], zoomLevel);
        //Log.v("JongLim", String.format("BBox Left/Bottom, Right/Top = (%.4f, %.4f, %.4f, %.4f)", bbox[0], bbox[1], bbox[2], bbox[3]));

        // resume ret array to store width & height
        ret[2] = ret[3] = tileSize;

        //get top layer
        if (layersList.isEmpty()) {
            return;
        }

        HashMap map = layersList.get(0);
        MyRequestManager.getFeatureInfo((String) map.get(LAY_KEY), ret, bbox, new MyCallback<MapGeoJson>(this, MapGeoJson.class, "GetFeature Info...") {
            @Override
            public void onSucceed(MapGeoJson body, int id) {
                if (body != null && body.getFeatures().size() > 0) {
                    MapFeature feature = body.getFeatures().get(0);
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

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (mMarker != null) {
            mMarker.remove();
            mMarker = null;
        }
    }

    private void addSymbolLayer(@WMSTileFactory.GeoLayers final String layerName, boolean show) {
        String LAY_TILE = "tile_object";
        if (show) {

            String geoJsonUrl = WMSTileFactory.getGeoJsonUrl(layerName, tileSize);
            MyRequestManager.getFeatureList(geoJsonUrl, new MyCallback<MapGeoJson>(this, MapGeoJson.class, "Loading...") {
                @Override
                public void onSucceed(MapGeoJson list, int id) {

                    if (list != null && list.getFeatures() != null && list.getFeatures().size() > 0) {

                    }
                }

                @Override
                public void onFailed(String exception, int code, int id) {
                    Log.e("JongLim", String.format("Call Failed, exception = %s, code = %d, id = %d", exception, code, id));
                }
            });

        } else if (!layersList.isEmpty()) {

            for (HashMap map : layersList) {
                if (layerName.equals(map.get(LAY_KEY))) {
                    TileOverlay t = (TileOverlay) map.get(LAY_TILE);
                    if (t != null) {
                        t.setVisible(false);
                        t.remove();
                        layersList.remove(map);
                        break;
                    }
                }
            }
        }
    }

    private void addTileOverlay(@WMSTileFactory.GeoLayers String layerName, boolean show) {
        String LAY_TILE = "tile_object";
        if (show) {
            mProvider = WMSTileFactory.getTileProvider(layerName, tileSize, "image/png");
            TileOverlay t = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

            HashMap<String, Object> map = new HashMap<>();
            map.put(LAY_KEY, layerName);
            map.put(LAY_TILE, t);
            layersList.add(0, map);

        } else if (!layersList.isEmpty()) {

            for (HashMap map : layersList) {
                if (layerName.equals(map.get(LAY_KEY))) {
                    TileOverlay t = (TileOverlay) map.get(LAY_TILE);
                    if (t != null) {
                        t.setVisible(false);
                        t.remove();
                        layersList.remove(map);
                        break;
                    }
                }
            }
        }
    }

    private void addMarker(MapFeature feature) {
        float[] point = feature.getGeometryCoordinate();
        //Log.d("JongLim", String.format("Features X,Y = (%f, %f)", point[0], point[1]));

        float lng = (float) WMSTileFactory.x2lon(point[0]);
        float lat = (float) WMSTileFactory.y2lat(point[1]);
        LatLng latLng = new LatLng(lat, lng);
        feature.setGeometryCoordinate(lng, lat);

        if (Math.abs(latLng.latitude) > 82) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 22));
        } else if (Math.abs(latLng.latitude) > 80) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 20));
        } else {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        mMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.vessel_marker)));
        if (mPopupHolder == null) {
            mPopupHolder = new MarkerInfoHolder(this, null);
        }
        mPopupHolder.setBean(feature);

        mInfoWindowAdapter.setWindowView(mPopupHolder.getRoot());
        if (!mMarker.isInfoWindowShown()) {
            mMarker.showInfoWindow();
        }
    }

}
