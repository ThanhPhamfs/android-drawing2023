package com.example.drawing2023

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private var drawing_view: DrawingView? = null
    private var btnBrush: ImageButton? = null
    private var btnGallery: ImageButton? = null
    private var mImageButtonCurrentPaint: ImageButton? = null
    val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null){
                val imageBackground: ImageView = findViewById(R.id.iv_background)
                imageBackground.setImageURI(result.data?.data)
            }
        }

    val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permisionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    Toast.makeText(this, "$permisionName was allowed!", Toast.LENGTH_SHORT).show()
                    val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)
                } else {
                    if (permisionName == android.Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "$permisionName was not allowed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setControl()
        drawing_view?.setSizeForBrush(8f)
        setEvent()
    }

    private fun setControl() {
        drawing_view = findViewById(R.id.drawing_view)
        btnBrush = findViewById(R.id.btnBrush)
        btnGallery = findViewById(R.id.btnGallery)
//        Select red color for brush
        val linearLayoutColors = findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = linearLayoutColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.pallet_pressed
            )
        )
    }

    private fun setEvent() {
        btnBrush?.setOnClickListener { showBrushSizeChooserDialog() }
        btnGallery?.setOnClickListener { requestStoragePermission() }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showRationaleDialog(
                "Kids Drawing App",
                "Kids Drawing App needs to access Your External Storage"
            )
        } else {
            requestPermission.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    //    Click to select brush colors
    fun paintClicked(view: View) {
        if (view != mImageButtonCurrentPaint) {
            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.pallet_normal
                )
            )
            mImageButtonCurrentPaint = view as ImageButton
            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.pallet_pressed
                )
            )
            drawing_view?.setColorForBrush(view.tag.toString())
        }
    }

    private fun showBrushSizeChooserDialog() {
        var brushSizeDialog = Dialog(this)
        brushSizeDialog.setContentView(R.layout.dialog_brush_size)
        brushSizeDialog.setTitle("Brush size: ")
        val smallBtn: ImageButton = brushSizeDialog.findViewById(R.id.btnSmallBrush)
        smallBtn.setOnClickListener {
            drawing_view?.setSizeForBrush(8f)
            brushSizeDialog.dismiss()
        }
        val mediumBtn: ImageButton = brushSizeDialog.findViewById(R.id.btnMediumBrush)
        mediumBtn.setOnClickListener {
            drawing_view?.setSizeForBrush(16f)
            brushSizeDialog.dismiss()
        }
        val largeBtn: ImageButton = brushSizeDialog.findViewById(R.id.btnLargeBrush)
        largeBtn.setOnClickListener {
            drawing_view?.setSizeForBrush(24f)
            brushSizeDialog.dismiss()
        }
        brushSizeDialog.show()
    }

    private fun showRationaleDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}