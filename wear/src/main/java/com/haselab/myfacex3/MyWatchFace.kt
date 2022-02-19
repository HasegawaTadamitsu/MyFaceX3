package com.haselab.myfacex3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Rect
import android.os.*
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.util.Log
import android.view.SurfaceHolder
import com.haselab.myfacex3.vibration.VibrationMgr
import java.lang.ref.WeakReference
import java.util.*

/**
 * Updates rate in milliseconds for interactive mode. We update once a second to advance the
 * second hand.
 */
private const val INTERACTIVE_UPDATE_RATE_MS = 1000

private const val TAG = "MyFaceX3"

/**
 * Handler message id for updating the time periodically in interactive mode.
 */
private const val MSG_UPDATE_TIME = 0


/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 *
 *
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
class MyWatchFace : CanvasWatchFaceService() {

    override fun onCreateEngine(): Engine {
        Log.v(TAG, "start onCreateEngine")
        val vib = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        VibrationMgr.setVibrator(vib)
        return Engine()
    }

    private class EngineHandler(reference: MyWatchFace.Engine) : Handler(Looper.getMainLooper()) {
        private val mWeakReference: WeakReference<MyWatchFace.Engine> = WeakReference(reference)

        override fun handleMessage(msg: Message) {
            Log.v(TAG, "start handleMessage")

            val engine = mWeakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
                }
            }
        }
    }

    inner class Engine : CanvasWatchFaceService.Engine() {

        private var mRegisteredBatteryReceiver = false
        private var mRegisteredTimeZoneReceiver = false

        /* Handler to update the time once a second in interactive mode. */
        private val mUpdateTimeHandler = EngineHandler(this)

        private val mTimeZoneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.v(TAG, "start ZoneReceiveOnReceive")
                mWatchFace.setTimeZone(TimeZone.getDefault())
                invalidate()
            }
        }
        private val mBatteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.v(TAG, "start BatteryOnReceiver")
                if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                    mWatchFace.setBatteryLevel(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1))
                }
                invalidate()
            }
        }
        private lateinit var mWatchFace: WatchFace
        override fun onCreate(holder: SurfaceHolder) {
            Log.v(TAG, "start onCreate")
            super.onCreate(holder)
            setWatchFaceStyle(
                WatchFaceStyle.Builder(this@MyWatchFace)
                    .setAcceptsTapEvents(true)
                    .build()
            )
            mWatchFace = WatchFace(resources)
            mWatchFace.initializeBackground()
            mWatchFace.initializeWatchFace()
        }

        override fun onDestroy() {
            Log.v(TAG, "start onDestroy")
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }

        override fun onPropertiesChanged(properties: Bundle) {
            Log.v(TAG, "start onPropertiesChanged")
            super.onPropertiesChanged(properties)
            val lowBit = properties.getBoolean(
                WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false
            )
            val burnProtect = properties.getBoolean(
                WatchFaceService.PROPERTY_BURN_IN_PROTECTION, false
            )
            if (lowBit || burnProtect) {
                mWatchFace.setPowerSaveMode()
            } else {
                mWatchFace.setPowerSaveNormal()
            }
        }

        override fun onTimeTick() {
            Log.v(TAG, "start onTimeTick")
            super.onTimeTick()
            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            Log.v(TAG, "start onAmbientModeChanged")
            super.onAmbientModeChanged(inAmbientMode)

            if (inAmbientMode) {
                mWatchFace.setPowerSaveMode()
            } else {
                mWatchFace.setPowerSaveNormal()
            }
            mWatchFace.updateWatchHandStyle()

            // Check and trigger whether or not timer should be running (only
            // in active mode).
            updateTimer()
        }

        override fun onInterruptionFilterChanged(interruptionFilter: Int) {
            Log.v(TAG, "start onInterruptionFilterChanged")

            super.onInterruptionFilterChanged(interruptionFilter)
            // @TODO
            // val inMuteMode = interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE
            // nothing
            /* Dim display in mute mode.
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode
                mHourPaint.alpha = if (inMuteMode) 100 else 255
                mMinutePaint.alpha = if (inMuteMode) 100 else 255
                mSecondPaint.alpha = if (inMuteMode) 80 else 255
                mClockFontPaint.alpha = if (inMuteMode) 80 else 255
                invalidate()
            }
            */
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.v(TAG, "start onSurfaceChanged")

            super.onSurfaceChanged(holder, format, width, height)
            mWatchFace.surfaceChanged(width, height)
        }

        /**
         * Captures tap event (and tap type). The [WatchFaceService.TAP_TYPE_TAP] case can be
         * used for implementing specific logic to handle the gesture.
         */
        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            Log.v(TAG, "start onTapCommand")
            Tap().onTapCommand(
                tapType, x, y, eventTime, applicationContext,
                mWatchFace.mBatteryLevel
            )
            invalidate()
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            Log.v(TAG, "start onDraw")
            hourSignal()
            mWatchFace.updateNowTime()
            mWatchFace.drawBackground(canvas)
            mWatchFace.drawWatchFace(canvas)
        }

        private fun hourSignal() {
            Log.v(TAG, "start hourSignal")
            val cal = mWatchFace.getCalendar()
            val min = cal.get(Calendar.MINUTE)
            val hour24 = cal.get(Calendar.HOUR_OF_DAY)
            val sec = cal.get(Calendar.SECOND)

            if ((hour24 in 7..19) &&    // 7〜19時
                (sec in 0..30 )&&       // 00to10 sec
                (min == 55 || min == 0 )      // 55,00
            ) {
                VibrationMgr.single()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            Log.v(TAG, "start onVisibilityChanged")
            super.onVisibilityChanged(visible)

            if (visible) {
                tzRegisterReceiver()
                bRegisterReceiver()
                /* Update time zone in case it changed while we weren't visible. */
                mWatchFace.setTimeZone(TimeZone.getDefault())
                invalidate()
            } else {
                unRegisterReceiver()
            }
            updateTimer()
        }

        private fun tzRegisterReceiver() {
            Log.v(TAG, "start tzRegisterReceiver")
            if (mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = true
            val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            this@MyWatchFace.registerReceiver(mTimeZoneReceiver, filter)
        }

        private fun bRegisterReceiver() {
            Log.v(TAG, "start bRegisterReceiver")
            if (mRegisteredBatteryReceiver) {
                return
            }
            mRegisteredBatteryReceiver = true
            val intentFilter = IntentFilter()
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
            this@MyWatchFace.registerReceiver(mBatteryReceiver, intentFilter)
        }

        private fun unRegisterReceiver() {
            Log.v(TAG, "start unRegisterReceiver")
            if (!mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = false
            this@MyWatchFace.unregisterReceiver(mTimeZoneReceiver)
        }

        /**
         * Starts/stops the [.mUpdateTimeHandler] timer based on the state of the watch face.
         */
        private fun updateTimer() {
            Log.v(TAG, "start updateTimer")
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (!mWatchFace.getPowerSaveMode()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        fun handleUpdateTimeMessage() {
            Log.v(TAG, "start handleUpdateTimeMessage")
            invalidate()
            if (!mWatchFace.getPowerSaveMode()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }
    }
}
