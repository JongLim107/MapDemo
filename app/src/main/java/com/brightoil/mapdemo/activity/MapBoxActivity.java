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
import com.mapbox.mapboxsdk.annotations.InfoWindow;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.RasterLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.RasterSource;
import com.mapbox.mapboxsdk.style.sources.TileSet;
import com.mapbox.mapboxsdk.style.sources.VectorSource;

import java.net.MalformedURLException;
import java.net.URL;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

/**
 * Created by JongLim on 2018-03-09.
 */


public class MapBoxActivity extends FragmentActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, MapboxMap.OnMarkerClickListener, CompoundButton
        .OnCheckedChangeListener {
    private MapView mapView;
    private MapboxMap mMap;
    private Marker mMapMarker;

    private int tileXSize = 256;
    private int tileYSize = 256;
    private WMSTileProvider mProvider;

    private Switch mSwitchTug;
    private Switch mSwitchCargo;
    private Switch mSwitchTanker;
    private MarkerInfoAdapter mInfoAdapter;
    private MarkerInfoHolder mPopupHolder;
    private InfoWindow mInfoWindow;

    class MarkerInfoAdapter implements MapboxMap.InfoWindowAdapter {
        private View windowView;

        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            return windowView;
        }

        public void setWindowView(View windowView) {
            this.windowView = windowView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapbox);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mSwitchTug = findViewById(R.id.swTug);
        mSwitchTug.setOnCheckedChangeListener(this);
        mSwitchCargo = findViewById(R.id.swCargo);
        mSwitchCargo.setOnCheckedChangeListener(this);
        mSwitchTanker = findViewById(R.id.swTanker);
        mSwitchTanker.setOnCheckedChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == mSwitchTug.getId()) {

            /* GeoJsonSource */
            if (mSwitchTug.isChecked()) {
                try {
                    URL geoJsonUrl = new URL("https://d2ad6b4ur7yvpq.cloudfront.net/naturalearth-3.3.0/ne_50m_urban_areas.geojson");
                    //URL geoJsonUrl = new URL("http://192.168.65.43:8080/geoserver/myWorkspace/wms?layers=myWorkspace:ais_shape&srs=EPSG:4326&format
                    // =application%2Fjson%3Btype%2Fgeojson");
                    GeoJsonSource urbanAreasSource = new GeoJsonSource("urban-areas", geoJsonUrl);
                    mMap.addSource(urbanAreasSource);

                    FillLayer urbanArea = new FillLayer("urban-areas-fill", "urban-areas");

                    urbanArea.setProperties(fillColor(Color.parseColor("#ff0088")), fillOpacity(0.4f));

                    mMap.addLayerBelow(urbanArea, "water");
                } catch (MalformedURLException malformedUrlException) {
                    malformedUrlException.printStackTrace();
                }
            } else {
                mMap.removeLayer("urban-areas-fill");
                mMap.removeSource("urban-areas");
            }

        } else if (compoundButton.getId() == mSwitchCargo.getId()) {

            /* RasterSource WmsSource */
            if (mSwitchCargo.isChecked()) {
                RasterSource webMapSource = new RasterSource("web-map-source",
                        new TileSet("tileset", String.format(WMSTileFactory.mapboxUrl, tileXSize, tileXSize)), tileXSize);
                mMap.addSource(webMapSource);

                // Add the web map source to the map.
                RasterLayer webMapLayer = new RasterLayer("web-map-layer", "web-map-source");
                mMap.addLayerBelow(webMapLayer, "aeroway-taxiway");
            } else {
                mMap.removeLayer("web-map-layer");
                mMap.removeSource("web-map-source");
            }
        } else if (compoundButton.getId() == mSwitchTanker.getId()) {

            /* VectorSource */
            if (mSwitchTanker.isChecked()) {
                VectorSource vectorSource = new VectorSource("terrain-data", "mapbox://mapbox.mapbox-terrain-v2");
                mMap.addSource(vectorSource);

                LineLayer terrainData = new LineLayer("terrain-data", "terrain-data");
                terrainData.setSourceLayer("contour");
                terrainData.setProperties(lineJoin(Property.LINE_JOIN_ROUND), lineCap(Property.LINE_CAP_ROUND), lineColor(Color.parseColor("#ff69b4")),
                        lineWidth(1f));

                mMap.addLayer(terrainData);
            } else {
                mMap.removeLayer("terrain-data");
                mMap.removeSource("terrain-data");
            }
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mMap = mapboxMap;
        mMap.addOnMapClickListener(this);
        mProvider = WMSTileFactory.getWMSTileProvider(WMSTileFactory.TPType.normal, tileXSize, tileYSize);
        onCheckedChanged(mSwitchCargo, true);
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (mMapMarker != null) {
            mMap.removeMarker(mMapMarker);
            mMapMarker.remove();
        }

        int zoomLevel = (int) mMap.getCameraPosition().zoom;

        /** Get X,Y in meter unit */
        int[] ret = mProvider.getBoundingBoxIJ(point, zoomLevel);
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

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (mMapMarker != null) {
            mMapMarker.remove();
            mMap.removeMarker(mMapMarker);
        }
        return true;
    }

    private void addMarker(Feature feature) {
        float[] point = feature.getGeometryCoordinate();
        Log.d("JongLim", String.format("Features X,Y = (%f, %f)", point[0], point[1]));

        float lng = (float) WMSTileFactory.x2lon(point[0]);
        float lat = (float) WMSTileFactory.y2lat(point[1]);
        LatLng latLng = new LatLng(lat, lng);
        feature.setGeometryCoordinate(lng, lat);

        if (mPopupHolder == null) {
            mPopupHolder = new MarkerInfoHolder(this);
        }
        mPopupHolder.setBean(feature);

        mInfoAdapter = new MarkerInfoAdapter();
        mInfoAdapter.setWindowView(mPopupHolder.getRoot());
        mMap.setInfoWindowAdapter(mInfoAdapter);

        Icon icon = IconFactory.getInstance(this).fromResource(R.drawable.vessel_marker);
        mMapMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(icon));
        if (!mMapMarker.isInfoWindowShown()) {
            mMapMarker.showInfoWindow(mMap, mapView);
        }

    }
}
