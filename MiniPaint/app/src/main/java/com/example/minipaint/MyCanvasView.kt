package com.example.minipaint

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

private const val STROKE_WIDTH = 12f
private const val INSET = 20

class MyCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var extraBitmap: Bitmap
    private lateinit var extraCanvas: Canvas

    private val backgroundColor =
        ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val paintColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private val paint = Paint().apply {
        color = paintColor
        isAntiAlias = true

        isDither = true

        style = Paint.Style.STROKE

        strokeJoin = Paint.Join.ROUND  // default: MITER
        strokeCap = Paint.Cap.ROUND   // default: BUTT
        strokeWidth = STROKE_WIDTH   // default: Hairline-width (really thin)
    }

    private val path = Path()

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private lateinit var frame: Rect

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var viewOldWidth: Int = 0
    private var viewOldHeight: Int = 0

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int) {
        super.onSizeChanged(width, height, oldwidth, oldheight)

        viewWidth = width
        viewHeight = height
        viewOldWidth = oldwidth
        viewOldHeight = oldheight

        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)

        extraCanvas.drawColor(backgroundColor)
        frame = Rect(
            INSET,
            INSET,
            width - INSET,
            height - INSET
        )
        extraCanvas.drawRect(frame, paint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
        // canvas.drawRect(frame, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchStart()
            MotionEvent.ACTION_MOVE -> onTouchMove()
            MotionEvent.ACTION_UP -> onTouchUp()
        }
        return true
    }

    private fun onTouchStart() {
        //path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun onTouchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        //if (dx >= touchTolerance || dy >= touchTolerance) {
        path.quadTo(
            currentX,
            currentY,
            (motionTouchEventX + currentX) / 2,
            (motionTouchEventY + currentY) / 2
        )
        currentX = motionTouchEventX
        currentY = motionTouchEventY
        extraCanvas.drawPath(path, paint)
        //}
        invalidate()
    }

    private fun onTouchUp() {
        path.reset()
    }

    fun clearScreen() {

        onSizeChanged(viewWidth, viewHeight, viewOldWidth, viewOldHeight)
        invalidate()
        requestLayout()
    }

}