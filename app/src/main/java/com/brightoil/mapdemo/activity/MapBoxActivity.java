package com.brightoil.mapdemo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.brightoil.mapdemo.bean.MapGeoJson;
import com.brightoil.mapdemo.bean.MapFeature;
import com.brightoil.mapdemo.network.MyCallback;
import com.brightoil.mapdemo.network.MyGeoJsonCallback;
import com.brightoil.mapdemo.network.MyRequestManager;
import com.brightoil.mapdemo.tools.JLLog;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.RasterLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.RasterSource;
import com.mapbox.mapboxsdk.style.sources.TileSet;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.models.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private ArrayList<String> layerIds = new ArrayList<>();

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
                addSymbolLayer(WMSTileFactory.pax, b);
                break;
            }

            case R.id.swCargo: {
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
        mMapbox.setMaxZoomPreference(20);

        mProvider = WMSTileFactory.getTileProvider(tileSize);
        mSwitchDef.setChecked(true);
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        /** if the markerInfoView is showing, dismiss it. */
        if (mMarker != null) {
            mMapbox.removeMarker(mMarker);
            mMarker.remove();
            mMarker = null;
            return;
        }


        /** get the top layer. if it was empty , do nothing. */
        if (layerIds.isEmpty()) {
            return;
        }


        /** if click on the symbolLayer, just show the info windows. no need to call network api. */
        if (((Objects.equals(layerIds.get(0), WMSTileFactory.pax)))) {
            PointF screenPoint = mMapbox.getProjection().toScreenLocation(point);
            String[] layerIdsArray = layerIds.toArray(new String[layerIds.size()]);
            List<Feature> features = mMapbox.queryRenderedFeatures(screenPoint, layerIdsArray);
            if (!features.isEmpty()) {
                JLLog.showToast(MapBoxActivity.this, "hello from: " + features.get(0).getStringProperty("name"));
                addMarker(features.get(0));
                return;
            }
        }


        /** if click on normal tile layer, need to call network api for feature info. */
        int zoomLevel = (int) mMapbox.getCameraPosition().zoom;

        /* Get X,Y in meter unit */
        int[] ret = mProvider.getBoundingBoxIJ(point, zoomLevel);
        //Log.v("JongLim", String.format("ZoomLevel = %d", zoomLevel));
        //Log.v("JongLim", String.format("XY inner box = (%d, %d)", ret[0], ret[1]));
        //Log.v("JongLim", String.format("index of box = (%d, %d)", ret[2], ret[3]));
        //Log.v("JongLim", String.format("Click Point XY = (%d, %d)", ret[4], ret[5]));//the rang is -20037508.34 to 20037508.34

        double[] bbox = mProvider.getBoundingBox(ret[2], ret[3], zoomLevel);
        //Log.v("JongLim", String.format("BBox Left/Bottom, Right/Top = (%.4f, %.4f, %.4f, %.4f)", bbox[0], bbox[1], bbox[2], bbox[3]));

        // resume ret array to store width & height
        ret[2] = ret[3] = tileSize;
        MyRequestManager.getFeatureInfo(layerIds.get(0), ret, bbox, new MyCallback<MapGeoJson>(this, MapGeoJson.class, "GetFeature Info...") {
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
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (!marker.isInfoWindowShown()) {
            mMapbox.selectMarker(mMarker);
        } else {
            mMapbox.deselectMarker(mMarker);
        }
        return true;
    }


    /**
     * class for parse GeoServer return json result
     */
    private class FeatureList {
        private String type;
        private String totalFeatures;
        private ArrayList<Feature> features;

        public String getType() {
            return type;
        }

        public String getTotalFeatures() {
            return totalFeatures;
        }

        ArrayList<Feature> getFeatures() {
            return features;
        }
    }

    private void addSymbolLayer(@WMSTileFactory.GeoLayers final String layerName, boolean show) {

        if (show) {

            String geoJsonUrl = WMSTileFactory.getGeoJsonUrl(layerName, tileSize);
            MyRequestManager.getFeatureList(geoJsonUrl, new MyGeoJsonCallback<FeatureList>(this, FeatureList.class, "Loading...") {
                @Override
                public void onSucceed(FeatureList list, int id) {

                    if (list != null && list.getFeatures() != null && list.getFeatures().size() > 0) {

                        List<Feature> markerCoordinates = list.getFeatures();
                        FeatureCollection featureCollection = FeatureCollection.fromFeatures(markerCoordinates);
                        GeoJsonSource source = new GeoJsonSource("marker-source", featureCollection);
                        mMapbox.addSource(source);

                        Bitmap icon = BitmapFactory.decodeResource(MapBoxActivity.this.getResources(), R.drawable.vessel_marker);
                        // Add the marker image to map
                        mMapbox.addImage("marker-image", icon);

                        SymbolLayer markers = new SymbolLayer(layerName, "marker-source");
                        markers.withProperties(PropertyFactory.iconImage("marker-image"));
                        mMapbox.addLayer(markers);

                        layerIds.add(0, layerName); // push to the top of the list.

                    }
                }

                @Override
                public void onFailed(String exception, int code, int id) {
                    Log.e("JongLim", String.format("Call Failed, exception = %s, code = %d, id = %d", exception, code, id));
                }
            });

        } else {

            mMapbox.removeSource("marker-source");
            mMapbox.removeLayer(layerName);
            layerIds.remove(layerName);
        }
    }

    private void addTileOverlay(@WMSTileFactory.GeoLayers String layerName, boolean show) {
        if (show) {
            String tiles = WMSTileFactory.getMapboxTile(layerName, tileSize, "image/png");
            TileSet ts = new TileSet("tileset", tiles);
            RasterSource webMapSource = new RasterSource("source-" + layerName, ts, tileSize);
            mMapbox.addSource(webMapSource);

            // Add the web map source to the map.
            RasterLayer tileLayer = new RasterLayer(layerName, "source-" + layerName);
            if (!layerIds.isEmpty()) {
                String lyn = layerIds.get(0);
                Layer layer = mMapbox.getLayerAs(lyn);
                if (layer != null) {
                    mMapbox.addLayerAbove(tileLayer, lyn);
                    layerIds.add(0, layerName);
                    return;
                }
            }
            mMapbox.addLayerAbove(tileLayer, "aeroway-taxiway");
            layerIds.add(0, layerName);// push to the top of the list.
        } else {
            mMapbox.removeLayer(layerName);
            mMapbox.removeSource("source-" + layerName);
            layerIds.remove(layerName);
        }
    }

    private void addMarker(MapFeature mf) {
        float[] point = mf.getGeometryCoordinate();
        //Log.d("JongLim", String.format("Features X,Y = (%f, %f)", point[0], point[1]));

        float lng = (float) WMSTileFactory.x2lon(point[0]);
        float lat = (float) WMSTileFactory.y2lat(point[1]);
        LatLng latLng = new LatLng(lat, lng);
        mf.setGeometryCoordinate(lng, lat);

        if (mPopupHolder == null) {
            mPopupHolder = new MarkerInfoHolder(this, null);
        }
        mPopupHolder.setBean(mf);

        MarkerInfoAdapter infoAdapter = new MarkerInfoAdapter();
        infoAdapter.setWindowView(mPopupHolder.getRoot());
        mMapbox.setInfoWindowAdapter(infoAdapter);
        Icon icon = IconFactory.getInstance(this).fromResource(R.drawable.vessel_marker);
        mMarker = mMapbox.addMarker(new MarkerOptions().position(latLng).icon(icon));
        mMapbox.selectMarker(mMarker);
    }

    private void addMarker(Feature feature) {
        Position point = (Position) feature.getGeometry().getCoordinates();
        //Log.d("JongLim", String.format("Features X,Y = (%f, %f)", point[0], point[1]));

        float lng = (float) point.getLongitude();
        float lat = (float) point.getLatitude();
        LatLng latLng = new LatLng(lat, lng);

        MapFeature mf = MapFeature.fromJson(feature.toJson());
        mf.setGeometryCoordinate(lng, lat);
        if (mPopupHolder == null) {
            mPopupHolder = new MarkerInfoHolder(this, null);
        }
        mPopupHolder.setBean(mf);

        MarkerInfoAdapter infoAdapter = new MarkerInfoAdapter();
        infoAdapter.setWindowView(mPopupHolder.getRoot());
        mMapbox.setInfoWindowAdapter(infoAdapter);
        Icon icon = IconFactory.getInstance(this).fromResource(R.drawable.vessel_marker);
        mMarker = new MarkerOptions().position(latLng).icon(icon).getMarker();
        mMapbox.selectMarker(mMarker);
    }
}
