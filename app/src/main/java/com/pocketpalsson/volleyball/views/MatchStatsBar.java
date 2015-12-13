package com.pocketpalsson.volleyball.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.utilities.ContextHelper;

import io.sweers.barber.Barber;
import io.sweers.barber.Required;
import io.sweers.barber.StyledAttr;

public class MatchStatsBar extends View {
    @Required
    @StyledAttr(R.styleable.MatchStatsBar_isOnLeftSide)
    public boolean isOnLeftSide;
    private Paint paint = new Paint();
    private Paint textPaint = new Paint();
    private float barHeight;

    private int value;
    private int maxValue;
    private int absoluteMaxValue;

    public MatchStatsBar(Context context) {
        super(context);
    }

    public MatchStatsBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchStatsBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            maxValue = (int) (Math.random() * 50);
            value = (int) (Math.random() * maxValue);
        }
        Barber.style(this, attrs, R.styleable.MatchStatsBar, defStyleAttr);
        barHeight = getContext().getResources().getDimension(R.dimen.stats_bar_height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int dpSize = ContextHelper.dpToPixels(getContext(), 1);
//        int radius = 6 * dpSize;
//        Path roundedRect = roundedRect(0, 0, getWidth(), getHeight(), radius);
//        canvas.clipPath(roundedRect);
        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.WHITE);
//        Rect containerRect = new Rect(0, 0, getWidth(), getHeight());
//        canvas.drawRect(containerRect, paint);

        int barWidth = (int) ((float) value / absoluteMaxValue * getWidth());
        paint.setColor(value == maxValue ? ContextCompat.getColor(getContext(), R.color.accent) : Color.GRAY);

        String valueText = "" + value;
        Rect bounds = new Rect();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        textPaint.getTextBounds(valueText, 0, valueText.length(), bounds);
        float textHeight = bounds.bottom - bounds.top;
        float textWidth = bounds.right - bounds.left;
        if (isOnLeftSide) {
            Rect rect = new Rect(getWidth() - barWidth, (int) (getHeight() - barHeight) / 2, getWidth(), (int) (getHeight() + barHeight) / 2);
            canvas.drawRect(rect, paint);
            paint.setStyle(Paint.Style.STROKE);

            canvas.drawRect(rect, paint);
//            canvas.drawText(valueText, getWidth() - barWidth + 10 * dpSize, getHeight() / 2 + textHeight / 2, textPaint);
        } else {
            Rect rect = new Rect(0, (int) (getHeight() - barHeight) / 2, barWidth, (int) (getHeight() + barHeight) / 2);
            canvas.drawRect(rect, paint);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(rect, paint);
//            canvas.drawText(valueText, barWidth - 10 * dpSize - textWidth, getHeight() / 2 + textHeight / 2, textPaint);
        }
    }

    public Path roundedRect(float left, float top, float right, float bottom, float radius) {
        Path path = new Path();
        if (radius < 0) radius = 0;
        float width = right - left;
        float height = bottom - top;
        if (radius > width / 2) radius = width / 2;
        if (radius > height / 2) radius = height / 2;
        float widthMinusCorners = (width - (2 * radius));
        float heightMinusCorners = (height - (2 * radius));

        path.moveTo(right, top + radius);

        //top-right corner
        path.rQuadTo(0, -radius, -radius, -radius);
        path.rLineTo(-widthMinusCorners, 0);

        //top-left corner
        path.rQuadTo(-radius, 0, -radius, radius);
        path.rLineTo(0, heightMinusCorners);

        //bottom-left corner
        path.rQuadTo(0, radius, radius, radius);

        path.rLineTo(widthMinusCorners, 0);
        //bottom-right corner
        path.rQuadTo(radius, 0, radius, -radius);

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }

    public void setValues(int value, int maxValue, int absoluteMaxValue) {
        this.value = value;
        this.maxValue = maxValue;
        this.absoluteMaxValue = absoluteMaxValue;
        invalidate();
    }
}
