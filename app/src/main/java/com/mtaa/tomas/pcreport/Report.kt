package com.mtaa.tomas.pcreport

import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonJson
import com.beust.klaxon.json
import org.json.JSONException
import org.json.JSONObject

class Report(_id: Int, _state: Int, _report: String, _spec: String, _os: String, _mem: Int, _upTime: Double, _owner: String, _depart: String, _date: String, _screen: String) {
    var id: Int = _id
    var state: Int = _state
    var report: String = _report
    var specification: String = _spec
    var operatingSystem: String = _os
    var memory: Int = _mem
    var uptime: Double = _upTime
    var owner: String = _owner
    var department: String = _depart
    var occurredAt: String = _date
    var screen: String = _screen

    constructor(`object`: JSONObject) : this(`object`.getInt("id"),
                                        `object`.getInt("state"),
                                        `object`.getString("report"),
                                        `object`.getString("specification"),
                                        `object`.getString("operating_system"),
                                        `object`.getInt("memory"),
                                        `object`.getDouble("uptime"),
                                        `object`.getString("owner"),
                                        `object`.getString("department"),
                                        `object`.getString("occurred_at"),
                                        `object`.getString("screen")) {
        try {
            this.id = `object`.getInt("id")
            this.state = `object`.getInt("state")
            this.report = `object`.getString("report")
            this.specification = `object`.getString("specification")
            this.operatingSystem = `object`.getString("operating_system")
            this.memory = `object`.getInt("memory")
            this.uptime = `object`.getDouble("uptime")
            this.owner = `object`.getString("owner")
            this.department = `object`.getString("department")
            this.occurredAt = `object`.getString("occurred_at").substringBefore('.').replace('T', ' ')
            this.screen = `object`.getString("screen")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun toJson(): JSONObject {
        return JSONObject("""{
               "state" : "${this.state}",
               "report" : "${this.report}",
               "specification" : "${this.specification}",
               "operating_system" : "${this.operatingSystem}",
               "memory" : "${this.memory}",
               "uptime" : "${this.uptime}",
               "owner" : "${this.owner}",
               "department" : "${this.department}",
               "occurred_at" : "${this.occurredAt}",
               "screen" : "${this.screen}"
            }""")
    }

    fun reportToJson(): JSONObject {
        return JSONObject("""{
               "id" : "${this.id}",
               "state" : "${this.state}",
               "report" : "${this.report}",
               "specification" : "${this.specification}",
               "operating_system" : "${this.operatingSystem}",
               "memory" : "${this.memory}",
               "uptime" : "${this.uptime}",
               "owner" : "${this.owner}",
               "department" : "${this.department}",
               "occurred_at" : "${this.occurredAt}",
               "screen" : "${this.screen}"
            }""")
    }

    companion object {
        fun fromJson(jsonObject: JSONObject): Report {
            lateinit var report: Report
            try {
                report = Report(jsonObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return report
        }
    }
}