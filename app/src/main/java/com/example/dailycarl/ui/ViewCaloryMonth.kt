/**
 * Vipawan  Jarukitpipat 6088044
 * Klinton  Chhun        6088111
 *
 * View remaining Calories in Month mode
 */
package com.example.dailycarl.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailycarl.R
import com.example.dailycarl.database.ActivityDB
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlin.collections.HashMap

class ViewCaloryMonth : Fragment() {

    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference

    companion object {
        fun newInstance(): Fragment {
            return ViewCaloryMonth()
        }
    }
    /**
     * @param:  inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
     * @return: View
     *
     * Retrieve information from the selected month
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.activty_view_calory_month, container, false)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val pickMonth = viewRoot.findViewById<TextView>(R.id.viewMonth_pickMonth)
        pickMonth.text = "" + (month+1) + "/" + year
        val currentMonth = "" + (month+1) + "/" + year
        renderBarChart(viewRoot, currentMonth)
        pickMonth.setOnClickListener {
            val dialogFragment = MonthYearPickerDialogFragment.getInstance(month, year)
            fragmentManager?.let { it1 -> dialogFragment.show(it1, null) }
            dialogFragment.setOnDateSetListener { year, monthOfYear ->
                pickMonth.text = "" + (monthOfYear+1) + "/" + year
                renderBarChart(viewRoot, "" + (monthOfYear+1) + "/" + year)
            }
        }

        return viewRoot
    }
    /**
     * @param:  viewRoot: View,
     *          currentMonth: String
     *
     * Render bar chart of each day activity in that month
     */
    private fun renderBarChart(viewRoot: View, currentMonth: String){
        var viewExGoal = viewRoot.findViewById<TextView>(R.id.viewMonth_goal_ex_data)
        var viewEx = viewRoot.findViewById<TextView>(R.id.viewMonth_cal_ex_data)
        var viewFoodGoal = viewRoot.findViewById<TextView>(R.id.viewMonth_goal_food_data)
        var viewFood = viewRoot.findViewById<TextView>(R.id.viewMonth_cal_food_data)
        var userId = ""
        var currentUser = mAuth!!.currentUser
        /**retrieve information from the database**/
        currentUser?.let { userId = currentUser.uid }
        database.child("Users").child(userId).child("usersActivity")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val exercise = HashMap<Float, Float>()
                    val goalEx = HashMap<Float, Float>()
                    val food = HashMap<Float, Float>()
                    val goalFood = HashMap<Float, Float>()
                    var totalExGoal = 0.0
                    var totalEx = 0.0
                    var totalFoodGoal = 0.0
                    var totalFood = 0.0
                    for(snapshot in dataSnapshot.children){
                        val activityDB = snapshot.getValue<ActivityDB>()
                        val activityDate = activityDB!!.date!!.split("/")
                        val monthYear = "" + activityDate[1] + "/" + activityDate[2]
                        /** Retrieve total calories from Exercise activity **/
                        if(currentMonth == monthYear && activityDB!!.type == "ex"){
                            if(exercise.keys.contains(activityDate[0].toFloat())){
                                val temp = exercise[activityDate[0].toFloat()]
                                totalEx += activityDB.activityCalory!!.toFloat()
                                exercise[activityDate[0].toFloat()] =  temp!! + activityDB.activityCalory!!.toFloat()
                            }else{
                                exercise[activityDate[0].toFloat()] = activityDB.activityCalory!!.toFloat()
                                totalEx += activityDB.activityCalory!!.toFloat()
                            }
                            goalEx[activityDate[0].toFloat()] = activityDB.goal!!.toFloat()
                            totalExGoal += activityDB.goal!!.toFloat()
                        }
                        /** Retrieve total calories from Eat activity **/
                        if(currentMonth == monthYear && activityDB!!.type == "eat"){
                            if(food.keys.contains(activityDate[0].toFloat())){
                                val temp = food[activityDate[0].toFloat()]
                                totalFood += activityDB.activityCalory!!.toFloat()
                                food[activityDate[0].toFloat()] = temp!! + activityDB.activityCalory!!.toFloat()
                            }else{
                                totalFood += activityDB.activityCalory!!.toFloat()
                                food[activityDate[0].toFloat()] = activityDB.activityCalory!!.toFloat()
                            }
                            goalFood[activityDate[0].toFloat()] = activityDB.goal!!.toFloat()
                            totalFoodGoal += activityDB.goal!!.toFloat()
                        }
                    }
                    viewExGoal.text = "" + totalExGoal + " calories"
                    viewEx.text = "" + totalEx + " calories"
                    viewFoodGoal.text = "" + totalFoodGoal + " calories"
                    viewFood.text = "" + totalFood + " calories"
                    exerciseBarChart(viewRoot, exercise, goalEx)
                    foodBarChart(viewRoot, food, goalFood)
                }
            })
    }
    /**
     * @param:  viewRoot: View,
     *          exEntry: HashMap<Float, Float>,
     *          goalEntry: HashMap<Float, Float>
     *
     * Render bar chart of each day Exercise activity in that month
     */
    private fun exerciseBarChart(viewRoot: View, exEntry: HashMap<Float, Float>, goalEntry: HashMap<Float, Float>){
        var exerciseEntry = ArrayList<BarEntry>()
        var exerciseGoal = ArrayList<BarEntry>()
        for(i in 1..31){
            if(exEntry.keys.contains(i.toFloat())){
                exEntry[i.toFloat()]?.let { BarEntry(i.toFloat(), it) }?.let { exerciseEntry.add(it) }
                goalEntry[i.toFloat()]?.let { BarEntry(i.toFloat(), it) }?.let { exerciseGoal.add(it) }
            }
            else{
                exerciseEntry.add(BarEntry(i.toFloat(), 0f))
                exerciseGoal.add(BarEntry(i.toFloat(), 0f))
            }
        }
        var desc = Description()
        desc.text = "Bar Chart for exercise"
        desc.textSize = 15f
        var barChat = viewRoot.findViewById<BarChart>(R.id.viewMonth_barChartEx)
        barChat.description = desc
        var exDataSet = BarDataSet(exerciseEntry, "Exercise")
        exDataSet.color = Color.YELLOW
        exDataSet.valueTextSize = 15f
        var exGoalDataSet = BarDataSet(exerciseGoal, "Exercise Goal")
        exGoalDataSet.color = Color.GREEN
        exGoalDataSet.valueTextSize = 15f
        var data = BarData(exDataSet, exGoalDataSet)
        barChat.data = data
        var days = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31")
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
        barChat.xAxis.axisMaximum = (0 + barChat.barData.getGroupWidth(groupSpace, barSpace) * 31)
        barChat.axisLeft.axisMinimum = 0f
        barChat.groupBars(0f,groupSpace,barSpace)
        barChat.animateXY(1500,1500)
        barChat.invalidate()
    }
    /**
     * @param:  viewRoot: View,
     *          exEntry: HashMap<Float, Float>,
     *          goalEntry: HashMap<Float, Float>
     *
     * Render bar chart of each day Food activity in that month
     */
    private fun foodBarChart(viewRoot: View, foodEnt: HashMap<Float, Float>, goalFood: HashMap<Float, Float>){
        var foodEntry = ArrayList<BarEntry>()
        var foodGoal = ArrayList<BarEntry>()
        for(i in 1..31){
            if(foodEnt.keys.contains(i.toFloat())){
                foodEnt[i.toFloat()]?.let { BarEntry(i.toFloat(), it) }?.let { foodEntry.add(it) }
                goalFood[i.toFloat()]?.let { BarEntry(i.toFloat(), it) }?.let { foodGoal.add(it) }
            }
            else{
                foodEntry.add(BarEntry(i.toFloat(), 0f))
                foodGoal.add(BarEntry(i.toFloat(), 0f))
            }
        }
        var desc = Description()
        desc.text = "Bar Chart for food"
        desc.textSize = 15f
        var barChat = viewRoot.findViewById<BarChart>(R.id.viewMonth_barChartFood)
        barChat.description = desc
        var foodDataSet = BarDataSet(foodEntry, "Food")
        foodDataSet.color = Color.YELLOW
        foodDataSet.valueTextSize = 15f
        var foodGoalDataSet = BarDataSet(foodGoal, "Food Goal")
        foodGoalDataSet.color = Color.GREEN
        foodGoalDataSet.valueTextSize = 15f
        var data = BarData(foodDataSet, foodGoalDataSet)
        barChat.data = data
        var days = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31")
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
        barChat.xAxis.axisMaximum = (0 + barChat.barData.getGroupWidth(groupSpace, barSpace) * 31)
        barChat.axisLeft.axisMinimum = 0f
        barChat.groupBars(0f,groupSpace,barSpace)
        barChat.animateXY(1500,1500)
        barChat.invalidate()
    }

}