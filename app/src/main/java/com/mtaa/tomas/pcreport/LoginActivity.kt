package com.mtaa.tomas.pcreport

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import java.net.ConnectException

class LoginActivity : AppCompatActivity() {
    lateinit var adapter: ReportAdapter
    lateinit var mRequestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init() // init login components

        mRequestQueue = Volley.newRequestQueue(this) // 'this' is Context

        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.isClickable = true
        loginButton.setOnClickListener {
            login(Constants.host_ip, mRequestQueue)
            loginButton.isClickable = false
        }

        val passField = findViewById<EditText>(R.id.login_password)
        passField.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.callOnClick()
            }
            true
        }
    }

    override fun onBackPressed() {
        this.moveTaskToBack(true)
    }

    fun login(url: String, mRequestQueue: RequestQueue): Int {
        var returnVal = 0
        val json = JsonArrayRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    findViewById<Button>(R.id.login_button).isClickable = true
                },
                Response.ErrorListener { error ->
                    if (error is NoConnectionError || error is TimeoutError) {
                        val intent = Intent(this, ConnectionErrorActivity::class.java)
                        startActivity(intent)
                        returnVal = 1
                    } else {
                        val intent = Intent(this, HomeActivity::class.java)
                        //intent.putExtra() // extra data passed through the intent
                        val username = findViewById<TextView>(R.id.login_username)
                        val password = findViewById<TextView>(R.id.login_password)
                        if (password.text.toString() == "admin" && username.text.toString() == "admin") startActivity(intent)
                        else {
                            println("ERROR: Bad login: " + username.text + " " + password.text)
                            val builder = AlertDialog.Builder(this)
                            builder.setMessage("Bad login. ")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                                        dialog.cancel()
                                    })
                            val alert = builder.create()
                            alert.show()
                        }
                    }
                    findViewById<Button>(R.id.login_button).isClickable = true
                    println("ERROR: " + error.printStackTrace())
                })
        mRequestQueue.add(json)
        return returnVal
    }

    fun init() {
        val passField = findViewById<TextView>(R.id.login_password)
        val nameField = findViewById<TextView>(R.id.login_username)

        nameField.text = ("admin")
        passField.text = ("admin")
    }
}
