package com.samuelberrien.spectrix.ui.expand;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.samuelberrien.spectrix.R;


/**
 * Created by samuel on 15/10/17.
 */

public class ExpandButton extends LinearLayout {

    private Button mainButton;
    private Button confirmButton;

    private Runnable onExpandListener;

    public ExpandButton(Context context, Runnable onConfirm) {
        super(context);
        init(context, onConfirm);
    }

    private void init(Context context, final Runnable onConfirm) {
        setOrientation(HORIZONTAL);
        setLayoutTransition(new LayoutTransition());

        onExpandListener = new Runnable() {
            @Override
            public void run() {
            }
        };

        mainButton = new Button(context);
        mainButton.setAllCaps(false);
        mainButton.setGravity(Gravity.CENTER);
        mainButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_drawer));

        confirmButton = new Button(context);
        confirmButton.setGravity(Gravity.CENTER);
        confirmButton.setBackground(ContextCompat.getDrawable(context, R.drawable.start_button));
        confirmButton.setText("GO");

        confirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirm.run();
            }
        });

        mainButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onExpandListener.run();
                confirmButton.setVisibility(VISIBLE);
            }
        });

        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 0.25f;
        addView(mainButton, layoutParams);

        layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 0.75f;
        addView(confirmButton, layoutParams);

        collapse();
    }

    public void setText(CharSequence charSequence) {
        mainButton.setText(charSequence);
    }

    void collapse() {
        confirmButton.setVisibility(GONE);
    }

    void setOnExpandListener(Runnable onExpandListener) {
        this.onExpandListener = onExpandListener;
    }
}
