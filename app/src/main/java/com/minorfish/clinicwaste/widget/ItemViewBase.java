package com.minorfish.clinicwaste.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ItemViewBase extends LinearLayout {
    public ItemViewBase(Context context) {
        super(context);
    }

    public ItemViewBase(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemViewBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
