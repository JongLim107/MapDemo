package com.brightoil.mapdemo.map;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MapMarker {
    private int drawableResId;
    private CustomLocation location;
    private Object mObject;//在googleMapadapter 和 daiduMapadapter 会用到该字段 保存各自真实的Marker对象
    private Object mPopupInfo;//点击marker时弹出popup  附带的信息

    private MapAdapter.OnMarkerClickListener mClickListener;

    public MapMarker(@NonNull CustomLocation location, int drawableResId,
            @Nullable MapAdapter.OnMarkerClickListener listener) {
        this.location = location;
        this.drawableResId = drawableResId;
        this.mClickListener = listener;
    }

    public int getDrawableResId() {
        return drawableResId;
    }

    public void setDrawableResId(int drawableResId) {
        this.drawableResId = drawableResId;
    }

    public CustomLocation getLocation() {
        return location;
    }

    public void setLocation(CustomLocation location) {
        this.location = location;
    }

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        mObject = object;
    }

    public Object getPopupInfo() {
        return mPopupInfo;
    }

    public void setPopupInfo(Object popupInfo) {
        mPopupInfo = popupInfo;
    }

    public MapAdapter.OnMarkerClickListener getClickListener() {
        return mClickListener;
    }

    public void setClickListener(MapAdapter.OnMarkerClickListener clickListener) {
        mClickListener = clickListener;
    }
}

