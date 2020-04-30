package com.example.dailycarl.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailycarl.R
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import java.util.*
import kotlin.collections.ArrayList
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ViewCaloryMonth : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return ViewCaloryMonth()
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
        val viewRoot = inflater.inflate(R.layout.activty_view_calory_month, container, false)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val pickMonth = viewRoot.findViewById<TextView>(R.id.viewMonth_pickMonth)
        pickMonth.text = "" + (month+1) + "/" + year
        pickMonth.setOnClickListener {
            val dialogFragment = MonthYearPickerDialogFragment.getInstance(month, year)
            fragmentManager?.let { it1 -> dialogFragment.show(it1, null) }
            dialogFragment.setOnDateSetListener { year, monthOfYear ->
                pickMonth.text = "" + (monthOfYear+1) + "/" + year
            }
        }

        exerciseBarChart(viewRoot)
        foodBarChart(viewRoot)

        return viewRoot
    }

    private fun exerciseBarChart(viewRoot: View){
        var desc = Description()
        desc.text = "Bar Chart for exercise"
        desc.textSize = 15f
        var barChat = viewRoot.findViewById<BarChart>(R.id.viewMonth_barChartEx)
        barChat.description = desc
        var exDataSet = BarDataSet(exerciseEntry<BarEntry>(), "Exercise")
        exDataSet.color = Color.YELLOW
        exDataSet.valueTextSize = 15f
        var exGoalDataSet = BarDataSet(exerciseGoal<BarEntry>(), "Exercise Goal")
        exGoalDataSet.color = Color.GREEN
        exGoalDataSet.valueTextSize = 15f
        var data = BarData(exDataSet, exGoalDataSet)
        barChat.data = data
        var days = arrayOf<String>("M", "T", "W", "T", "F", "S", "S")
        var xAxis = barChat.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(days)
        xAxis.setCenterAxisLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        barChat.isDragEnabled = true
        barChat.setVisibleXRangeMaximum(3f)

        val barSpace = 0.08f
        val groupSpace = 0.44f
        data.barWidth = 0.22f
        barChat.xAxis.axisMinimum = 0f
        barChat.xAxis.axisMaximum = (0 + barChat.barData.getGroupWidth(groupSpace, barSpace) * 7)
        barChat.axisLeft.axisMinimum = 0f
        barChat.groupBars(0f,groupSpace,barSpace)
        barChat.animateXY(1500,1500)
        barChat.invalidate()
    }

    private fun <BarEntry> exerciseEntry(): ArrayList<com.github.mikephil.charting.data.BarEntry> {
        var exerciseEntry = ArrayList<com.github.mikephil.charting.data.BarEntry>()
        exerciseEntry.add(BarEntry(1f, 100f))
        exerciseEntry.add(BarEntry(2f, 120f))
        exerciseEntry.add(BarEntry(3f, 140f))
        exerciseEntry.add(BarEntry(4f, 160f))
        exerciseEntry.add(BarEntry(5f, 180f))
        exerciseEntry.add(BarEntry(6f, 200f))
        exerciseEntry.add(BarEntry(7f, 210f))
        return exerciseEntry
    }

    private fun <BarEntry> exerciseGoal(): ArrayList<com.github.mikephil.charting.data.BarEntry> {
        var exGoal = ArrayList<com.github.mikephil.charting.data.BarEntry>()
        exGoal.add(BarEntry(1f, 90f))
        exGoal.add(BarEntry(2f, 130f))
        exGoal.add(BarEntry(3f, 120f))
        exGoal.add(BarEntry(4f, 170f))
        exGoal.add(BarEntry(5f, 150f))
        exGoal.add(BarEntry(6f, 210f))
        exGoal.add(BarEntry(7f, 190f))
        return exGoal
    }

    private fun foodBarChart(viewRoot: View){
        var desc = Description()
        desc.text = "Bar Chart for food"
        desc.textSize = 15f
        var barChat = viewRoot.findViewById<BarChart>(R.id.viewMonth_barChartFood)
        barChat.description = desc
        var foodDataSet = BarDataSet(foodEntry<BarEntry>(), "Food")
        foodDataSet.color = Color.YELLOW
        foodDataSet.valueTextSize = 15f
        var foodGoalDataSet = BarDataSet(foodGoal<BarEntry>(), "Food Goal")
        foodGoalDataSet.color = Color.GREEN
        foodGoalDataSet.valueTextSize = 15f
        var data = BarData(foodDataSet, foodGoalDataSet)
        barChat.data = data
        var days = arrayOf<String>("M", "T", "W", "T", "F", "S", "S")
        var xAxis = barChat.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(days)
        xAxis.setCenterAxisLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        barChat.isDragEnabled = true
        barChat.setVisibleXRangeMaximum(3f)

        val barSpace = 0.08f
        val groupSpace = 0.44f
        data.barWidth = 0.22f
        barChat.xAxis.axisMinimum = 0f
        barChat.xAxis.axisMaximum = (0 + barChat.barData.getGroupWidth(groupSpace, barSpace) * 7)
        barChat.axisLeft.axisMinimum = 0f
        barChat.groupBars(0f,groupSpace,barSpace)
        barChat.animateXY(1500,1500)
        barChat.invalidate()
    }

    private fun <BarEntry> foodEntry(): ArrayList<com.github.mikephil.charting.data.BarEntry> {
        var foodEntry = ArrayList<com.github.mikephil.charting.data.BarEntry>()
        foodEntry.add(BarEntry(1f, 90f))
        foodEntry.add(BarEntry(2f, 130f))
        foodEntry.add(BarEntry(3f, 120f))
        foodEntry.add(BarEntry(4f, 170f))
        foodEntry.add(BarEntry(5f, 150f))
        foodEntry.add(BarEntry(6f, 210f))
        foodEntry.add(BarEntry(7f, 190f))
        return foodEntry
    }

    private fun <BarEntry> foodGoal(): ArrayList<com.github.mikephil.charting.data.BarEntry> {
        var foodGoal = ArrayList<com.github.mikephil.charting.data.BarEntry>()
        foodGoal.add(BarEntry(1f, 100f))
        foodGoal.add(BarEntry(2f, 120f))
        foodGoal.add(BarEntry(3f, 140f))
        foodGoal.add(BarEntry(4f, 160f))
        foodGoal.add(BarEntry(5f, 180f))
        foodGoal.add(BarEntry(6f, 200f))
        foodGoal.add(BarEntry(7f, 210f))
        return foodGoal
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}