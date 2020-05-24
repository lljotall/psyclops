package com.psyclops

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.media.ThumbnailUtils
import android.os.Handler
import android.util.Log


// ----------------------------------------------------------------------
class RepeatingThread(
    private var mBitmap: Bitmap?,
    private val mAudio: AudioWave,
    private val mSquareLength: Int,
    private val mAct: Activity
) :
    Runnable {
    private val mHandler = Handler()
    private val mAveragePixelRGB = IntArray(3)
    private val mAveragePixelHSV = FloatArray(3)

    override fun run() {
        mHandler.postDelayed(this, mAudio.mDuration.toLong())
        if (mBitmap == null) return
        val avgPixel = calculateAverage()
        Log.d("FazendoMedia", java.lang.Float.toString(avgPixel[0]) + "H")
        Log.d("FazendoMedia", java.lang.Float.toString(avgPixel[2]) + "V")
        //   float[] avgPixel = new float[3];
//   avgPixel[0] = 100;
//   avgPixel[2] = (float) .5;
        mAudio.playSound(
            15 * avgPixel[0] + 900.toDouble(),  // range from .9 to 3.6 kHz
            10 + 20 * avgPixel[2].toDouble()
        )
        Log.d("audioLength:", java.lang.Double.toString(mAudio.mDuration))
    }

    fun calculateAverage(): FloatArray {
        calculateAverageRGB()
        Color.RGBToHSV(
            mAveragePixelRGB[0],
            mAveragePixelRGB[1],
            mAveragePixelRGB[2],
            mAveragePixelHSV
        )
        return mAveragePixelHSV
    }

    fun calculateAverageRGB(): IntArray? {
        if (mBitmap == null) return mAveragePixelRGB
        // http://answers.opencv.org/question/61628/android-camera2-yuv-to-rgb-conversion-turns-out-green/, from shyam kumar
// mAveragePixelRGB[0] = 0;
// mAveragePixelRGB[1] = 255;
// mAveragePixelRGB[2] = 255;
        Log.d("Bitmappp", mBitmap.toString())
        try { // Bitmap croppedBmp = ThumbnailUtils.extractThumbnail(mBitmap, mSquareLength, mSquareLength);
// Log.d("Bitmapppppp", croppedBmp.toString());
// mSquareLength commands
// int[] pixels = new int[mSquareLength * mSquareLength];
// croppedBmp.getPixels(pixels, 0, mSquareLength, 0, 0, mSquareLength, mSquareLength);
// Using our variables
/*            int[] pixels = new int[croppedBmp.getWidth() * croppedBmp.getHeight()];
            croppedBmp.getPixels(pixels, 0,
                    croppedBmp.getWidth(), 0, 0,
                    croppedBmp.getWidth(), croppedBmp.getHeight());

            int cumSum = 0;
            for (int px = 0; px < mSquareLength * mSquareLength; px++) {
                cumSum += pixels[px];
            }
            cumSum /= mSquareLength * mSquareLength;

            mAveragePixelRGB[0] = ((cumSum >> 16) & 0xff )+ 255;
            mAveragePixelRGB[1] = (cumSum >> 8) & 0xff;
            mAveragePixelRGB[2] = (cumSum) & 0xff;
*/
            val pixel =
                mBitmap!!.getPixel(mBitmap!!.width / 2, mBitmap!!.height / 2)
                // mBitmap!!.getPixel(mBitmap!!.width / 2 + 60, mBitmap!!.height / 2) - 10
            val correctionFactor = 1.05
            mAveragePixelRGB[0] =
                Math.round((pixel shr 16 and 0xff).toFloat()) //*correctionFactor) % 255; // theres a bug on dark environments that shows up as high values for blue
            mAveragePixelRGB[1] =
                Math.round((pixel shr 8 and 0xff) * correctionFactor).toInt() % 255 // should check the white balance of the image
            mAveragePixelRGB[2] =
                Math.round((pixel and 0xff) * correctionFactor).toInt() % 255

            /*Log.d("rgb pixel:", "");
            Log.d("r pixel:", Integer.toString(mAveragePixelRGB[0]));
            Log.d("g pixel:", Integer.toString(mAveragePixelRGB[1]));
            Log.d("b pixel:", Integer.toString(mAveragePixelRGB[2]));
*/
//Color.RGBToHSV(averagePixelRGB[0], averagePixelRGB[1], averagePixelRGB[2], averagePixelHSV);
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } finally {
        }
        return mAveragePixelRGB
    }

    fun setBitmap(bitmap: Bitmap?) {
        mBitmap = bitmap
    }

    fun getAvgPixel(): IntArray? { // int[] fakeColor = new int[3];
/*mAveragePixelRGB[0] = 0;
        mAveragePixelRGB[1] = 255;
        mAveragePixelRGB[2] = 0;*/
// return fakeColor;
        return calculateAverageRGB()
        // return mAveragePixelRGB;
    }

    fun getCroppedBitMap(size: Int): Bitmap? {
        var size = size
        if (mBitmap == null) return null
        if (size == 0) size = mSquareLength
        return ThumbnailUtils.extractThumbnail(mBitmap, size, size)
    }
}
