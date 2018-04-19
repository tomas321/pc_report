package com.mtaa.tomas.pcreport

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.DisplayMetrics
import android.widget.Button
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlin.math.roundToInt

class DeleteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        window.setLayout((width * 0.7).roundToInt(), (height * 0.2).roundToInt())
        window.setBackgroundDrawableResource(android.R.color.transparent) // android.graphics.Color.TRANSPARENT

        val mRequestQueue = Volley.newRequestQueue(this) // 'this' is Context

        val itemID = intent.getIntExtra("id", -1) // default no delete with ID == -1
        val deleteButton = findViewById<Button>(R.id.delete_button)
        val cancelDelete = findViewById<Button>(R.id.delete_cancel_button)

        deleteButton.setOnClickListener { v ->
            val requestObject = sendDelete(Constants.host_ip, itemID)
            mRequestQueue.add(requestObject)
        }

        cancelDelete.setOnClickListener { v ->
            setResult(Constants.DELETE_RESULT_FAIL)
            finish()
        }
    }

    fun sendDelete(url: String, id: Int): JsonObjectRequest {
        val json = JsonObjectRequest(Request.Method.DELETE, "$url/reports/$id", null,
                Response.Listener { response ->
                    setResult(Constants.DELETE_RESULT_OK)
                    finish()
                },
                Response.ErrorListener { error ->
                    if (error is NoConnectionError || error is TimeoutError) {
                        setResult(Constants.OFFLINE)
                        val intent = Intent(this, ConnectionErrorActivity::class.java)
                        startActivity(intent)
                        println("DEBUG: opening error popup")
                        finish() // close pop up after connection error pop up closes
                    } else if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 404) {
                            setResult(Constants.DELETE_RESULT_404)
                            finish()
                        }
                    }
                })
        return json
    }
}
