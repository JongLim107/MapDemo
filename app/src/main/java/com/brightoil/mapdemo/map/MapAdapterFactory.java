package com.brightoil.mapdemo.map;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.brightoil.mapdemo.activity.MyApplication;
import com.brightoil.mapdemo.tools.SharedPreferencesUtils;

public class MapAdapterFactory {

    public final static int GOOGLE_MAP = 1;
    public final static int BAIDU_MAP = 2;

    /**
     * get a MapAdapter  from factory   method must  be called  in MainThread
     *
     * @param options
     * @param loadListener
     * @return
     */
    public static MapAdapter createAdapter(@Nullable MapSettingOptions options, @Nullable MapAdapter.OnLoadCompletedListener loadListener) {
        int provider = getMapProvider(MyApplication.getInstance());
        MyApplication.getInstance().intiBaiduMap();
        if (provider == BAIDU_MAP) {
            return BaiduMapAdapter.newInstance(options, loadListener);
        } else {
            return GoogleMapAdapter.newInstance(options, loadListener);
        }
    }

    @IntDef({GOOGLE_MAP, BAIDU_MAP})
    public @interface MapProvider {
    }

    public static void saveMapProvider(Context context, @MapProvider int provider) {
        SharedPreferencesUtils.put(context, "MapProvider", provider);
    }

    public static
    @MapProvider
    int getMapProvider(Context context) {
        int provide = SharedPreferencesUtils.get(context, "MapProvider", GOOGLE_MAP);
        if (provide == BAIDU_MAP) {
            return BAIDU_MAP;
        } else {
            if (!MyApplication.getInstance().isSupportGooglePlay()) {
                return BAIDU_MAP;
            }
            return GOOGLE_MAP;
        }
    }

    public static boolean hasSetDefaultMapProvider(Context context) {
        int provider = SharedPreferencesUtils.get(context, "MapProvider", 0);
        return provider != 0;
    }
}
