package com.brightoil.mapdemo.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brightoil.mapdemo.activity.R;

/**
 * the redirect item view.
 * <p>eg. the item in home fragment, the item in menu fragment</p>
 * Created by TaoCheng.Wang on 05/29/2017.
 * <p>
 * Modify by JongLim ,Just extension does not affect the original features and functionality.
 */
public class LabelTextView extends RelativeLayout {

    private TextView labelView;
    private TextView textView;
    private ImageView iconView;
    private ImageView rightIcon;

    public LabelTextView(Context context) {
        this(context, null);
    }

    public LabelTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_custom_tv, this);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LabelTextView, defStyleAttr, 0);

            String label_text = ta.getString(R.styleable.LabelTextView_label_text);

            final int colorTextHintBlack = ContextCompat.getColor(context, R.color.text_hint_black);
            int label_color = ta.getColor(R.styleable.LabelTextView_label_color, colorTextHintBlack);
            int label_size = ta.getDimensionPixelSize(R.styleable.LabelTextView_label_size, 0);
            final int labelTextStyle = ta.getInt(R.styleable.LabelTextView_label_text_style, -1);

            String value_text = ta.getString(R.styleable.LabelTextView_value_text);
            final int colorTextBlack = ContextCompat.getColor(context, R.color.text_black);
            int value_color = ta.getColor(R.styleable.LabelTextView_value_color, colorTextBlack);
            int value_size = ta.getDimensionPixelSize(R.styleable.LabelTextView_value_size, 0);
            final int valueTextStyle = ta.getInt(R.styleable.LabelTextView_value_text_style, -1);
            int value_background = ta.getResourceId(R.styleable.LabelTextView_value_background, R.drawable.rect_trans_bg);
            int padding = ta.getDimensionPixelSize(R.styleable.LabelTextView_content_padding, 0);
            int padLeft = ta.getDimensionPixelSize(R.styleable.LabelTextView_content_padding_l, 0);
            int padRight = ta.getDimensionPixelSize(R.styleable.LabelTextView_content_padding_r, 0);
            int padBottom = ta.getDimensionPixelSize(R.styleable.LabelTextView_content_padding_b, 0);
            int padTop = ta.getDimensionPixelSize(R.styleable.LabelTextView_content_padding_t, 0);
            boolean isHorizontalStyle = ta.getBoolean(R.styleable.LabelTextView_content_horizontal, false);

            int gravity = ta.getInteger(R.styleable.LabelTextView_content_gravity, -1);

            Drawable drawable = ta.getDrawable(R.styleable.LabelTextView_label_icon);
            Drawable rightDrawable = ta.getDrawable(R.styleable.LabelTextView_right_icon);
            int tintColor = ta.getColor(R.styleable.LabelTextView_icon_tint, 1);
            int tintRightColor = ta.getColor(R.styleable.LabelTextView_right_icon_tint, 1);
            boolean isArrow = ta.getBoolean(R.styleable.LabelTextView_redirect_arrow, false);
            boolean isLine = ta.getBoolean(R.styleable.LabelTextView_redirect_line, true);

            ta.recycle();

            LinearLayout llayout = (LinearLayout) findViewById(R.id.rl_item);
            if (padding > 0) {
                llayout.setPadding(padding, padding, padding, padding);
            } else {
                llayout.setPadding(padLeft, padTop, padRight, padBottom);
            }

            if (isHorizontalStyle) {
                findViewById(R.id.ll_content_v).setVisibility(GONE);
                findViewById(R.id.ll_content_h).setVisibility(VISIBLE);
                labelView = (TextView) findViewById(R.id.tv_item_label_h);
                textView = (TextView) findViewById(R.id.tv_item_text_h);
            } else {
                labelView = (TextView) findViewById(R.id.tv_item_label);
                textView = (TextView) findViewById(R.id.tv_item_text);
            }

            if (gravity != -1){
                labelView.setGravity(gravity);
                textView.setGravity(gravity);
            }
            textView.setBackgroundResource(value_background);
            textView.setText(value_text);

            textView.setTextColor(value_color);

            if (value_size > 0) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value_size);
            }
            if (valueTextStyle > 0) {
                textView.setTypeface(Typeface.defaultFromStyle(valueTextStyle));
            }

            if (label_text != null) {
                labelView.setText(label_text);
                labelView.setTextColor(label_color);
            } else {
                labelView.setVisibility(GONE);
            }
            if (label_size > 0) {
                labelView.setTextSize(TypedValue.COMPLEX_UNIT_PX, label_size);
            }
            if (labelTextStyle > 0) {
                labelView.setTypeface(Typeface.defaultFromStyle(labelTextStyle));
            }

            iconView = (ImageView) findViewById(R.id.iv_label_icon);
            if (drawable != null) {
                if (tintColor != 1) {
                    DrawableCompat.setTint(drawable, tintColor);
                }
                iconView.setImageDrawable(drawable);
                iconView.setVisibility(VISIBLE);
            } else {
                iconView.setVisibility(View.GONE);
            }

            rightIcon = (ImageView) findViewById(R.id.iv_right_icon);
            if (rightDrawable != null) {
                if (tintRightColor != 1) {
                    DrawableCompat.setTint(rightDrawable, tintRightColor);
                }
                rightIcon.setImageDrawable(rightDrawable);
                rightIcon.setVisibility(VISIBLE);
            } else {
                rightIcon.setVisibility(View.GONE);
            }

            View arrowView = findViewById(R.id.iv_item_arrow);
            if (isArrow) {
                arrowView.setVisibility(View.VISIBLE);
            } else {
                arrowView.setVisibility(View.GONE);
            }

            View lineView = findViewById(R.id.v_item_line);
            if (isLine) {
                lineView.setVisibility(View.VISIBLE);
            } else {
                lineView.setVisibility(View.GONE);
            }

        }
    }

    public TextView getTextView() {
        return textView;
    }

    public void setText(@StringRes int resId) {
        textView.setText(resId);
    }

    public void setText(CharSequence value) {
        textView.setText(value);
    }

    public void setLabel(@StringRes int resId) {
        labelView.setText(resId);
        labelView.setVisibility(VISIBLE);
    }

    public void setLabel(String label) {
        labelView.setText(label);
        labelView.setVisibility(VISIBLE);
    }

    public void setLVVisibility(int type){
        iconView.setVisibility(type);
    }

    public void setRVVisibility(int type){
        rightIcon.setVisibility(type);
    }
}
