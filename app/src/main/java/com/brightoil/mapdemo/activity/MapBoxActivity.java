package com.brightoil.mapdemo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.brightoil.mapdemo.bean.Feature;
import com.brightoil.mapdemo.bean.MapFeatureBean;
import com.brightoil.mapdemo.network.MyCallback;
import com.brightoil.mapdemo.network.MyRequestManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.RasterLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.RasterSource;
import com.mapbox.mapboxsdk.style.sources.TileSet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;

/**
 * Created by JongLim on 2018-03-09.
 */


public class MapBoxActivity extends FragmentActivity
        implements OnMapReadyCallback, MapboxMap.OnMapClickListener, MapboxMap.OnMarkerClickListener, CompoundButton.OnCheckedChangeListener {
    private MapView mMapView;
    private MapboxMap mMapbox;
    private Marker mMarker;

    private int tileSize = 256;
    private WMSTileProvider mProvider;

    private Switch mSwitchDef;
    private ArrayList<String> layersList = new ArrayList<>();

    private MarkerInfoHolder mPopupHolder;

    class MarkerInfoAdapter implements MapboxMap.InfoWindowAdapter {
        private View windowView;

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            return windowView;
        }

        void setWindowView(View windowView) {
            this.windowView = windowView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapbox);

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);


        mSwitchDef = findViewById(R.id.swShip);
        mSwitchDef.setOnCheckedChangeListener(this);
        Switch switchTug = findViewById(R.id.swTug);
        switchTug.setOnCheckedChangeListener(this);
        Switch switchCargo = findViewById(R.id.swCargo);
        switchCargo.setOnCheckedChangeListener(this);
        Switch switchTanker = findViewById(R.id.swTanker);
        switchTanker.setOnCheckedChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch (compoundButton.getId()) {

            case R.id.swTug: {
                /* GeoJsonSource */
                if (b) {
                    try {
                        URL geoJsonUrl = new URL("https://d2ad6b4ur7yvpq.cloudfront.net/naturalearth-3.3.0/ne_50m_urban_areas.geojson");
                        GeoJsonSource urbanAreasSource = new GeoJsonSource("urban-areas", geoJsonUrl);
                        mMapbox.addSource(urbanAreasSource);

                        FillLayer urbanArea = new FillLayer("urban-areas-fill", "urban-areas");
                        urbanArea.setProperties(fillColor(Color.parseColor("#ff0088")), fillOpacity(0.4f));

                        //mMapbox.addLayer(urbanArea);
                        mMapbox.addLayerAbove(urbanArea, "water");
                        layersList.add(0, WMSTileFactory.tug);
                    } catch (MalformedURLException malformedUrlException) {
                        malformedUrlException.printStackTrace();
                    }

                } else {
                    mMapbox.removeLayer("urban-areas-fill");
                    mMapbox.removeSource("urban-areas");
                    layersList.remove(WMSTileFactory.tug);
                }
                //addTileOverlay(WMSTileFactory.tug, b);
                break;
            }

            case R.id.swCargo: {
                /* RasterSource WmsSource */
                addTileOverlay(WMSTileFactory.cargo, b);
                break;
            }

            case R.id.swTanker: {
                addTileOverlay(WMSTileFactory.tanker, b);
                break;
            }

            default: {
                addTileOverlay(WMSTileFactory.vessels, b);
            }
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mMapbox = mapboxMap;
        mMapbox.addOnMapClickListener(this);
        mMapbox.setOnMarkerClickListener(this);

        mProvider = WMSTileFactory.getTileProvider(tileSize);
        mSwitchDef.setChecked(true);
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (mMarker != null) {
            mMapbox.removeMarker(mMarker);
            mMarker.remove();
            mMarker = null;
            return;
        }

        int zoomLevel = (int) mMapbox.getCameraPosition().zoom;

        /* Get X,Y in meter unit */
        int[] ret = mProvider.getBoundingBoxIJ(point, zoomLevel);
        //Log.v("JongLim", String.format("ZoomLevel = %d", zoomLevel));
        //Log.v("JongLim", String.format("XY inner box = (%d, %d)", ret[0], ret[1]));
        //Log.v("JongLim", String.format("index of box = (%d, %d)", ret[2], ret[3]));
        //Log.v("JongLim", String.format("Click Point XY = (%d, %d)", ret[4], ret[5]));//the rang is -20037508.34 to 20037508.34

        double[] bbox = mProvider.getBoundingBox(ret[2], ret[3], zoomLevel);
        //Log.v("JongLim", String.format("BBox Left/Bottom, Right/Top = (%.4f, %.4f, %.4f, %.4f)", bbox[0], bbox[1], bbox[2], bbox[3]));

        // get the top layer
        if (layersList.isEmpty()) {
            return;
        }

        // resume ret array to store width & height
        ret[2] = ret[3] = tileSize;
        MyRequestManager.getFeatureInfo(layersList.get(0), ret, bbox, new MyCallback<MapFeatureBean>(this, MapFeatureBean.class, "GetFeature Info...") {
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

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (!marker.isInfoWindowShown()) {
            mMapbox.selectMarker(mMarker);
        } else {
            mMapbox.deselectMarker(mMarker);
        }
        return true;
    }

    private void addTileOverlay(@WMSTileFactory.GeoLayers String layerName, boolean show) {
        if (show) {
            String tiles = WMSTileFactory.getMapboxTile(layerName, tileSize);
            TileSet ts = new TileSet("tileset", tiles);
            RasterSource webMapSource = new RasterSource("web-map-source-" + layerName, ts, tileSize);
            mMapbox.addSource(webMapSource);

            // Add the web map source to the map.
            RasterLayer webMapLayer = new RasterLayer("web-map-layer-" + layerName, "web-map-source-" + layerName);
            if (!layersList.isEmpty()) {
                String ly = "web-map-layer-" + layersList.get(0);
                Layer layer = mMapbox.getLayerAs(ly);
                if (layer != null) {
                    mMapbox.addLayerAbove(webMapLayer, ly);
                    layersList.add(0, layerName);
                    return;
                }
            }
            mMapbox.addLayerAbove(webMapLayer, "aeroway-taxiway");
            layersList.add(0, layerName);
        } else {
            mMapbox.removeLayer("web-map-layer-" + layerName);
            mMapbox.removeSource("web-map-source-" + layerName);
            layersList.remove(layerName);
        }
    }

    private void addMarker(Feature feature) {
        float[] point = feature.getGeometryCoordinate();
        //Log.d("JongLim", String.format("Features X,Y = (%f, %f)", point[0], point[1]));

        float lng = (float) WMSTileFactory.x2lon(point[0]);
        float lat = (float) WMSTileFactory.y2lat(point[1]);
        LatLng latLng = new LatLng(lat, lng);
        feature.setGeometryCoordinate(lng, lat);

        if (mPopupHolder == null) {
            mPopupHolder = new MarkerInfoHolder(this, null);
        }
        mPopupHolder.setBean(feature);

        MarkerInfoAdapter infoAdapter = new MarkerInfoAdapter();
        infoAdapter.setWindowView(mPopupHolder.getRoot());
        mMapbox.setInfoWindowAdapter(infoAdapter);
        Icon icon = IconFactory.getInstance(this).fromResource(R.drawable.vessel_marker);
        mMarker = mMapbox.addMarker(new MarkerOptions().position(latLng).icon(icon));
        mMapbox.selectMarker(mMarker);
    }
}
