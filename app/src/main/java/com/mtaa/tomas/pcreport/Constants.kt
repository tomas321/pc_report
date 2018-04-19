package com.mtaa.tomas.pcreport

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog

class Constants {
    companion object {
        const val host_ip = "http://10.42.0.1:8080" //"http://147.175.152.220:8080"
        const val critical_color = 0xFFFFB2A2 //FF5050
        const val problem_color = 0xFFFFED7C //FFBF3E
        const val resolved_color = 0xFFAAE7AA //1D9C0D
//        RESULTS
        const val OFFLINE = 0
//        delete action results
        const val DELETE_REQUEST = 0
        const val DELETE_RESULT_OK = 1
        const val DELETE_RESULT_FAIL = 2
        const val DELETE_RESULT_404 = 3
//        new report action results
        const val NEW_REPORT_REQUEST = 10
        const val NEW_REPORT_OK = 11
        const val NEW_REPORT_FAIL = 12
//        discard changes results
        const val DISCARD_REQUEST = 20
        const val DISCARD_NO = 21
        const val DISCARD_YES = 22
//        edit report results
        const val EDIT_REQUEST = 30
        const val EDIT_OK = 31
        const val EDIT_CANCEL = 32
        const val EDIT_404 = 33
//        storage permissions
        const val STORAGE_PERMISSION_REQUEST = 100
//        camera results
        const val CAMERA_REQUEST = 200

        fun dialogOK(context: Context, title: String, msg: String) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(msg)
                    .setTitle(title)
                    .setCancelable(false)
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            val alert = builder.create()
            alert.show()
        }
    }
}