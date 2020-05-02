package com.example.dailycarl.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailycarl.R
import com.example.dailycarl.database.ActivityDB
import com.example.dailycarl.database.RecycleviewDB
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_view_calories.*
import java.util.*
import kotlin.collections.ArrayList

class ViewCaloriesActivity : Fragment() {

    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference

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
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var pickDay = viewRoot.findViewById<TextView>(R.id.day_picker_view_day)
        val currentDate = "" + day + "/" + (month+1) + "/" + year
        pickDay.text = currentDate
        calculation(currentDate, viewRoot)
        renderRecycleView(viewRoot, currentDate)
        pickDay.setOnClickListener {
            activity?.let { it1 ->
                DatePickerDialog(it1, DatePickerDialog.OnDateSetListener{ _, mYear, mMonth, mDay ->
                    pickDay.text = "" + mDay + "/" + (mMonth+1) + "/" + mYear
                    calculation("" + mDay + "/" + (mMonth+1) + "/" + mYear, viewRoot)
                    renderRecycleView(viewRoot, "" + mDay + "/" + (mMonth+1) + "/" + mYear)
                }, year, month, day)
            }?.show()
        }

        return viewRoot
    }

    private fun renderRecycleView(viewRoot: View, date: String){
        var userId = ""
        var currentUser = mAuth!!.currentUser
        currentUser?.let { userId = currentUser.uid }
        database.child("Users").child(userId).child("usersActivity")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val recycleList = ArrayList<RecycleviewDB>()
                    for(snapShot in dataSnapshot.children) {
                        val activityDB = snapShot.getValue<ActivityDB>()
                        if(activityDB!!.type.toString() == "ex" && activityDB!!.date.toString() == date){
                            recycleList += RecycleviewDB(R.drawable.ex_logo,
                                "Exercise: ", activityDB!!.menu.toString(), activityDB!!.activityCalory.toString(), activityDB!!.location.toString())
                        }
                        if(activityDB!!.type.toString() == "eat" && activityDB!!.date.toString() == date){
                            recycleList += RecycleviewDB(R.drawable.food_logo,
                                "Food: ", activityDB!!.menu.toString(), activityDB!!.activityCalory.toString(), activityDB!!.location.toString())
                        }
                    }
                    val recycleView = viewRoot.findViewById<RecyclerView>(R.id.recycle_view)
                    recycleView.adapter = RecycleViewAdapter(recycleList)
                    recycleView.layoutManager = LinearLayoutManager(activity?.applicationContext)
                    recycleView.setHasFixedSize(true)
                }
            })
    }

    private fun calculation(date: String, viewRoot: View){
        val exGoal = viewRoot.findViewById<TextView>(R.id.viewCal_ex_gaol_data)
        val ex = viewRoot.findViewById<TextView>(R.id.viewCal_ex_data)
        val foodGoal = viewRoot.findViewById<TextView>(R.id.viewCal_food_goal_data)
        val food = viewRoot.findViewById<TextView>(R.id.viewCal_food_data)
        var userId = ""
        var currentUser = mAuth!!.currentUser
        currentUser?.let { userId = currentUser.uid }
        database.child("Users").child(userId).child("usersActivity")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var totalFood = 0.0; var totalEx = 0.0
                    var goalFood = 0.0; var goalEx = 0.0
                    for(snapShot in dataSnapshot.children){
                        val activityDB = snapShot.getValue<ActivityDB>()
                        if(activityDB != null && activityDB.type.toString() == "ex" && activityDB.date.toString() == date){
                            goalEx = activityDB.goal.toString().toDouble()
                            totalEx += activityDB.activityCalory.toString().toDouble()
                        }
                        if(activityDB != null && activityDB.type.toString() == "eat" && activityDB.date.toString() == date){
                            goalFood = activityDB.goal.toString().toDouble()
                            totalFood += activityDB.activityCalory.toString().toDouble()
                        }
                    }
                    exGoal.text = "" + goalEx + " Calories"
                    ex.text = "" + totalEx + " Calories"
                    foodGoal.text = "" + goalFood + " Calories"
                    food.text = "" + totalFood + " Calories"
                    exercisePieChart(viewRoot, totalEx, goalEx)
                    foodPieChart(viewRoot, totalFood, goalFood)
                }
            })
    }

    private fun exercisePieChart (viewRoot: View, exCal: Double, exGoal: Double){
        val pieChart = viewRoot.findViewById<PieChart>(R.id.pieChart)
        pieChart.setUsePercentValues(true)
        pieChart.holeRadius = 0f
        var desc = Description()
        desc.text = "Pie Chart for Exercise"
        desc.textSize = 15f
        pieChart.description = desc

        var value = ArrayList<PieEntry>()
        if(exCal == 0.0 && exGoal == 0.0){
            value.add(PieEntry(100f, "Goal"))
            value.add(PieEntry(0f, "Exercise"))
        }
        if(exCal > exGoal){
            value.add(PieEntry(0f, "Goal"))
            value.add(PieEntry(100f, "Exercise"))
        }
        if(exCal != 0.0 && exGoal != 0.0 && exCal <= exGoal){
            val exCalPercent = (exCal.toFloat() / exGoal.toFloat()) * 100
            val exGaolPercent = 100 - exCalPercent
            value.add(PieEntry(exGaolPercent, "Goal"))
            value.add(PieEntry(exCalPercent, "Exercise"))
        }

        var pieDataSet = PieDataSet(value, "Calories(%)")
        pieDataSet.valueTextSize = 20f
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        var pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.animateXY(1500, 1500)
    }

    private fun foodPieChart(viewRoot: View, foodCal: Double, foodGoal: Double){
        val pieChart = viewRoot.findViewById<PieChart>(R.id.pieChartFood)
        pieChart.setUsePercentValues(true)
        pieChart.holeRadius = 0f
        var desc = Description()
        desc.text = "Pie Chart for Food"
        desc.textSize = 15f
        pieChart.description = desc

        var value = ArrayList<PieEntry>()
        if(foodCal == 0.0 && foodGoal == 0.0){
            value.add(PieEntry(100f, "Goal"))
            value.add(PieEntry(0f, "Food"))
        }
        if(foodCal > foodGoal){
            value.add(PieEntry(0f, "Goal"))
            value.add(PieEntry(100f, "Food"))
        }
        if(foodCal != 0.0 && foodGoal != 0.0 && foodCal <= foodGoal){
            val foodCalPercent = (foodCal.toFloat() / foodGoal.toFloat()) * 100
            val foodGoalPercent = 100 - foodCalPercent
            value.add(PieEntry(foodGoalPercent, "Goal"))
            value.add(PieEntry(foodCalPercent, "Food"))
        }

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
