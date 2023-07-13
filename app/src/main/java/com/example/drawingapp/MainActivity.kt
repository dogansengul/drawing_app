package com.example.drawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.drawingapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Float.min

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawingView: DrawingView
    private lateinit var showBrushSizeDialog: Dialog
    private var progressDialog: Dialog? = null
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
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedImageUri = result.data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                val resizedBitmap = resizeBitmap(bitmap, drawingView.getWidthAndHeight()[0], drawingView.getWidthAndHeight()[1])
                binding.imageView.setImageBitmap(resizedBitmap)
            }
        }
    private fun openGallery() {
        //Snackbar.make(drawingView, "Gallery opened", Snackbar.LENGTH_SHORT).show()
        val pickGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryLauncher.launch(pickGalleryIntent)
    }
    private fun isReadStorageAllowed(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
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
            lifecycleScope.launch{
                if(isReadStorageAllowed()){
                    showProgressDialog()
                    saveBitmapFile(getBitmapFromView(findViewById(R.id.frame_layout)))
                }
            }
        }
        binding.brushSizeButton.setOnClickListener {
            showBrushSizeChooserDialog()
        }
        binding.galleryButton.setOnClickListener {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2){
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
    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val background = view.background
        if(background != null) {
            background.draw(canvas)
        } else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }
    private suspend fun saveBitmapFile(bitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if(bitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val file = File(
                        externalCacheDir?.absoluteFile.toString()
                                + File.separator + "DrawingApp_"
                                + System.currentTimeMillis() / 1000 + ".png"
                    )

                    val fo = FileOutputStream(file)
                    fo.write(bytes.toByteArray())
                    fo.close()

                    result = file.absolutePath
                    runOnUiThread {
                        cancelDialog()
                        if (result.isNotEmpty()) {
                            Toast.makeText(
                                applicationContext,
                                "File saved succesfully. $result",
                                Toast.LENGTH_SHORT
                            ).show()
                            shareImage(result)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    private fun showProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog?.setContentView(R.layout.progress_dialog)
        progressDialog?.show()
    }
    private fun cancelDialog() {
        if(progressDialog != null) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
    }
    private fun shareImage(filePath: String) {
        // Create an intent to share the image.
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"

        // Get the Uri of the image to share.
        val fileUri = FileProvider.getUriForFile(
            applicationContext,
            "com.example.drawingapp.fileprovider",
            File(filePath)
        )
        intent.putExtra(Intent.EXTRA_STREAM, fileUri)

        // Grant permission to the receiving app to read the shared file
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Show a chooser dialog so the user can select which app to share with.
        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaleFactor = min(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val scaledWidth = (width * scaleFactor).toInt()
        val scaledHeight = (height * scaleFactor).toInt()

        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
    }
}
