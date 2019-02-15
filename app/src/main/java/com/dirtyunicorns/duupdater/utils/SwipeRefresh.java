package com.dirtyunicorns.duupdater.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.dirtyunicorns.duupdater.R;

public class SwipeRefresh extends SwipeRefreshLayout {

    public SwipeRefresh(@NonNull Context context) {
        super(context);
    }

    public static int getBackground(Context context) {
        TypedArray array = context.obtainStyledAttributes(new int[]{R.attr.card_background_color});
        int color = array.getColor(0, 0);
        array.recycle();
        return color;
    }

    public SwipeRefresh(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setProgressBackgroundColorSchemeColor(getBackground(getContext()));
        setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
    }
}