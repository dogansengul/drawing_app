package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.icu.text.ListFormatter.Width
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.util.*

// This class is a View class that allows the user to draw on the screen by touching it.
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // The Canvas object that we use to draw (its purpose is to draw on the bitmap object that it will receive)
    private lateinit var drawingCanvas: Canvas
    // The Bitmap object that the canvas uses
    private lateinit var canvasBitmap: Bitmap
    // The Path object that connects the points that the user touches
    private var path: Path = Path()
    // The Stack object that we use to undo the drawings that the user made
    private val undoStack: Stack<Path> = Stack()
    // The Paint object that we use to draw
    private var paint: Paint = Paint(Paint.DITHER_FLAG).apply {
        color = Color.BLACK // Color black
        style = Paint.Style.STROKE // Style line
        strokeJoin = Paint.Join.ROUND // Line joins round
        strokeCap = Paint.Cap.ROUND // Line ends round
        strokeWidth = 20f // Line thickness 20 pixels
        isAntiAlias = true // Edge smoothing enabled
    }


    // The function that makes the necessary settings for the class to run at the beginning
    private fun setUpDrawing(w: Int, h: Int) {
        // Creates a bitmap according to the width and height it receives as a parameter
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        // Assigns the created bitmap to the canvas
        drawingCanvas = Canvas(canvasBitmap)
    }

    // The function that draws the View on the screen
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draws the bitmap and path on the canvas
        canvas.drawBitmap(canvasBitmap, 0f, 0f, paint)
        canvas.drawPath(path, paint)

    }

    // The function that handles the user's touch events on the screen
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x // The x coordinate of the touched point
        val y = event.y // The y coordinate of the touched point

        when (event.action) { // Performs an operation according to the type of touch event
            MotionEvent.ACTION_DOWN -> { // Runs when first touched on the screen
                path = Path() // Creates a new path
                path.moveTo(x, y) // Assigns the touched point as the starting point

            }
            MotionEvent.ACTION_MOVE -> { // Runs when moving your finger on the screen
                path.lineTo(x, y) // Adds the touched point to the path and updates the path
            }
            MotionEvent.ACTION_UP -> { // Runs when lifting your finger off the screen
                drawingCanvas.drawPath(path, paint) // Draws the path on the canvas
                undoStack.push(Path(path)) // Adds the path to the undo stack
                path.reset() // Resets the path
            }
        }
        invalidate() // Calls the function that requests the View to be redrawn. This function triggers the onDraw function.
        return true
    }


    // The function that runs when the size of the View changes
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) { // If the new sizes are greater than zero
            setUpDrawing(w, h) // Calls the setting function
        }
    }

    // The function that undoes the last drawing made by the user
    fun undo() {
        if (undoStack.isNotEmpty()) { // If the undo stack is not empty
            undoStack.pop() // Removes the last added path
            redrawDrawing() // Redraws the drawing
        }
    }

    // Helper function that draws all paths in undo stack on canvas
    private fun redrawDrawing() {
        // Clears all drawing
        drawingCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)

        // Draws paths in undo stack
        for (path in undoStack) {
            drawingCanvas.drawPath(path, paint)
        }

        invalidate()// Calls the function that requests View to be redrawn. This function triggers onDraw function.
    }


    // The function that clears all drawing and clears undo stack
    fun clearDrawing() {
        path.reset()// Resets path
        drawingCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)// Clears all drawing
        undoStack.clear()// Clears undo stack
        Toast.makeText(context, "Canvas is cleared.", Toast.LENGTH_SHORT).show()// Informs user
        invalidate()// Calls function that requests View to be redrawn. This function triggers onDraw function.
    }

}


