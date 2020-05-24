package com.psyclops

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.View
import java.lang.Boolean

//https://stackoverflow.com/questions/44749473/draw-rectangle-over-camera2-preview-android?noredirect=1&lq=1
class Rectangle : View {
    var paint = Paint()
    var mPaintBlack = Paint()
    var mPaintYellow = Paint()
    private var mSquareLength: Int
    private var mRepeatingThread: RepeatingThread?
    private var trainingCanvas = false

    constructor(
        context: Context?,
        squareLength: Int,
        repeatingThread: RepeatingThread?
    ) : super(context) {
        mSquareLength = squareLength
        mRepeatingThread = repeatingThread
    }

    constructor(context: Context?, squareLength: Int) : super(context) {
        mSquareLength = squareLength
        mRepeatingThread = null

        mPaintBlack.style = Paint.Style.FILL
        mPaintBlack.color = Color.BLACK
        mPaintBlack.textSize = 25f

        mPaintYellow.style = Paint.Style.FILL
        mPaintYellow.color = Color.YELLOW
        mPaintYellow.textSize = 25f

        trainingCanvas = false
    }

    public override fun onDraw(canvas: Canvas) {
        val width = this.width
        val height = this.height
        var color = IntArray(3)
        color[0] = 255
        color[1] = 255
        color[2] = 0
        if (trainingCanvas) {
            val trainingRect = Rect(
                0,
                0,
                width,
                height
            )
            canvas.drawRect(trainingRect, mPaintBlack)
        }
        if (mRepeatingThread != null) {
            color = mRepeatingThread!!.getAvgPixel()!!
        }
        //paint.setColor(Color.GREEN);
        paint.color = Color.rgb(color[0], color[1], color[2])
        // paint.setStyle(Paint.Style.FILL);
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 15f
        val rect = Rect(
            (width - mSquareLength) / 2,
            (height - mSquareLength) / 2,
            (width + mSquareLength) / 2,
            (height + mSquareLength) / 2
        )
        canvas.drawRect(rect, paint)
        val rectII = Rect(
            (width - mSquareLength) / 2,
            0,  //(height - mSquareLength),
            (width + mSquareLength) / 2,
            mSquareLength
        ) //(height + mSquareLength));
        val croppedBmp: Bitmap? = mRepeatingThread!!.getCroppedBitMap(mSquareLength)
        Log.d("BitmapCrop", Boolean.toString(croppedBmp == null))
        if (croppedBmp != null) {
            canvas.drawBitmap(
                croppedBmp,
                Rect(0, 0, croppedBmp.width, croppedBmp.height),
                rectII,
                null
            )
        }

        val blackPaintThickness = 2f
        val leftMarginText = 10f
        val yRgbText = canvas.height - 60f //1500f
        val yHsvText = canvas.height - 30f // 1530f

        // RGB text
        val averagePixelRGBStr =
            "RGB: " + String.format("%3d", color[0]) + ", " + String.format(
                "%3d",
                color[1]
            ) + ", " + String.format("%3d", color[2])

        canvas.drawText(averagePixelRGBStr, leftMarginText - blackPaintThickness, yRgbText - blackPaintThickness, mPaintBlack);
        canvas.drawText(averagePixelRGBStr, leftMarginText + blackPaintThickness, yRgbText - blackPaintThickness, mPaintBlack);
        canvas.drawText(averagePixelRGBStr, leftMarginText + blackPaintThickness, yRgbText + blackPaintThickness, mPaintBlack);
        canvas.drawText(averagePixelRGBStr, leftMarginText - blackPaintThickness, yRgbText + blackPaintThickness, mPaintBlack);
        canvas.drawText(averagePixelRGBStr, leftMarginText, yRgbText, mPaintYellow)
        // HSV text
        val averagePixelHSV: FloatArray = mRepeatingThread!!.calculateAverage()
        //
        val imageHSVRGBStr = "HSV: " + String.format(
            "%.3g",
            averagePixelHSV[0]
        ) + ", " + String.format(
            "%.2g",
            averagePixelHSV[1]
        ) + ", " + String.format("%.2g", averagePixelHSV[2])
        canvas.drawText(imageHSVRGBStr, leftMarginText - blackPaintThickness, yHsvText - blackPaintThickness, mPaintBlack);
        canvas.drawText(imageHSVRGBStr, leftMarginText + blackPaintThickness, yHsvText - blackPaintThickness, mPaintBlack);
        canvas.drawText(imageHSVRGBStr, leftMarginText + blackPaintThickness, yHsvText + blackPaintThickness, mPaintBlack);
        canvas.drawText(imageHSVRGBStr, leftMarginText - blackPaintThickness, yHsvText + blackPaintThickness, mPaintBlack);
        canvas.drawText(imageHSVRGBStr, leftMarginText, yHsvText, mPaintYellow)
        invalidate()
    }

    fun setRThread(repeatingThread: RepeatingThread?) {
        mRepeatingThread = repeatingThread
    }

    fun toogleTraining() {
        trainingCanvas = !trainingCanvas
    }
}