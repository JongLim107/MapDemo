package com.brightoil.mapdemo.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brightoil.mapdemo.activity.R;

/**
 * Created by JongLim on 2016/12/23.
 */

public class MyProgressDialog extends Dialog{
    private static final String TAG = "MyProDlog";

    private Context mContext;
    private TextView tipTextView;
    private OnCancelListener mListener;

    public MyProgressDialog(Context context) {
        super(context, R.style.loading_dialog);// 创建自定义样式dialog
        mContext = context;
    }

    public MyProgressDialog(Context context, OnCancelListener listener) {
        super(context, R.style.loading_dialog);// 创建自定义样式dialog
        mContext = context;
        mListener = listener;
    }

    public void initDialog(boolean cancelable, String msg) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_loading, null);

        this.setContentView(v.findViewById(R.id.dialog_view), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
                .MATCH_PARENT));
        this.setCancelable(cancelable);

//        // 使用ImageView显示动画
//        Animation animation= AnimationUtils.loadAnimation(mContext, R.anim.load_animation);
//        ImageView imageView = (ImageView) v.findViewById(R.id.img);
//        imageView.startAnimation(animation);

        // 提示文字
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        tipTextView.setText(msg);// 设置加载信息
    }

    public void setMessage(String msg){
        if (tipTextView != null ) {
            tipTextView.setText(msg);// 设置加载信息
        }
    }

    public void setListener(OnCancelListener listener) {
        mListener = listener;
    }

    public void dismissAfter(int ms){
        if (tipTextView != null) {
            tipTextView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.this.dismiss();
                }
            }, ms);
        }

        if (mListener != null) {
            mListener.onCancel(this);
        }
    }
}
