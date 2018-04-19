package com.mtaa.tomas.pcreport

import java.security.spec.AlgorithmParameterSpec
import org.json.JSONException
import org.json.JSONArray
import android.R.attr.name
import org.json.JSONObject
import android.R.attr.name

class ReportItem(_report: String, _spec: String, _date: String, _state: Int) {
    var report: String = _report
    var spec: String = _spec
    var date: String = _date
    var state: Int = _state
    var id: Int = -1

    constructor(`object`: JSONObject) : this(`object`.getString("report"), `object`.getString("specification"), `object`.getString("occurred_at"), `object`.getInt("state")) {
        try {
            this.report = `object`.getString("report")
            this.spec = `object`.getString("specification")
            this.date = `object`.getString("occurred_at").substringBefore('.').replace('T', ' ')
            this.state = `object`.getInt("state")
            this.id = `object`.getInt("id")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun parseState(_state: Int): String {
        return when (_state) {
            1 -> "critical"
            2 -> "problem"
            3 -> "resolved"
            else -> ""
        }
    }

    companion object {
        fun fromJson(jsonObjects: JSONArray): MutableList<ReportItem> {
            val reports: MutableList<ReportItem> = mutableListOf()
            for (i in 0 until jsonObjects.length()) {
                try {
                    reports.add(ReportItem(jsonObjects.getJSONObject(i)))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return reports
        }
    }
}