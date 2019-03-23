package com.example.android.camera2basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
//https://stackoverflow.com/questions/44749473/draw-rectangle-over-camera2-preview-android?noredirect=1&lq=1

import android.util.Log;
import android.view.View;

public class Rectangle extends View {
    Paint paint = new Paint();
    private int mSquareLength;
    private RepeatingThread mRepeatingThread;

    public Rectangle(Context context, int squareLength, RepeatingThread repeatingThread) {
        super(context);
        mSquareLength = squareLength;
        mRepeatingThread = repeatingThread;
    }

    public Rectangle(Context context, int squareLength) {
        super(context);
        mSquareLength = squareLength;
        mRepeatingThread = null;
    }


    @Override
    public void onDraw(Canvas canvas) {
        int width = this.getWidth();
        int height = this.getHeight();
        int[] color = new int[3];

        color[0] = 255;
        color[1] = 255;
        color[2] = 0;

        if(mRepeatingThread != null) {
            color = mRepeatingThread.getAvgPixel();
        }
        //paint.setColor(Color.GREEN);
        paint.setColor(Color.rgb(color[0], color[1], color[2]));
        // paint.setStyle(Paint.Style.FILL);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth (15);
        Rect rect = new Rect((width- mSquareLength)/2,
                (height - mSquareLength)/2,
                (width + mSquareLength)/2,
                (height + mSquareLength)/2);
        canvas.drawRect(rect, paint );


        Rect rectII = new Rect((width- mSquareLength)/2,
                0, //(height - mSquareLength),
                (width + mSquareLength)/2,
                 mSquareLength); //(height + mSquareLength));
        Bitmap croppedBmp = mRepeatingThread.getCroppedBitMap(mSquareLength);
        Log.d("BitmapCrop", Boolean.toString(croppedBmp == null));
        if(croppedBmp != null) {
            canvas.drawBitmap(croppedBmp, new Rect(0, 0, croppedBmp.getWidth(), croppedBmp.getHeight()), rectII, null);
        }
        invalidate();
    }

    public void setRThread(RepeatingThread repeatingThread){
        mRepeatingThread = repeatingThread;
    }
}