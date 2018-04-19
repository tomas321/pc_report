package com.mtaa.tomas.pcreport

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import com.github.chrisbanes.photoview.PhotoView

class PreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        val imagePath = intent.getStringExtra("path")
        val previewImage = BitmapFactory.decodeFile(imagePath)
        val imagePreview = findViewById<PhotoView>(R.id.image_preview)
        if (previewImage.height > 2048 || previewImage.width > 2048) {
            val scale = (previewImage.height * (2048.0 / previewImage.width)).toInt()
            val bitmapImageScaled = Bitmap.createScaledBitmap(previewImage, 2048, scale, true)
            imagePreview.setImageBitmap(bitmapImageScaled)
        } else {
            imagePreview.setImageBitmap(previewImage)
        }
    }
}
