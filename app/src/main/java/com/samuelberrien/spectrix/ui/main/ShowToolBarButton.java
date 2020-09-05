package com.samuelberrien.spectrix.ui.main;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by samuel on 11/12/17.
 */

public class ShowToolBarButton extends AppCompatButton {
    public ShowToolBarButton(Context context) {
        super(context);
    }

    public ShowToolBarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowToolBarButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        return true;
    }
}
