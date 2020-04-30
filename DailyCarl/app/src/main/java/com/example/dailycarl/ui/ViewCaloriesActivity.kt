package com.example.dailycarl.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailycarl.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*
import kotlin.collections.ArrayList

class ViewCaloriesActivity : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return ViewCaloriesActivity()
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
        val viewRoot = inflater.inflate(R.layout.activity_view_calories, container, false)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var pickDay = viewRoot.findViewById<TextView>(R.id.day_picker_view_day)
        pickDay.text = "" + day + "/" + (month+1) + "/" + year
        pickDay.setOnClickListener {
            activity?.let { it1 ->
                DatePickerDialog(it1, DatePickerDialog.OnDateSetListener{ _, mYear, mMonth, mDay ->
                    pickDay.text = "" + mDay + "/" + (mMonth+1) + "/" + mYear
                }, year, month, day)
            }?.show()
        }
        exercisePieChart(viewRoot)
        foodPieChart(viewRoot)
        return viewRoot
    }

    private fun exercisePieChart (viewRoot: View){
        val pieChart = viewRoot.findViewById<PieChart>(R.id.pieChart)
        pieChart.setUsePercentValues(true)
        pieChart.holeRadius = 0f
        var desc = Description()
        desc.text = "Pie Chart for Exercise"
        desc.textSize = 15f
        pieChart.description = desc

        var value = ArrayList<PieEntry>()
        value.add(PieEntry(40f, "Goal"))
        value.add(PieEntry(60f, "Exercise"))

        var pieDataSet = PieDataSet(value, "Calories(%)")
        pieDataSet.valueTextSize = 20f
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        var pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.animateXY(1500, 1500)
    }

    private fun foodPieChart(viewRoot: View){
        val pieChart = viewRoot.findViewById<PieChart>(R.id.pieChartFood)
        pieChart.setUsePercentValues(true)
        pieChart.holeRadius = 0f
        var desc = Description()
        desc.text = "Pie Chart for Food"
        desc.textSize = 15f
        pieChart.description = desc

        var value = ArrayList<PieEntry>()
        value.add(PieEntry(50f, "Goal"))
        value.add(PieEntry(50f, "Food"))

        var pieDataSet = PieDataSet(value, "Calories(%)")
        pieDataSet.valueTextSize = 20f
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        var pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.animateXY(1500, 1500)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
