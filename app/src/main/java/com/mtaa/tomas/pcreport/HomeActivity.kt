package com.mtaa.tomas.pcreport

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest

import kotlinx.android.synthetic.main.activity_home.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import android.content.DialogInterface
import android.support.v7.app.AlertDialog


class HomeActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var listAdapter: ReportAdapter
    lateinit var mRequestQueue: RequestQueue
    lateinit var mRefresher: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(home_toolbar)
        println("            DIR: " + filesDir)

        add_report_button.setOnClickListener { view ->
            val newReportIntent = Intent(this, NewReportActivity::class.java)
            startActivityForResult(newReportIntent, Constants.NEW_REPORT_REQUEST)
        }

        val reportsList: MutableList<ReportItem> = mutableListOf()
        listAdapter = ReportAdapter(this, reportsList)
        val listView: ListView = findViewById(R.id.home_list)
        listView.adapter = listAdapter
        mRequestQueue = Volley.newRequestQueue(this) // 'this' is Context

        listView.setOnItemClickListener { parent, view, position, id ->
            val item: ReportItem = listView.getItemAtPosition(position) as ReportItem
            newDetailActivity(Constants.host_ip, item.id)
        }

        listView.setOnItemLongClickListener { parent, view, position, id ->
            val item: ReportItem = listView.getItemAtPosition(position) as ReportItem
            val deleteIntent = Intent(this, DeleteActivity::class.java)
            deleteIntent.putExtra("id", item.id)
            startActivityForResult(deleteIntent, Constants.DELETE_REQUEST)
            true
        }

        mRefresher = findViewById(R.id.refresh)
        mRefresher.setOnRefreshListener(this)

        val requestObject = sendGet(Constants.host_ip + "/reports")
        mRequestQueue.add(requestObject)
//        adapter.addReport("report3", "specific", "never")
    }

    override fun onRefresh() {
        handleRefresh()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val listView: ListView = findViewById(R.id.home_list)
        when (requestCode) {
            Constants.DELETE_REQUEST -> {
                if (resultCode == Constants.DELETE_RESULT_OK) {
                    handleRefresh()
                    Snackbar.make(listView, "Successfully deleted report", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                } else if (resultCode == Constants.DELETE_RESULT_FAIL) { } // do nothing
                else if (resultCode == Constants.DELETE_RESULT_404) {
                    handleRefresh()
                    Snackbar.make(listView, "Unknown item delete request", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
            }
            Constants.NEW_REPORT_REQUEST -> {
                if (resultCode == Constants.NEW_REPORT_OK) {
                    handleRefresh()
                    Snackbar.make(listView, "Successfully added new report", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
                else {} // do nothing
            }
            Constants.EDIT_REQUEST -> {
                if (resultCode == Constants.EDIT_OK) handleRefresh()
                else if (resultCode == Constants.EDIT_404) {
                    handleRefresh()
                    Snackbar.make(listView, "Item does not exist", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
            }
            Constants.OFFLINE -> {
                // no refresh keep the current contents
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_logout -> {
                handleLogout()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        this.moveTaskToBack(true)
    }

    fun sendGet(url: String): JsonArrayRequest {
        val json = JsonArrayRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    listAdapter.clear()
                    listAdapter.addReport(response)
                },
                Response.ErrorListener { error ->
                    println("ERROR: " + error.printStackTrace())
                })
//        println("HTTP GET url" + "\n" + json.body)
        return json
    }

    fun newDetailActivity(url: String, id: Int): Int {
        var returnVal = 0
        val json = JsonObjectRequest(Request.Method.GET, "$url/reports/$id", null,
                Response.Listener { response ->
                    if (checkPermissions() == 0) {
                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("response_json", response.toString())
                        startActivityForResult(intent, Constants.EDIT_REQUEST)
                    }
                },
                Response.ErrorListener { error ->
                    if (error is NoConnectionError || error is TimeoutError) {
                        val intent = Intent(this, ConnectionErrorActivity::class.java)
                        startActivity(intent)
                        returnVal = 1

                    }else if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 404) {
                            val builder = AlertDialog.Builder(this)
                            builder.setMessage("Item not found. click OK to refresh.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                                        handleRefresh()
                                    })
                            val alert = builder.create()
                            alert.show()
                        }
                    }
                    if (error.networkResponse != null)
                        println("\t\t\tDEBUG: opening error popup code: ${error.networkResponse.statusCode}\n")
                    println("Error: " + error.printStackTrace())
                })
        mRequestQueue.add(json)
        return returnVal
    }

    fun checkPermissions(): Int {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permissions are not granted
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.STORAGE_PERMISSION_REQUEST)
            return 1
        }
        return 0
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun handleLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Logout?")
                .setCancelable(false)
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
                .setPositiveButton("YES", DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })
        val alert = builder.create()
        alert.show()
    }

    fun handleRefresh() {
        // refresh -> another get, clear and display
        val queue = sendGet(Constants.host_ip + "/reports")
        mRequestQueue.add(queue)
        mRefresher.isRefreshing = false
    }
}
