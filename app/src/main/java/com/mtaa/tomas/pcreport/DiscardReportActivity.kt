package com.mtaa.tomas.pcreport

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import kotlin.math.roundToInt

class DiscardReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discard_report)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        window.setLayout((width * 0.7).roundToInt(), (height * 0.2).roundToInt())
        window.setBackgroundDrawableResource(android.R.color.transparent) // android.graphics.Color.TRANSPARENT

        val noButton = findViewById<Button>(R.id.discard_no_button)
        val yesButton = findViewById<Button>(R.id.discard_yes_button)

        noButton.setOnClickListener {v ->
            setResult(Constants.DISCARD_NO)
            finish()
        }
        yesButton.setOnClickListener { v ->
            setResult(Constants.DISCARD_YES)
            finish()
        }
    }
}
