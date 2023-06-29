package com.example.drawingapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.drawingapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawingView: DrawingView
    private lateinit var showBrushSizeDialog: Dialog
    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            it ->
            it.entries.forEach {
                val permission = it.key
                val isGranted = it.value
                if(isGranted){
                    openGallery()
                } else {
                    Toast.makeText(applicationContext, "$permission is not granted.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if(result.resultCode == RESULT_OK && result.data != null){
                binding.imageView.setImageURI(result.data?.data)
            }
        }
    private fun openGallery() {
        //Snackbar.make(drawingView, "Gallery opened", Snackbar.LENGTH_SHORT).show()
        val pickGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryLauncher.launch(pickGalleryIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawingView = binding.drawingView

        //color pallet buttons click events
        binding.black.setOnClickListener {
            drawingView.setColor(ContextCompat.getColor(this, R.color.black))
            binding.black.setImageResource(R.drawable.pallet_pressed)
            binding.blue.setImageResource(R.drawable.pallet_normal)
            binding.yellow.setImageResource(R.drawable.pallet_normal)
            binding.purple.setImageResource(R.drawable.pallet_normal)
            binding.green.setImageResource(R.drawable.pallet_normal)
            binding.red.setImageResource(R.drawable.pallet_normal)
            binding.orange.setImageResource(R.drawable.pallet_normal)
        }
        binding.blue.setOnClickListener {
            drawingView.setColor(ContextCompat.getColor(this, R.color.blue))
            binding.blue.setImageResource(R.drawable.pallet_pressed)

            binding.black.setImageResource(R.drawable.pallet_normal)
            binding.yellow.setImageResource(R.drawable.pallet_normal)
            binding.purple.setImageResource(R.drawable.pallet_normal)
            binding.green.setImageResource(R.drawable.pallet_normal)
            binding.red.setImageResource(R.drawable.pallet_normal)
            binding.orange.setImageResource(R.drawable.pallet_normal)
        }
        binding.yellow.setOnClickListener {
            drawingView.setColor(ContextCompat.getColor(this, R.color.yellow))
            binding.yellow.setImageResource(R.drawable.pallet_pressed)

            binding.black.setImageResource(R.drawable.pallet_normal)
            binding.blue.setImageResource(R.drawable.pallet_normal)
            binding.purple.setImageResource(R.drawable.pallet_normal)
            binding.green.setImageResource(R.drawable.pallet_normal)
            binding.red.setImageResource(R.drawable.pallet_normal)
            binding.orange.setImageResource(R.drawable.pallet_normal)
        }
        binding.purple.setOnClickListener {
            drawingView.setColor(ContextCompat.getColor(this, R.color.purple))
            binding.purple.setImageResource(R.drawable.pallet_pressed)

            binding.black.setImageResource(R.drawable.pallet_normal)
            binding.blue.setImageResource(R.drawable.pallet_normal)
            binding.yellow.setImageResource(R.drawable.pallet_normal)
            binding.green.setImageResource(R.drawable.pallet_normal)
            binding.red.setImageResource(R.drawable.pallet_normal)
            binding.orange.setImageResource(R.drawable.pallet_normal)
        }
        binding.green.setOnClickListener {
            drawingView.setColor(ContextCompat.getColor(this, R.color.green))
            binding.green.setImageResource(R.drawable.pallet_pressed)

            binding.black.setImageResource(R.drawable.pallet_normal)
            binding.blue.setImageResource(R.drawable.pallet_normal)
            binding.yellow.setImageResource(R.drawable.pallet_normal)
            binding.purple.setImageResource(R.drawable.pallet_normal)
            binding.red.setImageResource(R.drawable.pallet_normal)
            binding.orange.setImageResource(R.drawable.pallet_normal)
        }
        binding.red.setOnClickListener {
            drawingView.setColor(ContextCompat.getColor(this, R.color.red))
            binding.red.setImageResource(R.drawable.pallet_pressed)

            binding.black.setImageResource(R.drawable.pallet_normal)
            binding.blue.setImageResource(R.drawable.pallet_normal)
            binding.yellow.setImageResource(R.drawable.pallet_normal)
            binding.purple.setImageResource(R.drawable.pallet_normal)
            binding.green.setImageResource(R.drawable.pallet_normal)
            binding.orange.setImageResource(R.drawable.pallet_normal)
        }
        binding.orange.setOnClickListener {
            drawingView.setColor(ContextCompat.getColor(this, R.color.orange))
            binding.orange.setImageResource(R.drawable.pallet_pressed)

            binding.black.setImageResource(R.drawable.pallet_normal)
            binding.blue.setImageResource(R.drawable.pallet_normal)
            binding.yellow.setImageResource(R.drawable.pallet_normal)
            binding.purple.setImageResource(R.drawable.pallet_normal)
            binding.green.setImageResource(R.drawable.pallet_normal)
            binding.red.setImageResource(R.drawable.pallet_normal)
        }

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
        binding.galleryButton.setOnClickListener {
            if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.S_V2){
                requestPermissionAndOpenGallery(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                requestPermissionAndOpenGallery(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun requestPermissionAndOpenGallery(permission: String) {
        if(shouldShowRequestPermissionRationale(permission)){
            val rationaleDialog = AlertDialog.Builder(this)
                .setTitle("Permission Request")
                .setMessage("Drawing app needs storage access permission to open gallery.")
                .setPositiveButton("Okay", DialogInterface.OnClickListener { dialog, which -> requestPermission.launch(
                    arrayOf(permission)
                ) }).show()
        } else {
            requestPermission.launch(arrayOf(permission))
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
