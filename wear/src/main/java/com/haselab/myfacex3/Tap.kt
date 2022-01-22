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

private const val TAG = "MyFaceX3"

class Tap {

      fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long,applicationContext: Context ,bat: Int) {
          Log.v(TAG, "tap ")

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
                    "$bat%",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}
