package com.haselab.myfacex3

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService

private const val TAG = "MyFaceX3_VibrationMgr"


class VibrationMgr(val vb: Vibrator) {
    private var mLastVibrateLong: Long = 0 // last vibrate milli sec
    private val mEffectSingle by lazy {
        VibrationEffect.createOneShot(
            200, VibrationEffect.DEFAULT_AMPLITUDE
        )
    }

    public fun single() {
        Log.v(TAG, "vibratorsingle ")

        val now = System.currentTimeMillis()
        mLastVibrateLong = now
        vb.vibrate(mEffectSingle)
    }

}
