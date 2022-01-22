package com.haselab.myfacex3

import android.content.Context
import android.os.VibrationEffect
import androidx.core.content.ContextCompat.getSystemService

class Vibrator {
    private var mLastVibrateLong: Long = 0 // last vibrate milli sec
    private val mEffectSingle by lazy {
        VibrationEffect.createOneShot(
            200, VibrationEffect.DEFAULT_AMPLITUDE
        )
    }
 //   private val mVibrator by lazy {
 //       getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
 //   }

    fun vibrateSingle() {
        val now = System.currentTimeMillis()
        mLastVibrateLong = now
 //       mVibrator.vibrate(mEffectSingle)
    }

    fun vibrate(effect: VibrationEffect?) {

    }
}
