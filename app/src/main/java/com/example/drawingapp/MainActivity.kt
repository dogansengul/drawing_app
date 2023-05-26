package com.example.drawingapp

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.example.drawingapp.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawingView: DrawingView
    private lateinit var showBrushSizeDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawingView = binding.drawingView

        binding.black.setOnClickListener { drawingView.setColor(ContextCompat.getColor(this, R.color.black)) ; binding.black.setImageResource(R.drawable.pallet_pressed)}
        binding.blue.setOnClickListener { drawingView.setColor(ContextCompat.getColor(this, R.color.blue)) ; binding.blue.setImageResource(R.drawable.pallet_pressed)}
        binding.yellow.setOnClickListener { drawingView.setColor(ContextCompat.getColor(this, R.color.yellow)) ; binding.yellow.setImageResource(R.drawable.pallet_pressed)}
        binding.purple.setOnClickListener { drawingView.setColor(ContextCompat.getColor(this, R.color.purple)) ; binding.purple.setImageResource(R.drawable.pallet_pressed)}
        binding.green.setOnClickListener { drawingView.setColor(ContextCompat.getColor(this, R.color.green)) ; binding.green.setImageResource(R.drawable.pallet_pressed)}
        binding.red.setOnClickListener { drawingView.setColor(ContextCompat.getColor(this, R.color.red)); binding.red.setImageResource(R.drawable.pallet_pressed)}
        binding.orange.setOnClickListener { drawingView.setColor(ContextCompat.getColor(this, R.color.red)); binding.orange.setImageResource(R.drawable.pallet_pressed)}


        //buttons' methods
        binding.clearButton.setOnClickListener {
            drawingView.undo()
        }
        binding.clearButton.setOnLongClickListener {
            // Uzun basma işlemleri
            drawingView.clearDrawing()
            // true dönerek uzun basmanın diğer olayları engellenir
            true
        }
        binding.saveButton.setOnClickListener {
            saveDrawing()
        }
        binding.brushSizeButton.setOnClickListener {
            showBrushSizeChooserDialog()
        }
    }

    private fun showBrushSizeChooserDialog() {
        showBrushSizeDialog = Dialog(this)
        showBrushSizeDialog.setContentView(R.layout.brush_size_dialog)
        showBrushSizeDialog.show()
        val smallSizeButton: ImageButton = showBrushSizeDialog.findViewById(R.id.imageButton)
        smallSizeButton.setOnClickListener {
            drawingView.setStrokeWidth(5f)
            showBrushSizeDialog.dismiss()
        }
        val mediumSizeButton: ImageButton = showBrushSizeDialog.findViewById(R.id.imageButton2)
        mediumSizeButton.setOnClickListener {
            drawingView.setStrokeWidth(13f)
            showBrushSizeDialog.dismiss()
        }
        val largeButtonSize: ImageButton = showBrushSizeDialog.findViewById(R.id.imageButton3)
        largeButtonSize.setOnClickListener {
            drawingView.setStrokeWidth(30f)
            showBrushSizeDialog.dismiss()
        }
    }

    private fun saveDrawing() {
        val bitmap = Bitmap.createBitmap(drawingView.width, drawingView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawingView.draw(canvas)

        val fileName = "drawing_${System.currentTimeMillis()}.png"
        val directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(directory, fileName)

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(this, "Çizim kaydedildi: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Çizim kaydedilirken hata oluştu", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

}
