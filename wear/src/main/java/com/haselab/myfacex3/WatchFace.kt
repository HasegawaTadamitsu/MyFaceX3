package com.haselab.myfacex3

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import androidx.palette.graphics.Palette
import java.util.*
import kotlin.math.cos
import kotlin.math.sin


private const val TAG = "MyFaceX3_WatchFace"
private const val HOUR_STROKE_WIDTH = 10f
private const val MINUTE_STROKE_WIDTH = 10f
private const val SECOND_TICK_STROKE_WIDTH = 10f
private const val SHADOW_RADIUS = 60f
private val DAY_OF_WEEK = arrayOf(
    "日", "月", "火", "水", "木", "金", "土"
)

private const val FONT_SIZE = 60f

class WatchFace(private val resources: Resources) {
    private lateinit var mBackgroundPaint: Paint
    private lateinit var mBackgroundBitmap: Bitmap
    private var mWatchHandColor: Int = 0
    private var mWatchHandPinColor: Int = 0
    private var mWatchHandHighlightColor: Int = 0
    private var mWatchHandShadowColor: Int = 0

    private lateinit var mHourPaint: Paint
    private lateinit var mMinutePaint: Paint
    private lateinit var mSecondPaint: Paint
    private lateinit var mTickAndCirclePaint: Paint
    private lateinit var mClockFontPaint: Paint

    /*
    Interactive mode (normal) 通常の描画、秒針などアニメーション付き
    ↓powerSaveMode = true
    Ambient mode      スリープ状態、1分に一度の描画更新
    Protect mode      Ambient mode 時に描画面積を最小に
    LowBit mode       Ambient mode 時に白黒 2階調
    */
    private var mPowerSaveMode: Boolean = false
    fun setPowerSaveMode() {
        Log.v(TAG, "start setPowerSaveMode")
        mPowerSaveMode = true
    }

    fun setPowerSaveNormal() {
        Log.v(TAG, "start setPowerSaveNormal")
        mPowerSaveMode = false
    }

    private var mCalendar: Calendar = Calendar.getInstance()
    fun setTimeZone(_arg: TimeZone) {
        mCalendar.timeZone = _arg
    }
    fun getCalendar(): Calendar {
        return mCalendar
    }
    fun updateNowTime() {
        mCalendar.timeInMillis = System.currentTimeMillis()
    }

    fun initializeBackground() {
        Log.v(TAG, "start initializeBackground")

        mBackgroundPaint = Paint().apply {
            color = Color.BLACK
        }
        mBackgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.bg)

        /* Extracts colors from background image to improve watchface style. */
        Palette.from(mBackgroundBitmap).generate {
            it?.let {
                mWatchHandHighlightColor = it.getVibrantColor(Color.RED)
                mWatchHandColor = it.getLightVibrantColor(Color.WHITE)
                mWatchHandPinColor = it.getLightVibrantColor(Color.GRAY)
                mWatchHandShadowColor = it.getDarkMutedColor(Color.BLACK)
                updateWatchHandStyle()
            }

        }
    }

    fun initializeWatchFace() {
        Log.v(TAG, "start initializeWatchFace")

        /* Set defaults for colors */
        mWatchHandColor = Color.WHITE
        mWatchHandPinColor = Color.GRAY

        mWatchHandHighlightColor = Color.RED
        mWatchHandShadowColor = Color.BLACK

        mHourPaint = Paint().apply {
            color = mWatchHandPinColor
            strokeWidth = 2.0f // HOUR_STROKE_WIDTH
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
        }

        mMinutePaint = Paint().apply {
            color = mWatchHandPinColor
            strokeWidth = 2.0f // MINUTE_STROKE_WIDTH
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
        }

        mSecondPaint = Paint().apply {
            color = mWatchHandHighlightColor
            strokeWidth = 2.0f // SECOND_TICK_STROKE_WIDTH
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
        }

        mTickAndCirclePaint = Paint().apply {
            color = mWatchHandPinColor
            strokeWidth = SECOND_TICK_STROKE_WIDTH
            isAntiAlias = true
            style = Paint.Style.STROKE
            setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
        }
        mClockFontPaint = Paint().apply {
            color = mWatchHandColor
            strokeWidth = MINUTE_STROKE_WIDTH
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
        }
        mClockFontPaint.textSize = FONT_SIZE
        mClockFontPaint.isAntiAlias = true
        mClockFontPaint.typeface = Typeface.DEFAULT_BOLD
        mClockFontPaint.textAlign = Paint.Align.CENTER
    }

    fun updateWatchHandStyle() {
        Log.v(TAG, "start updateWatchHandStyle")

        if (mPowerSaveMode) {
            mHourPaint.color = Color.GRAY
            mMinutePaint.color = Color.GRAY
            mSecondPaint.color = Color.GRAY
            mTickAndCirclePaint.color = Color.WHITE

            mHourPaint.isAntiAlias = false
            mMinutePaint.isAntiAlias = false
            mSecondPaint.isAntiAlias = false
            mTickAndCirclePaint.isAntiAlias = false

            mHourPaint.clearShadowLayer()
            mMinutePaint.clearShadowLayer()
            mSecondPaint.clearShadowLayer()
            mTickAndCirclePaint.clearShadowLayer()

        } else {
            mHourPaint.color = mWatchHandPinColor
            mMinutePaint.color = mWatchHandPinColor
            mSecondPaint.color = mWatchHandHighlightColor
            mTickAndCirclePaint.color = mWatchHandPinColor

            mHourPaint.isAntiAlias = true
            mMinutePaint.isAntiAlias = true
            mSecondPaint.isAntiAlias = true
            mTickAndCirclePaint.isAntiAlias = true

            mHourPaint.setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
            mMinutePaint.setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
            mSecondPaint.setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
            mTickAndCirclePaint.setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor
            )
        }
    }

    private var mCenterX: Float = 0F
    private var mCenterY: Float = 0F

    private var sSecondHandLength: Float = 0F
    private var sMinuteHandLength: Float = 0F
    private var sHourHandLength: Float = 0F

    fun surfaceChanged(width: Int, height: Int) {
        Log.v(TAG, "start surfaceChange")

        mCenterX = width / 2f
        mCenterY = height / 2f

        /*
         * Calculate lengths of different hands based on watch screen size.
         */
        sSecondHandLength = (mCenterX * 0.90).toFloat()
        sMinuteHandLength = (mCenterX * 0.85).toFloat()
        sHourHandLength = (mCenterX * 0.65).toFloat()

        /* Scale loaded background image (more efficient) if surface dimensions change. */
        val scale = width.toFloat() / mBackgroundBitmap.width.toFloat()

        mBackgroundBitmap = Bitmap.createScaledBitmap(
            mBackgroundBitmap,
            (mBackgroundBitmap.width * scale).toInt(),
            (mBackgroundBitmap.height * scale).toInt(), true
        )
    }


    private fun zeroPad(inp: String): String {
        val str = "0000$inp"
        val len = str.length
        return str.substring(len - 2, len)
    }

    fun drawBackground(canvas: Canvas) {
        Log.v(TAG, "start drawBackground")
        canvas.drawColor(Color.BLACK)
    }

    var mBatteryLevel: Int = 0
    fun setBatteryLevel(_arg: Int) {
        mBatteryLevel = _arg
    }

    fun drawWatchFace(canvas: Canvas) {
        Log.v(TAG, "start drawWatchFace")

        /*
         * Draw ticks. Usually you will want to bake this directly into the photo, but in
         * cases where you want to allow users to select their own photos, this dynamically
         * creates them on top of the photo.
         */
        val innerTickRadius = mCenterX - 10
        val outerTickRadius = mCenterX
        for (tickIndex in 0..11) {
            val tickRot = (tickIndex.toDouble() * Math.PI * 2.0 / 12).toFloat()
            val innerX = sin(tickRot.toDouble()).toFloat() * innerTickRadius
            val innerY = (-cos(tickRot.toDouble())).toFloat() * innerTickRadius
            val outerX = sin(tickRot.toDouble()).toFloat() * outerTickRadius
            val outerY = (-cos(tickRot.toDouble())).toFloat() * outerTickRadius
            canvas.drawLine(
                mCenterX + innerX, mCenterY + innerY,
                mCenterX + outerX, mCenterY + outerY, mTickAndCirclePaint
            )

            var ti = tickIndex
            if (tickIndex == 0) {
                ti = 12
            }
            if (ti == 12 || ti == 6) {
                mClockFontPaint.textAlign = Paint.Align.CENTER
            } else if (ti in 1..5) {
                mClockFontPaint.textAlign = Paint.Align.RIGHT
            } else if (ti in 7..11) {
                mClockFontPaint.textAlign = Paint.Align.LEFT
            }

            var adjustY = 0
            if (ti <= 2 || 10 <= ti) {
                adjustY = (FONT_SIZE * 2f / 3f).toInt()
            } else if (ti == 3 || ti == 9) {
                adjustY = (FONT_SIZE / 3f).toInt()
            }
            if (ti == 3 || ti == 6 || ti == 9 || ti == 12) {
                mClockFontPaint.textSize = FONT_SIZE
            } else {
                mClockFontPaint.textSize = FONT_SIZE * 2f / 3f
            }
            canvas.drawText(
                ti.toString(),
                mCenterX + (innerX) * 1.0f,
                mCenterY + (innerY) * 1.0f + adjustY, mClockFontPaint
            )
        }
        mClockFontPaint.textSize = FONT_SIZE

        /*
         * These calculations reflect the rotation in degrees per unit of time, e.g.,
         * 360 / 60 = 6 and 360 / 12 = 30.
         */
        val seconds =
            mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / 1000f
        val secondsRotation = seconds * 6f

        val minutesRotation = mCalendar.get(Calendar.MINUTE) * 6f

        val hourHandOffset = mCalendar.get(Calendar.MINUTE) / 2f
        val hoursRotation = mCalendar.get(Calendar.HOUR) * 30 + hourHandOffset

        /*
         * Save the canvas state before we can begin to rotate it.
         */
        canvas.save()

        /*
         * Ensure the "seconds" hand is drawn only when we are in interactive mode.
         * Otherwise, we only update the watch face once a minute.
         */

        // draw hour line
        canvas.rotate(hoursRotation, mCenterX, mCenterY)
        val ptsHour = floatArrayOf(
            mCenterX - HOUR_STROKE_WIDTH / 2.0f,
            mCenterY,
            mCenterX + HOUR_STROKE_WIDTH / 2.0f,
            mCenterY,

            mCenterX + HOUR_STROKE_WIDTH / 2.0f,
            mCenterY,
            mCenterX + HOUR_STROKE_WIDTH / 2.0f,
            mCenterY - sHourHandLength / 3.0f,

            mCenterX + HOUR_STROKE_WIDTH / 2.0f,
            mCenterY - sHourHandLength / 3.0f,
            mCenterX,
            mCenterY - sHourHandLength,

            mCenterX,
            mCenterY - sHourHandLength,
            mCenterX - HOUR_STROKE_WIDTH / 2.0f,
            mCenterY - sHourHandLength / 3.0f,

            mCenterX - HOUR_STROKE_WIDTH / 2.0f,
            mCenterY - sHourHandLength / 3.0f,
            mCenterX - HOUR_STROKE_WIDTH / 2.0f,
            mCenterY
        )

        canvas.drawLines(ptsHour, mHourPaint)
        canvas.rotate(-hoursRotation, mCenterX, mCenterY)

        // minus lines
        canvas.rotate(minutesRotation, mCenterX, mCenterY)
        val ptsMinute = floatArrayOf(
            mCenterX - MINUTE_STROKE_WIDTH / 2.0f,
            mCenterY,
            mCenterX + MINUTE_STROKE_WIDTH / 2.0f,
            mCenterY,

            mCenterX + MINUTE_STROKE_WIDTH / 2.0f,
            mCenterY,
            mCenterX + MINUTE_STROKE_WIDTH / 2.0f,
            mCenterY - sMinuteHandLength / 3.0f,

            mCenterX + MINUTE_STROKE_WIDTH / 2.0f,
            mCenterY - sMinuteHandLength / 3.0f,
            mCenterX,
            mCenterY - sMinuteHandLength,

            mCenterX,
            mCenterY - sMinuteHandLength,
            mCenterX - MINUTE_STROKE_WIDTH / 2.0f,
            mCenterY - sHourHandLength / 3.0f,

            mCenterX - MINUTE_STROKE_WIDTH / 2.0f,
            mCenterY - sHourHandLength / 3.0f,
            mCenterX - MINUTE_STROKE_WIDTH / 2.0f,
            mCenterY
        )
        canvas.drawLines(ptsMinute, mMinutePaint)
        canvas.rotate(-(minutesRotation), mCenterX, mCenterY)

        // seconds line
        if (!mPowerSaveMode) {
            canvas.rotate(secondsRotation, mCenterX, mCenterY)
            val ptsSecond = floatArrayOf(
                mCenterX - SECOND_TICK_STROKE_WIDTH / 2.0f,
                mCenterY,
                mCenterX + SECOND_TICK_STROKE_WIDTH / 2.0f,
                mCenterY,

                mCenterX + SECOND_TICK_STROKE_WIDTH / 2.0f,
                mCenterY,
                mCenterX + SECOND_TICK_STROKE_WIDTH / 2.0f,
                mCenterY - sSecondHandLength / 3.0f,

                mCenterX + SECOND_TICK_STROKE_WIDTH / 2.0f,
                mCenterY - sSecondHandLength / 3.0f,
                mCenterX,
                mCenterY - sSecondHandLength,

                mCenterX,
                mCenterY - sSecondHandLength,
                mCenterX - SECOND_TICK_STROKE_WIDTH / 2.0f,
                mCenterY - sSecondHandLength / 3.0f,

                mCenterX - SECOND_TICK_STROKE_WIDTH / 2.0f,
                mCenterY - sSecondHandLength / 3.0f,
                mCenterX - SECOND_TICK_STROKE_WIDTH / 2.0f,
                mCenterY
            )
            canvas.drawLines(ptsSecond, mSecondPaint)
            canvas.rotate(-secondsRotation, mCenterX, mCenterY)

        }

        // write date
        mClockFontPaint.textAlign = Paint.Align.CENTER
        mClockFontPaint.textSize = (FONT_SIZE * 1 / 2f * 1.3f)
        mClockFontPaint.setShadowLayer(
            SHADOW_RADIUS, 0f, 0f, Color.BLACK
        )


        val dateStr = mCalendar.get(Calendar.YEAR).toString() +
                "/" +
                zeroPad((mCalendar.get(Calendar.MONTH) + 1).toString()) +
                "/" +
                zeroPad(mCalendar.get(Calendar.DATE).toString()) +
                "(" +
                DAY_OF_WEEK[mCalendar.get(Calendar.DAY_OF_WEEK) - 1] +
                ")"
        canvas.drawText(
            dateStr,
            mCenterX,
            mCenterY - mClockFontPaint.textSize,
            mClockFontPaint
        )

        // draw now time
        mClockFontPaint.textSize = FONT_SIZE * 1.5f
        mClockFontPaint.setShadowLayer(
            FONT_SIZE * 1.5f,
            5f, 5f, Color.BLACK
        )

        // 24hour 表示にする
        val amPm = mCalendar.get(Calendar.AM_PM)
        val hour = mCalendar.get(Calendar.HOUR) + amPm * 12

        val clockStr = if (!mPowerSaveMode) {
            zeroPad(hour.toString()) +
                    ":" +
                    zeroPad((mCalendar.get(Calendar.MINUTE).toString())) +
                    ":" +
                    zeroPad((mCalendar.get(Calendar.SECOND).toString()))
        } else {
            zeroPad(hour.toString()) +
                    ":" +
                    zeroPad((mCalendar.get(Calendar.MINUTE).toString())) + "   "
        }
        canvas.drawText(
            clockStr, mCenterX, mCenterY + FONT_SIZE,
            mClockFontPaint
        )

        // battery
        mClockFontPaint.textSize = (FONT_SIZE * 1 / 2f)
        mClockFontPaint.setShadowLayer(FONT_SIZE, 5f, 5f, Color.BLACK)

        val batteryPercent = mBatteryLevel
        val oldColor = mClockFontPaint.color
        val batteryStr = "$batteryPercent%"
        if (batteryPercent <= 40) {
            mClockFontPaint.color = Color.RED
        } else {
            mClockFontPaint.color = Color.WHITE
        }
        canvas.drawText(
            batteryStr, mCenterX, mCenterY + FONT_SIZE * 2,
            mClockFontPaint
        )
        // restore
        mClockFontPaint.textSize = FONT_SIZE
        mClockFontPaint.color = oldColor

        /* Restore the canvas' original orientation. */
        canvas.restore()
    }

    fun getPowerSaveMode(): Boolean {
        return mPowerSaveMode
    }

}
