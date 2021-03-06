package com.samuelberrien.spectrix.ui.expand;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by samuel on 15/10/17.
 */

public class RadioExpand extends LinearLayout implements Runnable {

    private ArrayList<ExpandButton> expandButtons;
    private LayoutParams layoutParams;

    public RadioExpand(Context context) {
        super(context);
        init();
    }

    public RadioExpand(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadioExpand(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        expandButtons = new ArrayList<>();
        layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, getResources().getDisplayMetrics());
        layoutParams.setMargins(margin, margin, margin, 0);
        setOrientation(VERTICAL);
    }

    public void addExpandButton(ExpandButton expandButton) {
        expandButton.setOnExpandListener(this);
        expandButtons.add(expandButton);
        addView(expandButton, layoutParams);
    }

    @Override
    public void run() {
        for (ExpandButton eB : expandButtons) {
            eB.collapse();
        }
    }
}
