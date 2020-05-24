package com.psyclops

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log


// ----------------------------------------------------------------------
class AudioWave @JvmOverloads constructor(
    //    private double carrierFrequency = 1500; //15*meanH + 900; // range from .9 to 3.6 kHz
//    private double modulatorFrequency = 20; //10 + meanV/12;
    var mDuration //100; //s
    : Double = 100.0,
    //Hz
    private val mSampFreq: Int = 44100,
    private val mModulatorExp: Short = 10.toShort()
) {
    private val nSamples: Int = (mDuration * mSampFreq / 1000).toInt()
    private val mAudioTrack: AudioTrack?
    fun playSound(carrierFrequency: Double, modulatorFrequency: Double) {
        Log.d("nSampless:", Integer.toString(nSamples))
        // Sine wave
        val mSoundSine = DoubleArray(nSamples)
        val mSoundSawTooth = DoubleArray(nSamples)
        val mBuffer = ShortArray(nSamples)
        for (t in 0 until nSamples) {
            mSoundSine[t] =
                Math.sin(2.0 * Math.PI * (carrierFrequency / mSampFreq) * t)
            mSoundSawTooth[t] =
                Math.round(modulatorFrequency / mSampFreq * t).toShort() - modulatorFrequency / mSampFreq * t + .5
            // A sine wave enveloped by a "polynomial sawtooth"
            mBuffer[t] = (mSoundSine[t] * Math.pow(
                mSoundSawTooth[t],
                mModulatorExp.toDouble()
            ) * Short.MAX_VALUE).toShort()
        }
        mAudioTrack!!.write(mBuffer, 0, nSamples)
        //mAudioTrack.stop();
//mAudioTrack.release();
    }

    fun resumeAudioTrack() {
        mAudioTrack?.play()
    }

    fun pauseAudioTrack() {
        mAudioTrack?.stop()
    }

    fun destroyAudioTrack() {
        mAudioTrack?.release()
    }

    init {
        // milliseconds
        Log.d("nSamples:", Integer.toString(nSamples))
        // AudioTrack definition
        mAudioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            mSampFreq,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,  //                mBufferSize, AudioTrack.MODE_STREAM);
            nSamples,
            AudioTrack.MODE_STREAM
        )
        // mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume())
        mAudioTrack.play()
    }
}

// ----------------------------------------------------------------------
