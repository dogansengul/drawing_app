package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet): View(context, attrs) {
    private lateinit var canvas: Canvas
    private lateinit var canvasBitmap: Bitmap
    private lateinit var drawPaint: Paint
    private lateinit var canvasPaint: Paint
    private var brushSize: Float = 0f
    private var brushColor = Color.BLACK
    private lateinit var drawPath: CustomPath
    internal inner class CustomPath(brushColor: Int, brushSize: Float) : Path() {

    }
     init {
         setUpDrawing()
     }

    private fun setUpDrawing() {
        canvas = Canvas()
        canvasPaint = Paint()
        drawPaint = Paint()
        drawPaint.apply {
            color = brushColor
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        drawPath = CustomPath(brushColor, brushSize)
        brushSize = 20f
    }
}