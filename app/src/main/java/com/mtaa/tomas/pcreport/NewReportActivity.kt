package com.mtaa.tomas.pcreport

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import kotlinx.android.synthetic.main.activity_new_report.*
import org.json.JSONObject
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class NewReportActivity : AppCompatActivity() {
    var newState: Int = -1
    lateinit var mRequestQueue: RequestQueue
    var imagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report)
        setSupportActionBar(new_report_toolbar)

        val specificationEditText = findViewById<EditText>(R.id.new_specification)
        specificationEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
        specificationEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)

        mRequestQueue = Volley.newRequestQueue(this)

        val criticalIcon = findViewById<ImageView>(R.id.critical_choose_image)
        val criticalRadioButton = findViewById<RadioButton>(R.id.critical_choose_radio)
        val problemIcon = findViewById<ImageView>(R.id.problem_choose_image)
        val problemRadioButton = findViewById<RadioButton>(R.id.problem_choose_radio)
        criticalIcon.setOnClickListener { v ->
            criticalRadioButton.callOnClick()
        }
        problemIcon.setOnClickListener { v ->
            problemRadioButton.callOnClick()
        }
        criticalRadioButton.setOnClickListener { v ->
            newState = 1
            criticalRadioButton.isChecked = true
            problemRadioButton.isChecked = false
        }
        problemRadioButton.setOnClickListener { v ->
            newState = 2
            criticalRadioButton.isChecked = false
            problemRadioButton.isChecked = true
        }
        val addButton = findViewById<Button>(R.id.new_report_button)
        addButton.setOnClickListener { v ->
            try {
                addNewReport()
            } catch (ie: IncompleteException) {
                val incompleteIntent = Intent(this, IncompleteActivity::class.java)
                startActivity(incompleteIntent)
            } catch (nfe: NumberFormatException) {
                val incompleteIntent = Intent(this, IncompleteActivity::class.java)
                incompleteIntent.putExtra("number", true)
                startActivity(incompleteIntent)
            }
        }
        val capturePhoto = findViewById<ImageView>(R.id.new_photo_capture)
        val choosePhoto = findViewById<ImageView>(R.id.new_photo_library)
        capturePhoto.setOnClickListener { v ->
            ImagePicker.cameraOnly()
                    .start(this, Constants.CAMERA_REQUEST)
        }
        choosePhoto.setOnClickListener { v ->
            ImagePicker.create(this)
                    .limit(1)
                    .showCamera(false)
                    .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.DISCARD_REQUEST -> {
                if (resultCode == Constants.DISCARD_YES) {
                    setResult(Constants.NEW_REPORT_FAIL)
                    finish()
                } else if (resultCode == Constants.DISCARD_NO) {
                    // do nothong and stay on new report page
                }
                return
            }
            Constants.CAMERA_REQUEST -> {
                if (data == null) return
                val image = ImagePicker.getFirstImageOrNull(data)
                imagePath = image.path
                findViewById<TextView>(R.id.new_screen_path).text = image.name
                return
            }
        }
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image = ImagePicker.getFirstImageOrNull(data)
            imagePath = image.path
            findViewById<TextView>(R.id.new_screen_path).text = image.name
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun addNewReport() {
        val report = isStringEmpty(findViewById<EditText>(R.id.new_report_title).text.toString())
        val specification = isStringEmpty(findViewById<EditText>(R.id.new_specification).text.toString())
        val operatingSystem = isStringEmpty(findViewById<EditText>(R.id.new_operating_system).text.toString())
        val memory = parseInt(isStringEmpty(findViewById<EditText>(R.id.new_memory).text.toString()))
        val uptime = parseDouble(isStringEmpty(findViewById<EditText>(R.id.new_uptime).text.toString()))
        val owner = isStringEmpty(findViewById<EditText>(R.id.new_owner_name).text.toString())
        val department = isStringEmpty(findViewById<EditText>(R.id.new_owner_department).text.toString())
        //val occurredAt = "2018-02-02 15:54:05"
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val occurredAt = SimpleDateFormat(pattern).format(Timestamp(System.currentTimeMillis()))
        val screen = isStringEmpty(imagePath)
        // id is automatic
        if (newState == -1) throw IncompleteException("incomplete")
        val reportObject = Report(-1, newState, report, specification, operatingSystem, memory, uptime, owner, department, occurredAt, screen)
        sendPost(Constants.host_ip, reportObject.toJson())
    }

    fun sendPost(url: String, body: JSONObject) {
        val json = JsonObjectRequest(Request.Method.POST, "$url/reports", body,
                Response.Listener { response ->
                    setResult(Constants.NEW_REPORT_OK)
                    finish()
                },
                Response.ErrorListener { error ->
                    if (error is NoConnectionError || error is TimeoutError) {
                        println("DEBUG: opening error popup")
                        val conErrIntent = Intent(this, ConnectionErrorActivity::class.java)
                        this@NewReportActivity.setResult(Constants.OFFLINE)
                        startActivity(conErrIntent)
                    }
                })
        mRequestQueue.add(json)
    }

    fun isStringEmpty(str: String): String {
        if (str.isBlank()) throw IncompleteException("empty field(s)")
        else return str
    }

    override fun onBackPressed() {
        // discard changes popup
        val popupIntent = Intent(this, DiscardReportActivity::class.java)
        startActivityForResult(popupIntent, Constants.DISCARD_REQUEST)
    }
}
