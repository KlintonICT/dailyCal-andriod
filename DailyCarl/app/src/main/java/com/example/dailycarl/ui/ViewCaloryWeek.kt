package com.example.dailycarl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailycarl.R
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import java.util.*

class ViewCaloryWeek : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return ViewCaloryWeek()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.activity_view_calory_week, container, false)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val pickMonth = viewRoot.findViewById<TextView>(R.id.viewWeek_pickMonth)
        pickMonth.setText("" + (month+1) + "/" + year)
        pickMonth.setOnClickListener {
            val dialogFragment = MonthYearPickerDialogFragment.getInstance(month, year)
            fragmentManager?.let { it1 -> dialogFragment.show(it1, null) }
            dialogFragment.setOnDateSetListener { year, monthOfYear ->
                pickMonth.setText("" + (monthOfYear+1) + "/" + year)
            }
        }
        return viewRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}