package com.example.dailycarl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment

class SettingActivity : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return SettingActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val language_choose = ArrayAdapter.createFromResource(this,
//            R.array.language_list, android.R.layout.simple_spinner_dropdown_item)
//        language_choose.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}