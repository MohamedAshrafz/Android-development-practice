package com.udacity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    companion object {
        const val RIGHT_TO_LEFT_RATIO = 0.65f
        const val TOP_TO_BOTTOM_RATIO = 0.5f
        const val TEXT_DOWN_SHIFTING = 15f
        const val DIAMETER_TO_HEIGHT_RATIO = 0.7f
        const val PADDING = 20f
    }

    private var pivotPointX: Float = 0f
    private var pivotPointY: Float = 0f
    private var radius: Float = 0f
    private var rectCircle = RectF(0f, 0f, 0f, 0f)
    private var rectBackgroundSpan = RectF(0f, 0f, 0f, 0f)

    private val paint = Paint().apply {
        style = Paint.Style.FILL

        textSize = 55.0f
        textAlign = Paint.Align.RIGHT
        typeface = Typeface.create("", Typeface.BOLD)
    }

    var progress: Float = 0f
        set(value) {
            field = if (value == 1.0f) {
                0f
            } else {
                value
            }
            angle = progress * 360f
            invalidate()
            requestLayout()
        }

    private var text: String = ""
        get() = when (progress) {
            0f -> "Download"
            else -> "We are loading"
        }
    private var angle: Float = 0.0f

    private var pieColor = 0
    private var backgroundInnerColor = 0
    private var textColor = 0

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            pieColor = getColor(R.styleable.LoadingButton_pieColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            backgroundInnerColor = getColor(R.styleable.LoadingButton_backgroundInnerColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        rectCircle.apply {
            left = 0f
            top = -radius
            right = radius * 2f
            bottom = +radius
        }
        rectBackgroundSpan.apply {
            right = progress * widthSize
            bottom = heightSize.toFloat()
        }

        paint.color = backgroundInnerColor

        canvas.drawRect(rectBackgroundSpan, paint)


        canvas.save()
        canvas.translate(pivotPointX, pivotPointY)

        paint.color = pieColor

        canvas.drawArc(rectCircle, 0f, angle, true, paint)

        paint.color = textColor

        canvas.drawText(text, -PADDING, TEXT_DOWN_SHIFTING, paint)

        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        pivotPointX = w * RIGHT_TO_LEFT_RATIO
        pivotPointY = h * TOP_TO_BOTTOM_RATIO
        radius = (h * DIAMETER_TO_HEIGHT_RATIO) / 2f

        setMeasuredDimension(w, h)
    }
}