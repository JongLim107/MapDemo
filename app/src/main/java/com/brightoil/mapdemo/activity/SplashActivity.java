package com.brightoil.mapdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.brightoil.mapdemo.tools.AnimationUtils;

/**
 * welcome page
 */
public class SplashActivity extends Activity implements View.OnClickListener {

    private final static int delay = 2000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        LinearLayout btnLayout = findViewById(R.id.linerLayout);
        AnimationUtils.showAndHiddenAnimation(btnLayout, AnimationUtils.AnimationState.STATE_SHOW, delay);

        findViewById(R.id.btnLeft).setOnClickListener(this);
        findViewById(R.id.btnRight).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLeft){
            startActivity(new Intent(this, GgMapActivity.class));
        } else if (view.getId() == R.id.btnRight){
            startActivity(new Intent(this, MapBoxActivity.class));
        }
    }
}
