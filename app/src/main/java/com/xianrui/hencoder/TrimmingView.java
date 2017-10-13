package com.xianrui.hencoder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;


/**
 * Created by xianrui on 2017/8/21.
 */

public class TrimmingView extends View {

    //文字大小
    @Px
    private static final int TEXT_SIZE = 40;

    //每隔多小个小格高亮
    @Px
    private static final int HIGHLIGHT_INTERVAL = 10;

    //小格之间的距离
    @Px
    private static final int TICK_DISTANCE = 30;

    //文字和卡尺之间的距离
    @Px
    private static final int TEXT_TO_TICK_DISTANCE = 24;

    //刻度宽度
    @Px
    private static final int TICK_WIDTH = 4;

    private boolean isLoop = true;

    private Paint mPaint;
    private TextPaint mTextPaint;
    private float mOffset;

    private int mTickColor = Color.BLACK;

    private int mTextColor = Color.BLACK;

    private int mCursorColor = Color.GREEN;

    private float mMaxValue = 1000;

    private float mMinValue = 0;

    private float fontHeight;

    float tickHeight;

    private GestureDetector mGestureDetector;

    private Scroller mScroller;

    private OnValueChangeListener mOnValueChangeListener;

    private VelocityTracker mVelocityTracker;

    private boolean isInTouch = false;


    public TrimmingView(Context context) {
        this(context, null);
    }

    public TrimmingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrimmingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        mOnValueChangeListener = onValueChangeListener;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mTickColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(TEXT_SIZE);
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        fontHeight = fm.descent - fm.ascent;

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                if (isLoop) {
                    mOffset += distanceX;
                    if (mOffset > TICK_DISTANCE * mMaxValue || mOffset < -TICK_DISTANCE * mMaxValue) {
                        mOffset = 0;
                    }
                } else {
                    if (mOffset > 0 && mOffset < TICK_DISTANCE * mMaxValue) {
                        mOffset += distanceX;
                    }
                }

                Log.i("xianrui", "mOffset " + mOffset + " distanceX " + distanceX);
                if (mOnValueChangeListener != null) {
                    mOnValueChangeListener.onScroll(-distanceX);
                    mOnValueChangeListener.onProgressChanged(TrimmingView.this, getValue(), true);
                }
                invalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                Log.i("xianrui", "mOffset " + mOffset + " velocityX " + velocityX);
//                mOffset += velocityX;
//                invalidate();
                return false;
//                return false;
            }
        });

        mScroller = new Scroller(getContext());
    }

    public void setEnableLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    public void setTickColor(int color) {
        if (mPaint != null) {
            mPaint.setColor(color);
        }
        mTickColor = color;
    }

    public void setTextColor(int textColor) {
        if (mTextPaint != null) {
            mTextPaint.setColor(textColor);
        }
        mTextColor = textColor;
    }

    public void setCursorColor(int cursorColor) {
        mCursorColor = cursorColor;
    }

    private void moveBy(int x) {
        mScroller.startScroll((int) mOffset, 0, x, 0, 500);
        invalidate();
    }

    private void moveTo(int x) {
        moveBy((int) (x - mOffset));
    }

    private void flingBy(int x) {
        if (isLoop) {
            mScroller.fling((int) mOffset, 0, x, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
        } else {
            mScroller.fling((int) mOffset, 0, x, 0, 0, (int) (TICK_DISTANCE * mMaxValue), 0, 0);
        }
        invalidate();
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mOnValueChangeListener != null) {
                mOnValueChangeListener.onScroll(-(mScroller.getCurrX() - mOffset));
                mOnValueChangeListener.onProgressChanged(this, getValue(), true);
            }
            mOffset = mScroller.getCurrX();
            postInvalidate();
        } else {
            if (!isMoveToNearTick() && !isInTouch) {
                moveToNearTick();
            } else {
                if (mOnValueChangeListener != null) {
                    mOnValueChangeListener.onStopTrackingTouch(this);
                }
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawTick(canvas, mOffset);
        drawCursor(canvas);
    }

    private void drawCursor(Canvas canvas) {
        canvas.translate(0, getPaddingTop());

        mPaint.setColor(mCursorColor);
        mPaint.setStrokeWidth(TICK_WIDTH * 4);
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, tickHeight, mPaint);
        mPaint.setColor(mTickColor);

        canvas.translate(0, -getPaddingTop());
    }

    public void setValueRange(float min, float max) {
        this.mMinValue = min;
        this.mMaxValue = max;
        invalidate();
    }

    public void setValue(float value) {
        if (value < mMinValue || value > mMaxValue) {
            return;
        }
        float offset = (value / (mMaxValue - mMinValue)) * ((mMaxValue - mMinValue)) * TICK_DISTANCE;
        moveTo((int) offset);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isInTouch = true;
                if (mOnValueChangeListener != null) {
                    mOnValueChangeListener.onStartTrackingTouch(this);
                }
                mVelocityTracker = VelocityTracker.obtain();
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(event);
                }
                if (mScroller.computeScrollOffset()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                if()
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                isInTouch = false;
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(700);
                    float vX = mVelocityTracker.getXVelocity();
                    flingBy(-(int) vX);
                }
                break;
        }


        return mGestureDetector.onTouchEvent(event);
    }

    public float getValue() {
        return (mOffset / TICK_DISTANCE) + mMinValue;
    }

    private void moveToNearTick() {
        if (mOffset % TICK_DISTANCE != 0) {
            float offset = mOffset % TICK_DISTANCE;
            if (offset > TICK_DISTANCE / 2) {
                moveBy((int) (TICK_DISTANCE - offset));
            } else {
                moveBy((int) -offset);
            }
        }
    }

    private boolean isMoveToNearTick() {
        return mOffset % TICK_DISTANCE == 0;
    }

    private void drawTick(Canvas canvas, float offset) {
        canvas.translate(0, getPaddingTop());

        float startX = getWidth() / 2 - offset;
        float startY = 0;

        tickHeight = getHeight() - (fontHeight + TEXT_TO_TICK_DISTANCE + getPaddingTop() + getPaddingBottom());

        int index = 0;

        if (startX < 0) {
            startX = -(offset % TICK_DISTANCE);
            index = (int) ((offset - getWidth() / 2) / TICK_DISTANCE);
        } else {
            if (isLoop) {
                int count = (int) (startX / TICK_DISTANCE);
                float start = startX - (count * TICK_DISTANCE);
                index = (int) (mMaxValue - count);
                while (start < startX) {
                    if (index % HIGHLIGHT_INTERVAL == 0) {
                        mPaint.setStrokeWidth(TICK_WIDTH);
                        canvas.drawLine(start, startY, start, startY + tickHeight, mPaint);
                        if (index >= mMaxValue) {
                            index = 0;
                        }
                        canvas.drawText(String.valueOf(index / HIGHLIGHT_INTERVAL), start, startY + tickHeight + TEXT_TO_TICK_DISTANCE / 2 + fontHeight / 2, mTextPaint);
                    } else {
                        mPaint.setStrokeWidth(TICK_WIDTH / 2);
                        canvas.drawLine(start, startY + tickHeight / 2, start, startY + tickHeight, mPaint);
                    }
                    start += TICK_DISTANCE;
                    index++;
                }
            }
        }


        while (startX < getWidth() && index <= mMaxValue) {
            if (index % HIGHLIGHT_INTERVAL == 0) {
                mPaint.setStrokeWidth(TICK_WIDTH);
                if (isLoop) {
                    if (index >= mMaxValue) {
                        index = 0;
                    }
                }
                canvas.drawLine(startX, startY, startX, startY + tickHeight, mPaint);
                canvas.drawText(String.valueOf(index / HIGHLIGHT_INTERVAL), startX, startY + tickHeight + TEXT_TO_TICK_DISTANCE / 2 + fontHeight / 2, mTextPaint);
            } else {
                mPaint.setStrokeWidth(TICK_WIDTH / 2);
                canvas.drawLine(startX, startY + tickHeight / 2, startX, startY + tickHeight, mPaint);
            }

            index++;
            startX += TICK_DISTANCE;
        }

        canvas.translate(0, -getPaddingTop());
    }

    public interface OnValueChangeListener {
        public void onScroll(float distance);

        public void onProgressChanged(TrimmingView trimmingView, float progress, boolean fromUser);

        public void onStartTrackingTouch(TrimmingView trimmingView);

        public void onStopTrackingTouch(TrimmingView trimmingView);
    }
}
