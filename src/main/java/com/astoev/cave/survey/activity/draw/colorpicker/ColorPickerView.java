package com.astoev.cave.survey.activity.draw.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by astoev on 10/24/15.
 */
public class ColorPickerView extends View {

    private static final int[] COLORS = new int[]{ 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };
    private static final float PI = 3.1415926f;

    private int mCenterX;
    private int mCenterY;
    private int mCenterRadius;

    private Paint mPaint;
    private Paint mCenterPaint;

    private ColorChangedListener mListener;
    private boolean mTrackingCenter;
    private boolean mHighlightCenter;
    private RectF mRectangle = new RectF();


    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Shader s = new SweepGradient(0, 0, COLORS, null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setShader(s);
        mPaint.setStyle(Paint.Style.STROKE);

        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setWillNotDraw(false);
    }

    public void setInitialColor(int initialColor) {
        mCenterPaint.setColor(initialColor);
    }

    public void setListener(ColorChangedListener listener) {
        mListener = listener;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        mCenterX = canvas.getWidth() / 2;
        mCenterY = canvas.getHeight() / 2;
        int baseScreenSize = Math.min(canvas.getWidth(), canvas.getHeight());
        mCenterRadius = baseScreenSize / 6;
        mPaint.setStrokeWidth(baseScreenSize / 5);
        mCenterPaint.setStrokeWidth(baseScreenSize / 30);

        float r = baseScreenSize / 3;

        canvas.translate(mCenterX, mCenterY);

        mRectangle.set(-r, -r, r, r);
        canvas.drawOval(mRectangle, mPaint);
        canvas.drawCircle(0, 0, mCenterRadius, mCenterPaint);

        if (mTrackingCenter) {
            int c = mCenterPaint.getColor();
            mCenterPaint.setStyle(Paint.Style.STROKE);

            if (mHighlightCenter) {
                mCenterPaint.setAlpha(0xFF);
            } else {
                mCenterPaint.setAlpha(0x80);
            }
            canvas.drawCircle(0, 0, mCenterRadius + mCenterPaint.getStrokeWidth(), mCenterPaint);

            mCenterPaint.setStyle(Paint.Style.FILL);
            mCenterPaint.setColor(c);
        }
    }

    private int ave(int s, int d, float p) {
        return s + java.lang.Math.round(p * (d - s));
    }

    private int interpColor(int[] colors, float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int) p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - mCenterX;
        float y = event.getY() - mCenterY;
        boolean inCenter = java.lang.Math.sqrt(x * x + y * y) <= mCenterRadius;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTrackingCenter = inCenter;
                if (inCenter) {
                    mHighlightCenter = true;
                    invalidate();
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                if (mTrackingCenter) {
                    if (mHighlightCenter != inCenter) {
                        mHighlightCenter = inCenter;
                        invalidate();
                    }
                } else {
                    float angle = (float) java.lang.Math.atan2(y, x);
                    // need to turn angle [-PI ... PI] into unit [0....1]
                    float unit = angle / (2 * PI);
                    if (unit < 0) {
                        unit += 1;
                    }
                    mCenterPaint.setColor(interpColor(COLORS, unit));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTrackingCenter) {
                    if (inCenter) {
                        mListener.colorChanged(mCenterPaint.getColor());
                    }
                    mTrackingCenter = false;    // so we draw w/o halo
                    invalidate();
                }
                break;
        }
        return true;
    }
}