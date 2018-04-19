package com.mtaa.tomas.pcreport

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONObject

class DetailActivity : AppCompatActivity() {
    lateinit var mRequestQueue: RequestQueue
    lateinit var reportDetail: Report

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val reportStr = intent.getStringExtra("response_json")
        val reportJson = JSONObject(reportStr)

        reportDetail = Report.fromJson(reportJson)
        setComponents(reportDetail)

        mRequestQueue = Volley.newRequestQueue(this) // 'this' is Context

        edit_report_button.setOnClickListener { view ->
            checkConnection()
        }

        val screen: ImageView = findViewById(R.id.detail_screen)
        screen.setOnClickListener { view ->
            val imageIntent = Intent(this, PreviewActivity::class.java)
            imageIntent.putExtra("path", reportDetail.screen)
            startActivity(imageIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.EDIT_REQUEST -> {
                if (resultCode == Constants.EDIT_OK) {
                    reportDetail = Report.fromJson(JSONObject(data!!.getStringExtra("report")))
                    setComponents(reportDetail)
                    setResult(Constants.EDIT_OK)

                } else if (resultCode == Constants.EDIT_CANCEL) {

                } else if (resultCode == Constants.EDIT_404) {
                    setResult(Constants.EDIT_404)
                    finish()
                }
            }
        }
    }

    fun checkConnection() {
        println("\t\t\tDEBUG: clicked")
        val json = JsonArrayRequest(Request.Method.GET, Constants.host_ip, null,
                Response.Listener { response ->

                },
                Response.ErrorListener { error ->
                    if (error is TimeoutError || error is NoConnectionError) {
                        val intent = Intent(this, ConnectionErrorActivity::class.java)
                        startActivity(intent)
                    } else if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 404) {
                            val builder = AlertDialog.Builder(this)
                            builder.setMessage("Item not found.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                                        setResult(Constants.EDIT_404)
                                        dialog.cancel()
                                        finish()
                                    })
                            val alert = builder.create()
                            alert.show()
                        }
                    } else {
                        val editIntent = Intent(this, EditActivity::class.java)
                        val currentReportStr = reportDetail.reportToJson()
                        editIntent.putExtra("report", currentReportStr.toString())
                        startActivityForResult(editIntent, Constants.EDIT_REQUEST)
                    }
                    println("ERROR: " + error.printStackTrace())
                })
        mRequestQueue.add(json)
    }

    fun setComponents(report: Report) {
        findViewById<TextView>(R.id.detail_report).text = report.report
        findViewById<TextView>(R.id.detail_os).text = report.operatingSystem
        findViewById<TextView>(R.id.detail_memory).text = report.memory.toString()
        findViewById<TextView>(R.id.detail_uptime).text = report.uptime.toString()
        findViewById<TextView>(R.id.detail_owner).text = report.owner
        findViewById<TextView>(R.id.detail_department).text = report.department
        findViewById<TextView>(R.id.detail_occurredAt).text = report.occurredAt
        findViewById<TextView>(R.id.detail_specification).text = report.specification
        val bitmapImage = BitmapFactory.decodeFile(report.screen)
        val detailImage = findViewById<ImageView>(R.id.detail_screen)
        if (bitmapImage.height > 2048 || bitmapImage.width > 2048) {
            val scale = (bitmapImage.height * (2048.0 / bitmapImage.width)).toInt()
            val bitmapImageScaled = Bitmap.createScaledBitmap(bitmapImage, 2048, scale, true)
            detailImage.setImageBitmap(bitmapImageScaled)
        } else {
            detailImage.setImageBitmap(bitmapImage)
        }



        val stateBlock = findViewById<LinearLayout>(R.id.state_block_layout)
        val icon = findViewById<ImageView>(R.id.detail_state_icon)
        val stateText = findViewById<TextView>(R.id.detail_state_text)
        when (report.state) {
            1 -> {
                icon.setImageResource(R.drawable.ic_warning_black_24dp)
                stateText.text = "requires imidiate attention"
                stateBlock.setBackgroundColor(Constants.critical_color.toInt())
            }
            2 -> {
                icon.setImageResource(R.drawable.ic_build_black_24dp)
                stateText.text = "requires repair"
                stateBlock.setBackgroundColor(Constants.problem_color.toInt())
            }
            3 -> {
                icon.setImageResource(R.drawable.ic_check_black_24dp)
                stateText.text = "report resolved"
                stateBlock.setBackgroundColor(Constants.resolved_color.toInt())
            }

        }
    }

}
