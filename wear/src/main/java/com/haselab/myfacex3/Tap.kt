package com.haselab.myfacex3

import android.content.Context
import android.support.wearable.watchface.WatchFaceService
import android.util.Log
import android.widget.Toast
import com.haselab.myfacex3.vibration.VibrationMgr

private const val TAG = "MyFaceX3_TAP"

class Tap {

      fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long,applicationContext: Context ,bat: Int) {
          Log.v(TAG, "on tap $tapType $x $y")

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
		      //       VibrationMgr.single()
              }
          }
    }
}
