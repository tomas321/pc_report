package com.mtaa.tomas.pcreport

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import kotlinx.android.synthetic.main.activity_edit.*
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Double
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt
import java.lang.reflect.Method
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    lateinit var report: Report
    lateinit var mRequestQueue: RequestQueue
    var newState: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(edit_toolbar)

        val reportStr = intent.getStringExtra("report")
        val reportJson = JSONObject(reportStr)
        report = Report.fromJson(reportJson)
        initComponents()

        // setup specification multiliner
        val specificationEditText = findViewById<EditText>(R.id.edit_specification)
        specificationEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
        specificationEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)

        mRequestQueue = Volley.newRequestQueue(this)

        val criticalIcon = findViewById<ImageView>(R.id.edit_critical_choose_image)
        val criticalRadioButton = findViewById<RadioButton>(R.id.edit_critical_choose_radio)
        val problemIcon = findViewById<ImageView>(R.id.edit_problem_choose_image)
        val problemRadioButton = findViewById<RadioButton>(R.id.edit_problem_choose_radio)
        val resolvedIcon = findViewById<ImageView>(R.id.edit_resolved_choose_image)
        val resolvedRadioButton = findViewById<RadioButton>(R.id.edit_resolved_choose_radio)
        criticalIcon.setOnClickListener { v ->
            criticalRadioButton.callOnClick()
        }
        problemIcon.setOnClickListener { v ->
            problemRadioButton.callOnClick()
        }
        resolvedIcon.setOnClickListener { v ->
            resolvedRadioButton.callOnClick()
        }
        criticalRadioButton.setOnClickListener { v ->
            newState = 1
            criticalRadioButton.isChecked = true
            problemRadioButton.isChecked = false
            resolvedRadioButton.isChecked = false
        }
        problemRadioButton.setOnClickListener { v ->
            newState = 2
            criticalRadioButton.isChecked = false
            problemRadioButton.isChecked = true
            resolvedRadioButton.isChecked = false
        }
        resolvedRadioButton.setOnClickListener {v ->
            newState = 3
            criticalRadioButton.isChecked = false
            problemRadioButton.isChecked = false
            resolvedRadioButton.isChecked = true
        }
        val imageChoser = findViewById<Button>(R.id.edit_screen_button)
        imageChoser.setOnClickListener { v ->
            ImagePicker.create(this)
                    .limit(1)
                    .returnMode(ReturnMode.CAMERA_ONLY)
                    .start()
        }
        val date = findViewById<EditText>(R.id.edit_occurred_at)
        date.addTextChangedListener(object : TextWatcher {
            var len = 0
            override fun afterTextChanged(s: Editable?) {
                val str = date.text.toString()
                if (str.length == 4 && len < str.length) {
                    date.append("-")
                } else if (str.length == 7 && len < str.length) {
                    date.append("-")
                } else if (str.length == 10 && len < str.length) {
                    date.append(" ")
                } else if (str.length == 13 && len < str.length) {
                    date.append(":")
                } else if (str.length == 16 && len < str.length) {
                    date.append(":")
                }
            }

            override fun beforeTextChanged(arg: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val str = date.text.toString()
                len = str.length
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image = ImagePicker.getFirstImageOrNull(data)
            findViewById<TextView>(R.id.edit_screen_path).text = image.path
            println("DEBUG: image path: " + report.screen)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_edit -> {
                try {
                    editReport()
                } catch (ie: IncompleteException) {
                    val incompleteIntent = Intent(this, IncompleteActivity::class.java)
                    if (ie.message != null) {
                        incompleteIntent.putExtra("dateFormat", true)
                    }
                    startActivity(incompleteIntent)
                } catch (nfe: NumberFormatException) {
                    val incompleteIntent = Intent(this, IncompleteActivity::class.java)
                    startActivity(incompleteIntent)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        setResult(Constants.EDIT_CANCEL)
        super.onBackPressed()
    }

    fun initComponents() {
        setState(this.report.state)
        newState = report.state
        findViewById<EditText>(R.id.edit_report).setText(this.report.report)
        findViewById<EditText>(R.id.edit_specification).setText(this.report.specification)
        findViewById<EditText>(R.id.edit_os).setText(this.report.operatingSystem)
        findViewById<EditText>(R.id.edit_memory).setText(this.report.memory.toString())
        findViewById<EditText>(R.id.edit_uptime).setText(this.report.uptime.toString())
        findViewById<EditText>(R.id.edit_owner).setText(this.report.owner)
        findViewById<EditText>(R.id.edit_department).setText(this.report.department)
        findViewById<EditText>(R.id.edit_occurred_at).setText(this.report.occurredAt)
        findViewById<TextView>(R.id.edit_screen_path).text = this.report.screen
    }

    fun setState(state: Int) {
        when (state) {
            1 -> findViewById<RadioButton>(R.id.edit_critical_choose_radio).isChecked = true
            2 -> findViewById<RadioButton>(R.id.edit_problem_choose_radio).isChecked = true
            3 -> findViewById<RadioButton>(R.id.edit_resolved_choose_radio).isChecked = true
        }
    }

    fun editReport() {
        report.state = newState
        report.report = isStringEmpty(findViewById<EditText>(R.id.edit_report).text.toString())
        report.specification = isStringEmpty(findViewById<EditText>(R.id.edit_specification).text.toString())
        report.operatingSystem = isStringEmpty(findViewById<EditText>(R.id.edit_os).text.toString())
        report.memory = Integer.parseInt(isStringEmpty(findViewById<EditText>(R.id.edit_memory).text.toString()))
        report.uptime = Double.parseDouble(isStringEmpty(findViewById<EditText>(R.id.edit_uptime).text.toString()))
        report.owner = isStringEmpty(findViewById<EditText>(R.id.edit_owner).text.toString())
        report.department = isStringEmpty(findViewById<EditText>(R.id.edit_department).text.toString())
        report.occurredAt = checkDate(findViewById<EditText>(R.id.edit_occurred_at).text.toString())
        report.screen = findViewById<TextView>(R.id.edit_screen_path).text.toString()

        val newJsonReport = report.toJson()
        sendPut(Constants.host_ip, report.id, newJsonReport)
    }

    fun sendPut(url: String, id: Int, body: JSONObject) {
        val json = JsonObjectRequest(Request.Method.PUT, "$url/reports/$id", body,
                Response.Listener { response ->
                    // handle response
                    val intent = Intent()
                    intent.putExtra("report", response.toString())
                    setResult(Constants.EDIT_OK, intent)
                    finish()
                },
                Response.ErrorListener { error ->
                    if (error is NoConnectionError || error is TimeoutError) {
                        val intent = Intent(this, ConnectionErrorActivity::class.java)
                        this@EditActivity.setResult(Constants.OFFLINE)
                        startActivity(intent)
                        println("DEBUG: opening error popup")
                    } else if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 404) {
                            setResult(Constants.EDIT_404)
                            finish()
                        }
                    }
                })
        mRequestQueue.add(json)
    }

    @SuppressLint("SimpleDateFormat")
    fun checkDate(dateTime: String): String {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = sdf.parse(dateTime)
            val dateInMillis = date.time
            val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Timestamp(dateInMillis))
            return dateString
        } catch (pe: ParseException) {
            throw IncompleteException("bad date format")
        }
    }

    fun isStringEmpty(str: String): String {
        if (str.isBlank()) throw IncompleteException("empty field(s)")
        else return str
    }
}
