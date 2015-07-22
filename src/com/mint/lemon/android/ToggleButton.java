/*
>  SettingsToggle.java
>
>  Copyright (c) 2014 Geographic Information Services, Inc
>
>  Released under an MIT License
>
>  https://github.com/gisinc/android-toggle-button
> */
package com.mint.lemon.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mint.lemon.AndroidToggleButton.R;

public class ToggleButton extends RelativeLayout implements View.OnClickListener {

    FrameLayout layout;
    View toggleCircle, background_oval_off, background_oval_on;
    TextView textView;
    int trackWidth;
    int trackHeight;

    private boolean checked;

    private Boolean _crossfadeRunning = false;
    private ObjectAnimator _oaLeft, _oaRight;

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        String text, textColor;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout, this, true);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ToggleButton);


        text = a.getString(R.styleable.ToggleButton_text);
        textColor = a.getString(R.styleable.ToggleButton_textColor);

        background_oval_off = findViewById(R.id.background_oval_off);
        background_oval_on = findViewById(R.id.background_oval_on);
        toggleCircle = findViewById(R.id.toggleCircle);
        textView = (TextView) findViewById(R.id.text);
        layout = (FrameLayout) findViewById(R.id.layout);


        Drawable TrackDrawableOff = a.getDrawable(R.styleable.ToggleButton_track_color_off);
        if (TrackDrawableOff != null) {
            background_oval_off.setBackground(TrackDrawableOff);
        }
        Drawable TrackDrawableOn = a.getDrawable(R.styleable.ToggleButton_track_color_on);
        if (TrackDrawableOn != null) {
            background_oval_on.setBackground(TrackDrawableOn);
        }
        Drawable ThumbDrawable = a.getDrawable(R.styleable.ToggleButton_thumb_color);
        if (ThumbDrawable != null) {
            toggleCircle.setBackground(ThumbDrawable);
        }
        trackWidth = (int) a.getDimension(R.styleable.ToggleButton_width, 66f);
        trackHeight = (int) a.getDimension(R.styleable.ToggleButton_height, 33f);

        background_oval_off.setLayoutParams(new FrameLayout.LayoutParams(trackWidth, trackHeight));
        background_oval_on.setLayoutParams(new FrameLayout.LayoutParams(trackWidth, trackHeight));
        toggleCircle.setLayoutParams(new FrameLayout.LayoutParams(trackHeight, trackHeight));

        textView.setText(text);
        if (textColor != null) textView.setTextColor(Color.parseColor(textColor));
        layout.setOnClickListener(this);
        a.recycle();

        //get a pixel size for a particular dimension - will differ by device according to screen density
        _oaLeft = ObjectAnimator.ofFloat(toggleCircle, "x", trackWidth / 2, 0).setDuration(250);
        _oaRight = ObjectAnimator.ofFloat(toggleCircle, "x", 0, trackWidth / 2).setDuration(250);


        //    setState();
    }

    public ToggleButton(Context context) {
        this(context, null);
    }


    private void _crossfadeViews(final View begin, View end, int duration) {
        _crossfadeRunning = true;

        end.setAlpha(0f);
        end.setVisibility(View.VISIBLE);
        end.animate().alpha(1f).setDuration(duration).setListener(null);
        begin.animate().alpha(0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                begin.setVisibility(View.GONE);
                _crossfadeRunning = false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        checked = !checked;
        toggle(true);
        if (mListener != null) {
            mListener.onCheckedChanged(null, checked);
        }
    }

    private void toggle(boolean animate) {
        if (_oaLeft.isRunning() || _oaRight.isRunning() || _crossfadeRunning) return;

        if (checked) {
            _oaRight.start();
            _crossfadeViews(background_oval_off, background_oval_on, animate ? 400 : 0);
        } else {
            _oaLeft.start();
            _crossfadeViews(background_oval_on, background_oval_off, animate ? 110 : 0);
        }

    }

    public void setChecked(boolean value) {
        checked = value;
        toggle(false);
    }

    public boolean isChecked() {
        return checked;
    }

    CompoundButton.OnCheckedChangeListener mListener;

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        this.mListener = listener;
    }
}
