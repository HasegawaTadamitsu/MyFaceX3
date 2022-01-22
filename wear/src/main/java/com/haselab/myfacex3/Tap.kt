package com.haselab.myfacex3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.os.*
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.palette.graphics.Palette
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.cos
import kotlin.math.sin


class Tap {
    /**
     * Captures tap event (and tap type). The [WatchFaceService.TAP_TYPE_TAP] case can be
     * used for implementing specific logic to handle the gesture.
     */
    Tap(tapType,x,y,eventTime,mBatteryLevel)

    fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long,bat: batteryl) {
        when (tapType) {
            WatchFaceService.TAP_TYPE_TOUCH -> {
                // The user has started touching the screen.
            }
            WatchFaceService.TAP_TYPE_TOUCH_CANCEL -> {
                // The user has started a different gesture or otherwise cancelled the tap.
            }
            WatchFaceService.TAP_TYPE_TAP -> {
                // The user has completed the tap gesture.
                Toast.makeText(
                    applicationContext,
                    "$mBatteryLevel%",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}
