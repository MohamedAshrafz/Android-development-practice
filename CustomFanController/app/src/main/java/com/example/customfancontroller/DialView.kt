package com.example.customfancontroller

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// the int label holds the id of the given fan speed string
private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    fun next() = when (this) {
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }
}

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -30
private const val INDICATOR_RADIUS_MULTIPLIER = (1 / 11f)

class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var radius = 0.0f
    private val paintPosition = PointF(0.0f, 0.0f)


    private var fanSpeedEnum = FanSpeed.OFF

    var fanSpeed: Int = 0
        get() = fanSpeedEnum.ordinal
        set(value) {
            field = value
            fanSpeedEnum = when (field) {
                1 -> FanSpeed.LOW
                2 -> FanSpeed.MEDIUM
                3 -> FanSpeed.HIGH
                else -> FanSpeed.OFF
            }
            invalidate()
        }


    private var fanSpeedLowColor = 0
    private var fanSpeedMediumColor = 0
    private var fanSeedMaxColor = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 55.0f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.DialView) {
            fanSpeedLowColor = getColor(R.styleable.DialView_fanColor1, 0)
            fanSpeedMediumColor = getColor(R.styleable.DialView_fanColor2, 0)
            fanSeedMaxColor = getColor(R.styleable.DialView_fanColor3, 0)
        }
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        fanSpeedEnum = fanSpeedEnum.next()
        contentDescription = resources.getString(fanSpeedEnum.label)

        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // / 2.0 for getting radius from the diameter (diameter = min(w, h))
        // 0.8 to make it less smaller than the full radius
        radius = ((min(w, h) / 2.0) * 0.8).toFloat()
    }

    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        val startAngle = Math.PI * (9.0 / 8.0)
        // divide the pi (pi is half a circle) to 4 equal divisions
        // then get the enum value position in that half
        val angle = startAngle + (pos.ordinal * (Math.PI / 4))

        // getting the x-component of the calculated angle
        // make sure the x is centered using (width / 2)
        x = (radius * cos(angle)).toFloat() + width / 2
        // getting the y-component of the calculated angle
        // make sure the y is centered using (height / 2)
        y = (radius * sin(angle)).toFloat() + height / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // setting the color depending on the fan speed
        paint.color = when (fanSpeedEnum) {
            FanSpeed.OFF -> Color.GRAY
            FanSpeed.LOW -> fanSpeedLowColor
            FanSpeed.MEDIUM -> fanSpeedMediumColor
            FanSpeed.HIGH -> fanSeedMaxColor
        }

        // drawing the dial
        // canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
        val pivotX = (width / 2).toFloat()
        val pivotY = (height / 2).toFloat()
        canvas.drawArc(
            pivotX - radius,
            pivotY - radius,
            pivotX + radius,
            pivotY + radius,
            0f,
            350f,
            true,
            paint
        )

        // drawing the indicator
        var markerRadius = radius + RADIUS_OFFSET_INDICATOR
        // make sure to use the same paintPosition and paint to ovoid allocation
        paintPosition.computeXYForSpeed(fanSpeedEnum, markerRadius)
        paint.color = Color.WHITE


        drawIndicator(canvas)
//        canvas.drawCircle(
//            paintPosition.x,
//            paintPosition.y,
//            radius * INDICATOR_RADIUS_MULTIPLIER,
//            paint
//        )

        // drawing the label of the speeds from 0 to 3
        paint.color = Color.BLACK
        for (i in FanSpeed.values()) {
            markerRadius = radius + RADIUS_OFFSET_LABEL
            paintPosition.computeXYForSpeed(i, markerRadius)
            paint.color = Color.BLACK
            canvas.drawText(resources.getString(i.label), paintPosition.x, paintPosition.y, paint)
        }

    }

    private inner class CustomRect {
        val rectangle = Rect()

        fun set(width: Int = 20, height: Int = 150, viewWidth: Int, viewHeight: Int) {

            // then center it around the width
            val widthShift = viewWidth / 2 - width / 2

            val heightShift = viewHeight / 2 + 70

            rectangle.apply {
                bottom = heightShift
                top = heightShift + height
                left = widthShift
                right = widthShift + width
            }
        }
    }

    private val myRect = CustomRect()

    private fun drawIndicator(canvas: Canvas) {
        // slightShift
        val slightAngleShift = 1f

        // start angle
        val startAngle = (180f * 0.625f)

        val speed = fanSpeedEnum.ordinal
        val shiftingAngle = Math.toDegrees((Math.PI / 4) * speed).toFloat() + startAngle

        canvas.rotate(
            shiftingAngle + slightAngleShift,
            (width / 2).toFloat(),
            (height / 2).toFloat()
        )

        myRect.set(viewWidth = width, viewHeight = height)
        canvas.drawRect(myRect.rectangle, paint)

        canvas.rotate(
            -(shiftingAngle + slightAngleShift),
            (width / 2).toFloat(),
            (height / 2).toFloat()
        )
    }
}