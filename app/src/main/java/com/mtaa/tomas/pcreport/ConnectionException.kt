package com.mtaa.tomas.pcreport

import java.lang.Exception

class ConnectionException(errorCode: Int, msg: String) : Exception(msg) {
    val error_code = errorCode
}