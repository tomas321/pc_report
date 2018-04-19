package com.mtaa.tomas.pcreport

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlin.math.roundToInt

class IncompleteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incomplete)

        val okButton = findViewById<Button>(R.id.incomplete_ok)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.7).roundToInt(), (height * 0.2).roundToInt())
        window.setBackgroundDrawableResource(android.R.color.transparent) // android.graphics.Color.TRANSPARENT

        if (intent.getBooleanExtra("dateFormat", false)) {
            findViewById<TextView>(R.id.incomplete_error_msg).text = "Incomplete fields and/or wrong date format"
        } else if (intent.getBooleanExtra("number", false)) {
            findViewById<TextView>(R.id.incomplete_error_msg).text = "bad number format or too large"
        }

        okButton.setOnClickListener {
            finish()
        }
    }
}
