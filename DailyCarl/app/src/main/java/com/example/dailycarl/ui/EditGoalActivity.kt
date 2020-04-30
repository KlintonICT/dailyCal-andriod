package com.example.dailycarl.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailycarl.R

class EditGoalActivity : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return EditGoalActivity()
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
        val viewRoot = inflater.inflate(R.layout.activity_edit_goal, container, false)
        val updateBtn = viewRoot.findViewById<TextView>(R.id.editGoal_update_btn)
        updateBtn.setOnClickListener { startActivity(Intent(activity, HandleDrawerNav::class.java)) }
        return viewRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}