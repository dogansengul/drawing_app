package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.icu.text.ListFormatter.Width
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private lateinit var drawingCanvas: Canvas
    private lateinit var canvasBitmap: Bitmap
    private var paint: Paint = Paint(Paint.DITHER_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 20f
    }
    private var path: Path = Path()

    private fun setUpDrawing(w: Int, h: Int) {
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawingCanvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap, 0f, 0f, paint)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                drawingCanvas.drawPath(path, paint)
                path.reset()
            }
        }
        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            setUpDrawing(w, h)
        }
    }

    fun clearDrawing() {
        path.reset()
        drawingCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
        invalidate()
    }
}

