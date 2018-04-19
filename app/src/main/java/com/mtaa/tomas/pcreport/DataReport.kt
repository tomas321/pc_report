package com.mtaa.tomas.pcreport

import com.beust.klaxon.Json

data class DataReport(@Json(name = "id") val _id: Int,
                      @Json(name = "state") val _state: Int,
                      @Json(name = "report") val _report: String,
                      @Json(name = "specification") val _spec: String,
                      @Json(name = "operating_system") val _os: String,
                      @Json(name = "memory") val _memory: Int,
                      @Json(name = "uptime") val _uptime: Double,
                      @Json(name = "owner") val _owner: String,
                      @Json(name = "department") val _depart: String,
                      @Json(name = "occurred_at") val _occurredAt: String,
                      @Json(name = "screen") val _screen: String) {
    fun create() {

    }
}