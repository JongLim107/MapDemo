package com.brightoil.mapdemo.network;

import android.content.Context;
import android.util.Log;

import com.brightoil.mapdemo.widgets.MyProgressDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JongLim on 2017/12/6.
 */

public abstract class MyCallback<T> extends Callback<T> {
    Class<T> clazz;
    private MyProgressDialog mLoadingDialog;

    /**
     * @param context
     * @param clazz
     * @param progressHint hint text that will display int the process loading dialog
     */
    protected MyCallback(Context context, Class<T> clazz, String progressHint) {
        if (progressHint == null) {
            return;
        }
        this.mLoadingDialog = new MyProgressDialog(context);
        this.mLoadingDialog.initDialog(false, progressHint);
        this.clazz = clazz;
    }

    @Override
    public void onBefore(Request request, int id) {
        Log.v("MyCallback", request.toString());
        if (mLoadingDialog != null) {
            mLoadingDialog.show();
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        onFailed(e.toString(), -1, id);
        dismissDialog();
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        T result = null;
        if (null != response && null != response.body()) {
            String body = response.body().string();
            Gson gson = new Gson();
            if (clazz == String.class) {
                result = (T) body;
            } else if (body.startsWith("{")) {
                result = gson.fromJson(body, clazz);
            } else if (body.startsWith("[")) {  //If this API return a list, use this one
                result = gson.fromJson(body, new TypeToken<ArrayList<T>>() {}.getType());
            }
        }
        return result;
    }

    @Override
    public void onResponse(T response, int id) {
        dismissDialog();
        if (response == null) {
            onFailed("Response is empty", -1, id);
        } else {
            onSucceed(response, id);
        }
    }

    private void dismissDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public abstract void onSucceed(T body, int id);

    public abstract void onFailed(String exception, int code, int id);
}
