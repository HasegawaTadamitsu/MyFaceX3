package com.haselab.myfacex3.vibration

import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log

private const val TAG = "MyFaceX3_VibrationMgr"

object VibrationMgr {
    private var mLastVibrateLong: Long = 0 // last vibrate milli sec
    private val mEffectSingle by lazy {
        VibrationEffect.createOneShot(
            200, VibrationEffect.DEFAULT_AMPLITUDE
        )
    }
    private var vb: Vibrator? = null
    fun setVibrator(_vb: Vibrator) {
        vb = _vb
    }

    fun single() {
        Log.v(TAG, "vibrator_single ")

        val now = System.currentTimeMillis()
        mLastVibrateLong = now
        if ( vb != null && vb is Vibrator)  {
            vb!!.vibrate(mEffectSingle)
        }
    }
}
