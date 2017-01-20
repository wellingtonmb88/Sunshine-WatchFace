package com.example.android.sunshine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SimpleWatchFace {

    private static final String TAG = SimpleWatchFace.class.getSimpleName();
    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";

    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    private final Paint timePaint;
    private final Paint datePaint;
    private final String dateText;
    private Calendar mCalendar;

    public static SimpleWatchFace newInstance(Context context) {
        Paint timePaint = new Paint();
        timePaint.setColor(Color.WHITE);
        timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        timePaint.setTypeface(NORMAL_TYPEFACE);
        timePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        timePaint.setAntiAlias(true);

        Paint datePaint = new Paint();
        datePaint.setColor(ContextCompat.getColor(context, R.color.white_alpha_99));
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        datePaint.setTypeface(NORMAL_TYPEFACE);
        datePaint.setAntiAlias(true);

        return new SimpleWatchFace(timePaint, datePaint);
    }

    private SimpleWatchFace(Paint timePaint, Paint datePaint) {
        this.timePaint = timePaint;
        this.datePaint = datePaint;
        mCalendar = Calendar.getInstance();
        dateText = formattedDateFromString();
    }

    public void draw(Canvas canvas, Rect bounds) {
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        String timeText = String.format(Locale.getDefault(),
                TIME_FORMAT_WITHOUT_SECONDS,
                mCalendar.get(Calendar.HOUR),
                mCalendar.get(Calendar.MINUTE));

        float timeXOffset = computeXOffset(timeText, timePaint, bounds);
        float timeYOffset = computeTimeYOffset(timeText, timePaint, bounds);
        canvas.drawText(timeText, timeXOffset, timeYOffset, timePaint);

        float dateXOffset = computeXOffset(dateText, datePaint, bounds);
        float dateYOffset = computeDateYOffset(dateText, datePaint);
        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, datePaint);

        canvas.drawLine(bounds.exactCenterX() - 60f,
                timeYOffset + dateYOffset + 30f,
                bounds.exactCenterX() + 60f,
                timeYOffset + dateYOffset + 30f, datePaint);
    }

    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY() - 100f;
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    private float computeDateYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 20.0f;
    }

    private String formattedDateFromString() {
        final Date date = new Date();

        final SimpleDateFormat dateFormat =
                new SimpleDateFormat("EE',' MMM d yyyy", Locale.getDefault());
        try {
            return dateFormat.format(date).toUpperCase();
        } catch (Exception e) {
            Log.e(TAG, "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return "";
    }

    public void setAntiAlias(boolean antiAlias) {
        timePaint.setAntiAlias(antiAlias);
        datePaint.setAntiAlias(antiAlias);
    }

    public void setColor(int color) {
        timePaint.setColor(color);
        datePaint.setColor(color);
    }
}
