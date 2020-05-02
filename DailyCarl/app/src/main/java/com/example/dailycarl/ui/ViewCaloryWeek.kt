package com.example.dailycarl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailycarl.R
import com.example.dailycarl.database.ActivityDB
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.util.*

class ViewCaloryWeek : Fragment() {

    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference

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
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val pickMonth = viewRoot.findViewById<TextView>(R.id.viewWeek_pickMonth)
        pickMonth.setText("" + (month+1) + "/" + year)
        calculationWeek(viewRoot, "" + (month+1) + "/" + year)
        pickMonth.setOnClickListener {
            val dialogFragment = MonthYearPickerDialogFragment.getInstance(month, year)
            fragmentManager?.let { it1 -> dialogFragment.show(it1, null) }
            dialogFragment.setOnDateSetListener { year, monthOfYear ->
                pickMonth.setText("" + (monthOfYear+1) + "/" + year)
                calculationWeek(viewRoot, "" + (monthOfYear+1) + "/" + year)
            }
        }
        return viewRoot
    }

    private fun calculationWeek(viewRoot: View, date: String){
        var userId = ""
        var currentUser = mAuth!!.currentUser
        currentUser?.let { userId = currentUser.uid }
        database.child("Users").child(userId).child("usersActivity")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var totalGoalEx = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0)
                    var totalEx = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0)
                    var totalGoalFood = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0)
                    var totalFood = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0)
                    for(snapshot in dataSnapshot.children){
                        val activityDB = snapshot.getValue<ActivityDB>()
                        val activityDate = activityDB!!.date!!.split("/")
                        val monthYear = "" + activityDate[1] + "/" + activityDate[2]
                        if(monthYear ==  date){
                            if(activityDB.type.toString() == "ex"){
                                when(activityDate[0].toDouble()){
                                    in 1.0..7.0 -> { totalEx[0] += activityDB.activityCalory!!.toDouble(); totalGoalEx[0] += activityDB.goal!!.toDouble()}
                                    in 8.0..14.0 -> { totalEx[1] += activityDB.activityCalory!!.toDouble(); totalGoalEx[1] += activityDB.goal!!.toDouble()}
                                    in 15.0..21.0 -> { totalEx[2] += activityDB.activityCalory!!.toDouble(); totalGoalEx[2] += activityDB.goal!!.toDouble()}
                                    in 22.0..28.0 -> { totalEx[3] += activityDB.activityCalory!!.toDouble(); totalGoalEx[3] += activityDB.goal!!.toDouble()}
                                    in 29.0..31.0 -> { totalEx[4] += activityDB.activityCalory!!.toDouble(); totalGoalEx[4] += activityDB.goal!!.toDouble()}
                                }
                            }
                            if(activityDB.type.toString() == "eat"){
                                when(activityDate[0].toDouble()){
                                    in 1.0..7.0 -> { totalFood[0] += activityDB.activityCalory!!.toDouble(); totalGoalFood[0] += activityDB.goal!!.toDouble()}
                                    in 8.0..14.0 -> { totalFood[1] += activityDB.activityCalory!!.toDouble(); totalGoalFood[1] += activityDB.goal!!.toDouble()}
                                    in 15.0..21.0 -> { totalFood[2] += activityDB.activityCalory!!.toDouble(); totalGoalFood[2] += activityDB.goal!!.toDouble()}
                                    in 22.0..28.0 -> { totalFood[3] += activityDB.activityCalory!!.toDouble(); totalGoalFood[3] += activityDB.goal!!.toDouble()}
                                    in 29.0..31.0 -> { totalFood[4] += activityDB.activityCalory!!.toDouble(); totalGoalFood[4] += activityDB.goal!!.toDouble()}
                                }
                            }
                        }
                    }
                    tableExercise(viewRoot, totalEx, totalGoalEx, totalFood, totalGoalFood)
                }
            })
    }

    private fun tableExercise(viewRoot: View, totalEx: Array<Double>, totalExGoal: Array<Double>, totalFood: Array<Double>, totalFoodGoal: Array<Double>){
        val exCal  = arrayOf(viewRoot.findViewById<TextView>(R.id.viewWeek_ex_cal1)
            , viewRoot.findViewById(R.id.viewWeek_ex_cal2)
            , viewRoot.findViewById(R.id.viewWeek_ex_cal3)
            , viewRoot.findViewById(R.id.viewWeek_ex_cal4)
            , viewRoot.findViewById(R.id.viewWeek_ex_cal5))
        val exGoal = arrayOf(viewRoot.findViewById<TextView>(R.id.viewWeek_ex_goal1)
            , viewRoot.findViewById(R.id.viewWeek_ex_goal2)
            , viewRoot.findViewById(R.id.viewWeek_ex_goal3)
            , viewRoot.findViewById(R.id.viewWeek_ex_goal4)
            , viewRoot.findViewById(R.id.viewWeek_ex_goal5))
        val foodCal = arrayOf(viewRoot.findViewById<TextView>(R.id.viewWeek_food_cal1)
            , viewRoot.findViewById(R.id.viewWeek_food_cal2)
            , viewRoot.findViewById(R.id.viewWeek_food_cal3)
            , viewRoot.findViewById(R.id.viewWeek_food_cal4)
            , viewRoot.findViewById(R.id.viewWeek_food_cal5))
        val foodGoal = arrayOf(viewRoot.findViewById<TextView>(R.id.viewWeek_food_goal1)
            , viewRoot.findViewById(R.id.viewWeek_food_goal2)
            , viewRoot.findViewById(R.id.viewWeek_food_goal3)
            , viewRoot.findViewById(R.id.viewWeek_food_goal4)
            , viewRoot.findViewById(R.id.viewWeek_food_goal5))
        for(i in 0..4){
            exCal[i].text    = totalEx[i].toString()
            exGoal[i].text   = totalExGoal[i].toString()
            foodCal[i].text  = totalFood[i].toString()
            foodGoal[i].text = totalFoodGoal[i].toString()
        }
    }

    private fun tableFood(viewRoot: View, date: String){

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}