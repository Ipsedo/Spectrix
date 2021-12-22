package com.samuelberrien.spectrix.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
 */
public final class ExpandCollapseView {

    private final View v;
    private final int duration;
    private final int height;
    private final AnimationListener listener;

    public ExpandCollapseView(View v, int duration, int widthMesureSpec, int heightMesureSpec, AnimationListener animationListener) {
        this.v = v;
        this.duration = duration;
        v.measure(widthMesureSpec, heightMesureSpec);
        this.height = v.getMeasuredHeight();
        listener = animationListener;
    }

    public void expand() {
        int prevHeight = 0;
        //v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, height);
        valueAnimator.addUpdateListener((ValueAnimator animation) -> {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onExpandEnd(v);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                listener.onExpandStart(v);
            }
        });

        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void collapse() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(height, 0);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener((ValueAnimator animation) -> {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onCollapseEnd(v);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                listener.onCollapseStart(v);
            }
        });

        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public interface AnimationListener {

        void onCollapseEnd(View v);

        void onExpandEnd(View v);

        void onCollapseStart(View v);

        void onExpandStart(View v);
    }
}
