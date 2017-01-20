package com.example.android.sunshine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

public class SimpleWeatherFace {

    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    private final Paint maxTempPaint;
    private final Paint minTempPaint;
    private String maxTemp = "40˚";
    private String minTemp = "10˚";
    private Bitmap weatherBitmap;
    private Bitmap weatherBitmapColor;

    public static SimpleWeatherFace newInstance(Context context) {
        Paint maxTempPaint = new Paint();
        maxTempPaint.setColor(Color.WHITE);
        maxTempPaint.setTextSize(context.getResources().getDimension(R.dimen.max_temp_size));
        maxTempPaint.setTypeface(NORMAL_TYPEFACE);
        maxTempPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        maxTempPaint.setAntiAlias(true);

        Paint minTempPaint = new Paint();
        minTempPaint.setColor(ContextCompat.getColor(context, R.color.white_alpha_99));
        minTempPaint.setTextSize(context.getResources().getDimension(R.dimen.min_temp_size));
        minTempPaint.setTypeface(NORMAL_TYPEFACE);
        minTempPaint.setAntiAlias(true);

        return new SimpleWeatherFace(context, maxTempPaint, minTempPaint);
    }

    private SimpleWeatherFace(Context context, Paint maxTempPaint, Paint minTempPaint) {
        this.maxTempPaint = maxTempPaint;
        this.minTempPaint = minTempPaint;

        weatherBitmapColor = getBitmap(context, R.mipmap.ic_launcher);
        weatherBitmap = weatherBitmapColor;
    }

    private float computeImageXOffset(Bitmap bitmap, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        return centerX - (bitmap.getWidth() / 2.0f) - 100f;
    }

    private float computeImageYOffset(Bitmap bitmap, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        return centerY + (bitmap.getHeight() / 2.0f);
    }

    private float computeTextXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    private float computeTextBoundsHeight(String text, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(text, 0, text.length(), textBounds);
        return textBounds.height();
    }

    private float computeTextBoundsWidth(String text, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(text, 0, text.length(), textBounds);
        return textBounds.width();
    }

    private Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    private Bitmap convertBitmapToMonochrome(Bitmap bmpSrc) {

        Bitmap bmpMonochrome = Bitmap.createBitmap(bmpSrc.getWidth(), bmpSrc.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        ColorMatrix ma = new ColorMatrix();
        ma.setSaturation(0);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(ma));
        canvas.drawBitmap(bmpSrc, 0, 0, paint);
        return bmpMonochrome;
    }

    public void draw(Canvas canvas, Rect bounds) {

        final float imageXOffset = computeImageXOffset(weatherBitmap, bounds);
        final float imageYOffset = computeImageYOffset(weatherBitmap, bounds);

        canvas.drawBitmap(weatherBitmap, imageXOffset, imageYOffset, maxTempPaint);

        final float centerText = imageYOffset + weatherBitmap.getHeight() / 2;

        final float maxTempXOffset = computeTextXOffset(maxTemp, maxTempPaint, bounds);
        final float maxTempBoundsHeight = computeTextBoundsHeight(maxTemp, maxTempPaint);

        canvas.drawText(maxTemp, maxTempXOffset, centerText + maxTempBoundsHeight / 2, maxTempPaint);

        final float maxTempBoundsWidth = computeTextBoundsWidth(maxTemp, maxTempPaint);
        final float minTempXOffset = computeTextXOffset(minTemp, minTempPaint, bounds);
        final float minTempBoundsHeight = computeTextBoundsHeight(minTemp, minTempPaint);

        canvas.drawText(minTemp, minTempXOffset + maxTempBoundsWidth + 10f, centerText + minTempBoundsHeight / 2, minTempPaint);
    }

    public void setWeatherBitmap(Bitmap bitmap) {
        this.weatherBitmapColor = bitmap;
        this.weatherBitmap = weatherBitmapColor;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public void setAntiAlias(boolean antiAlias) {
        maxTempPaint.setAntiAlias(antiAlias);
        minTempPaint.setAntiAlias(antiAlias);
    }

    public void setColor(int color) {
        maxTempPaint.setColor(color);
        minTempPaint.setColor(color);
    }

    public void setWeatherIconInAmbientMode(boolean isInAmbientMode) {
        if (isInAmbientMode) {
            weatherBitmap = convertBitmapToMonochrome(weatherBitmapColor);
        } else {
            weatherBitmap = weatherBitmapColor;
        }
    }
}
