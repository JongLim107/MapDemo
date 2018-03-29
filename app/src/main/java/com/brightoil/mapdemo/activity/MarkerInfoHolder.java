package com.brightoil.mapdemo.activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brightoil.mapdemo.bean.Feature;
import com.brightoil.mapdemo.widgets.LabelTextView;

/**
 * Created by JongLim on 2018-03-12.
 */

public class MarkerInfoHolder {

    private View root;
    private ImageView imgClose;
    private LabelTextView ltvTitle;
    private LabelTextView ltvLongitude;
    private LabelTextView ltvLatitude;
    private LabelTextView ltvMmsi;

    MarkerInfoHolder(Activity activity, ViewGroup p) {
        root = LayoutInflater.from(activity).inflate(R.layout.vessel_marker, p);
        imgClose = (ImageView) root.findViewById(R.id.imgClose);
        ltvTitle = (LabelTextView) root.findViewById(R.id.ltvTitle);
        ltvLongitude = (LabelTextView) root.findViewById(R.id.ltvLongitude);
        ltvLatitude = (LabelTextView) root.findViewById(R.id.ltvLatitude);
        ltvMmsi = (LabelTextView) root.findViewById(R.id.ltvMmsi);
    }

    public View getRoot() {
        return root;
    }

    public void setBean(Feature bean) {
        if (bean == null) {
            return;
        }

        ltvTitle.setText(bean.getFeatureName());
        ltvLatitude.setText(String.format("%.2f", bean.getGeometryCoordinate()[0]));
        ltvLongitude.setText(String.format("%.2f", bean.getGeometryCoordinate()[1]));
        ltvMmsi.setText(bean.getFeatureMmsi());
    }
}
