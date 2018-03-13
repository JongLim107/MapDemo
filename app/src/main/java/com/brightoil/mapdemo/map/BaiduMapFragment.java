package com.brightoil.mapdemo.map;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;


public class BaiduMapFragment extends Fragment {
    private MapView b;
    private BaiduMapOptions c;
    private BaiMapReadyCallback readyCallback;

    public BaiduMapFragment() {
    }


    public static BaiduMapFragment newInstance() {
        return new BaiduMapFragment();
    }

    public static BaiduMapFragment newInstance(BaiduMapOptions var0, BaiMapReadyCallback readyCallback) {
        BaiduMapFragment fragment = new BaiduMapFragment();
        fragment.c = var0;
        fragment.readyCallback = readyCallback;
        return fragment;
    }

    public BaiduMap getBaiduMap() {
        return this.b == null ? null : this.b.getMap();
    }

    public MapView getMapView() {
        return this.b;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onCreate(Bundle var1) {
        super.onCreate(var1);
    }

    public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
        this.b = new MapView(this.getActivity(), this.c);
        if (readyCallback != null) {
            readyCallback.onMapReady(b.getMap());
        }
        return this.b;
    }

    public void onViewCreated(View var1, Bundle var2) {
        super.onViewCreated(var1, var2);
    }

    public void onActivityCreated(Bundle var1) {
        super.onActivityCreated(var1);
    }

    public void onViewStateRestored(Bundle var1) {
        super.onViewStateRestored(var1);
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        this.b.onResume();
    }

    public void onSaveInstanceState(Bundle var1) {
        super.onSaveInstanceState(var1);
    }

    public void onPause() {
        super.onPause();
        this.b.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.b.onDestroy();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDetach() {
        super.onDetach();
    }

    public void onConfigurationChanged(Configuration var1) {
        super.onConfigurationChanged(var1);
    }

    public BaiMapReadyCallback getReadyCallback() {
        return readyCallback;
    }

    public void setReadyCallback(BaiMapReadyCallback readyCallback) {
        this.readyCallback = readyCallback;
    }

    public interface BaiMapReadyCallback {
        void onMapReady(BaiduMap map);
    }
}
