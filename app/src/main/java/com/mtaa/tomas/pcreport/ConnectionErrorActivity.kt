package com.mtaa.tomas.pcreport

import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Button
import kotlin.math.roundToInt

class ConnectionErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection_error)

        val okButton = findViewById<Button>(R.id.error_ok_button)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.7).roundToInt(), (height * 0.2).roundToInt())
        window.setBackgroundDrawableResource(android.R.color.transparent) // android.graphics.Color.TRANSPARENT

        okButton.setOnClickListener {
            finish()
        }
    }
}
