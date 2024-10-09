package com.zzy.medicinewarehouse.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.xutils.common.util.LogUtil;

public class RulerWidget extends View {

    public static String[] indexStr = {
            "#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    public static int INDEX_LENGTH = indexStr.length;

    OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    Paint mPaint = new Paint();
    boolean showBkg = false;
    int choose = -1;

    public RulerWidget(Context context) {
        super(context);
    }

    public RulerWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RulerWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(String[] data) {
        indexStr = data;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//		if(showBkg){
//        canvas.drawColor(Color.parseColor("#40000000"));
//		 }

        int height = getHeight();
        int width = getWidth();

//        int singleHeight = height / indexStr.length;
        int singleHeight = 60;
        int weizhi = height / 2 - indexStr.length / 2 * singleHeight;
        for (int i = 0; i < indexStr.length; i++) {
            mPaint.setColor(Color.GRAY);
            mPaint.setTextSize(35);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setAntiAlias(true);
            if (i == choose) {
                mPaint.setColor(Color.parseColor("#3399ff"));
                mPaint.setFakeBoldText(true);
            }
            float xPos = width / 2 - mPaint.measureText(indexStr[i]) / 2;
            float yPos = singleHeight * i + weizhi;
            canvas.drawText(indexStr[i], xPos, yPos, mPaint);
            mPaint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;

        int singleHeight = 60;
        int weizhi = getHeight() / 2 - indexStr.length / 2 * singleHeight;

        LogUtil.i("zzy---" + weizhi + "---" + y);


        final int c = (int) ((y - weizhi + 55) / 60);

//        weizhi <=  y  <  weizhi+singleHeight
//        777 - 791 + 60 = 46/60  = 0.76
//        840 -791 +60 = 109/60 = 1.81
//        902 - 791 + 60 = 171 / 60 = 2.82


        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if (oldChoose != c && listener != null) {
                    if (c > -1 && c < indexStr.length) {
                        listener.onTouchingLetterChanged(indexStr[c]);
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && listener != null) {
                    if (c > -1 && c < indexStr.length) {
                        listener.onTouchingLetterChanged(indexStr[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
//	                showBkg = false;
                choose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * @date 2014-9-3
     * @Description: ruler触摸回调
     */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

}