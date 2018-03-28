package com.brightoil.mapdemo.activity;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.View;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.brightoil.mapdemo.map.MapAdapterFactory;
import com.brightoil.mapdemo.tools.AppManager;
import com.fm.openinstall.OpenInstall;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mapbox.mapboxsdk.Mapbox;

import static com.brightoil.mapdemo.map.MapAdapterFactory.BAIDU_MAP;

/**
 * Created by JongLim on 2018-03-09.
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    private boolean baiduMapInit = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // register activity manager
        registerActivityLifecycleCallbacks(AppManager.getActivityLifecycleCallbacks());
        getDisplayInfo();
        instance = this;

        // init baidu map and google map
        setDefaultMapProvider();
        intiBaiduMap();
        Mapbox.getInstance(getApplicationContext(), getString(R.string.access_token));
        if (isMainProcess()) {
            OpenInstall.init(this);
        }
    }

    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }

    public boolean isSupportGooglePlay() {
        boolean support = true;
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            support = false;
        }
        return support;
    }

    public void setDefaultMapProvider() {
        if (!MapAdapterFactory.hasSetDefaultMapProvider(this)) {
            boolean isSupportGoogle = isSupportGooglePlay();
            if (isSupportGoogle) {
                MapAdapterFactory.saveMapProvider(this, MapAdapterFactory.GOOGLE_MAP);
            } else {
                MapAdapterFactory.saveMapProvider(this, BAIDU_MAP);
            }
        }
    }

    public void intiBaiduMap() {
        if (MapAdapterFactory.getMapProvider(this.getApplicationContext()) == BAIDU_MAP && !baiduMapInit) {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.initialize(this);
            //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
            //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
            SDKInitializer.setCoordType(CoordType.BD09LL);
            baiduMapInit = true;
        }
    }

    private void getDisplayInfo() {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        final float density = displayMetrics.density;
        final float densityDpi = displayMetrics.densityDpi;
        System.out.print("ScreenSize = " + screenWidth + " X " + screenHeight + "\n"
                + "density = " + density + " densityDpi = " + densityDpi + "displayMetrics = " + displayMetrics.toString());
    }

    public synchronized static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    public void exit() {
        AppManager.closeAllActivity();
    }

    public static void delayActivateView(final View view) {
        if (view == null) {
            return;
        }
        view.setClickable(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
            }
        },800);
    }

    /**
     * Get App release version
     * @return version in number(#.#.#)
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.version_name) + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.can_not_find_version_name);
        }
    }
}
