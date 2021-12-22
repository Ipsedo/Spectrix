package com.samuelberrien.spectrix.ui;

import android.view.MotionEvent;

/**
 * Created by samuel on 30/08/17.
 */

/**
 * faire mon propre bail -> plein de cas où ça marche pas
 */
public class RotationGestureDetector {
    private float fX, fY, sX, sY;
    private float mAngle;
    private boolean reinitPositions;

    private final OnRotationGestureListener mListener;

    public float getAngle() {
        return mAngle;
    }

    public RotationGestureDetector(OnRotationGestureListener listener) {
        mListener = listener;
        reinitPositions = true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (reinitPositions && event.getPointerCount() > 1) {
            sX = event.getX(0);
            sY = event.getY(0);
            fX = event.getX(1);
            fY = event.getY(1);
            reinitPositions = false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                sX = event.getX(0);
                sY = event.getY(0);
                fX = event.getX(1);
                fY = event.getY(1);
                if (mListener != null) {
                    mListener.onRotationBegin();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    float nfX, nfY, nsX, nsY;
                    nsX = event.getX(0);
                    nsY = event.getY(0);
                    nfX = event.getX(1);
                    nfY = event.getY(1);

                    mAngle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY);

                    if (mListener != null) {
                        mListener.onRotation(this);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                reinitPositions = true;
                break;
        }
        return true;
    }

    private float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
        float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
        float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return angle;
    }

    public interface OnRotationGestureListener {
        void onRotationBegin();

        void onRotation(RotationGestureDetector rotationDetector);
    }
}
