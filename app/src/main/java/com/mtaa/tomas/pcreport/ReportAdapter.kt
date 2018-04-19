package com.mtaa.tomas.pcreport

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.security.AccessControlContext
import android.R.attr.name
import android.widget.Button
import android.widget.ImageView
import org.json.JSONArray


class ReportAdapter(_context: Context, _reports: MutableList<ReportItem>) : ArrayAdapter<ReportItem>(_context, 0, _reports) {

    override fun getView(position: Int, _convertView: View?, parent: ViewGroup): View {
        val report: ReportItem = getItem(position)
        var convertView = _convertView

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.home_item, parent, false)
        }
        val reportName: TextView = convertView!!.findViewById(R.id.report_name)
        val specification: TextView = convertView.findViewById(R.id.specification)
        val date: TextView = convertView.findViewById(R.id.report_date)
        val icon: ImageView = convertView.findViewById(R.id.item_state_icon)

        reportName.text = report.report
        specification.text = report.spec
        date.text = report.date
        when (report.state) {
            1 -> icon.setImageResource(R.drawable.ic_warning_black_24dp)
            2 -> icon.setImageResource(R.drawable.ic_build_black_24dp)
            3 -> icon.setImageResource(R.drawable.ic_check_black_24dp)
        }
        val state = report.state
        setStateColor(convertView, state)

        return convertView
    }

    fun setStateColor(view: View, state: Int) {
        when (state) {
            1 -> view.setBackgroundColor(Constants.critical_color.toInt())
            2 -> view.setBackgroundColor(Constants.problem_color.toInt())
            3 -> view.setBackgroundColor(Constants.resolved_color.toInt())
        }
    }

    fun addReport(report: String, spec: String, date: String, state: Int) {
        val reportItem: ReportItem = ReportItem(report, spec, date, state)
        this.add(reportItem)
    }

    fun addReport(jsonReport: JSONArray) {
        val list: MutableList<ReportItem> = ReportItem.fromJson(jsonReport)
        list.sortByDescending { it.date }
        this.addAll(list)
    }
}