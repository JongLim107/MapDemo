package com.brightoil.mapdemo.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mapbox.services.commons.geojson.Geometry;
import com.mapbox.services.commons.geojson.custom.GeometryDeserializer;
import com.mapbox.services.commons.geojson.custom.PositionDeserializer;
import com.mapbox.services.commons.geojson.custom.PositionSerializer;
import com.mapbox.services.commons.models.Position;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;

/**
 * Created by JongLim on 2018-04-04.
 */

public abstract class MyGeoJsonCallback<T> extends MyCallback<T> {

    /**
     * @param context
     * @param clazz
     * @param progressHint hint text that will display int the process loading dialog
     */
    protected MyGeoJsonCallback(Context context, Class clazz, String progressHint) {
        super(context, clazz, progressHint);
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        T result = null;
        if (null != response && null != response.body()) {
            String body = response.body().string();
            if (clazz == String.class) {
                result = (T) body;
            } else if (body.startsWith("{")) {
                GsonBuilder gson = new GsonBuilder()
                        .registerTypeAdapter(Geometry.class, new GeometryDeserializer())
                        .registerTypeAdapter(Position.class, new PositionSerializer())
                        .registerTypeAdapter(Position.class, new PositionDeserializer());
                result = gson.create().fromJson(body, clazz);
            } else if (body.startsWith("[")) {  //If this API return a list, use this one
                Gson gson = new Gson();
                result = gson.fromJson(body, new TypeToken<ArrayList<T>>() {}.getType());
            }
        }
        return result;
    }

}
