/**
 * Vipawan  Jarukitpipat 6088044
 * Klinton  Chhun        6088111
 *
 * Add Eat or Exercise activity
 */

package com.example.dailycarl.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailycarl.R

class AddChoiceActivity : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return AddChoiceActivity()
        }
    }
    /**
     * @param:  inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
     * @return: View
     *
     * Start according to the selected menu
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_addchoice, container, false)
        val intent = Intent(activity, MapActivity::class.java) //Go to MapActivity
        val addEatBtn = view.findViewById<TextView>(R.id.add_eat_act_btn)
        val addExBtn = view.findViewById<TextView>(R.id.add_not_food_act)
        /**Add Eat Record**/
        addEatBtn.setOnClickListener {
            intent.putExtra("activityType", "eat")
            activity?.startActivity(intent)
        }
        /**Add Exercise Record**/
        addExBtn.setOnClickListener {
            intent.putExtra("activityType", "ex")
            activity?.startActivity(intent)
        }
        return view
    }

}