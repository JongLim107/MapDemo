package com.brightoil.mapdemo.network;

import com.brightoil.mapdemo.action.Config;
import com.brightoil.mapdemo.activity.WMSTileFactory;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;

import okhttp3.MediaType;

/**
 * Created by JongLim on 2017/12/1.
 */

public class MyRequestManager {
    private static String TAG = "MyRequestManager";

    private static PostFormBuilder getBuilder(String action, String body) {
        PostFormBuilder builder = new PostFormBuilder();

        builder.url(body);
        /*builder.addParams("CMD", "HTTPAPI");
        body = initRequest(action, body);
        if (body != null) {
            builder.addParams("JSONREQ", body.toString());
            Log.e(TAG, body.toString());
        }*/
        return builder;
    }

    private static PostStringBuilder getBuilder(String content) {
        PostStringBuilder builder = new PostStringBuilder();
        builder.url(Config.server);
        builder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        builder.content("This is the string content");
        //builder.content(content);
        return builder;
    }

    /**
     * Public API
     * Callback:
     if (mHandler.getLooper().getThread().isAlive()) {
         Bundle b = new Bundle();
         b.putString("JSONDATA", retStr);
         Message msg = new Message();
         msg.setData(b);
         msg.what = iActionType;
         mHandler.sendMessage(msg);
     }
     * */
    public static void getFeatureInfo(@WMSTileFactory.GeoLayers String layer, int[] xy, double[] bbox, MyCallback callback){
        String url = Config.server + "&SERVICE=" + Config.service + "&VERSION=" + Config.ver1 + "&CRS=" + Config.CRS + "&REQUEST=" + Config.req + "&FORMAT="
                + Config.format + "&TRANSPARENT=" + true + "&QUERY_LAYERS=gis:" + layer + "&LAYERS=gis:" + layer + "&TILED=" + true +
                "&INFO_FORMAT=" + Config.info_fmt + "&I=" + xy[0] + "&J=" + xy[1] + "&WIDTH=" + xy[2] + "&HEIGHT=" + xy[2] + "&BBOX=" + String
                .format("%.4f,%.4f,%.4f,%.4f", bbox[0], bbox[1], bbox[2], bbox[3]);

        getBuilder("", url).build().execute(callback);
    }

}
