package com.samuelberrien.spectrix.ui.main;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by samuel on 10/12/17.
 */

public class SpectrixToolBar extends Toolbar {

    public SpectrixToolBar(Context context) {
        super(context);
    }

    public SpectrixToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpectrixToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

}
