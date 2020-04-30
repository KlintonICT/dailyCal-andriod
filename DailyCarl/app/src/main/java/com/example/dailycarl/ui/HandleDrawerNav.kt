package com.example.dailycarl.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.ui.AppBarConfiguration
import com.example.dailycarl.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class HandleDrawerNav : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var bottomView: BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var bottomPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.handle_drawer_nav)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        bottomView = findViewById(R.id.navigationViewBottom)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigateToFragment(AddChoiceActivity.newInstance())
        val navTitle = findViewById<TextView>(R.id.nav_title)
        navTitle.text = getString(R.string.add_activity_title)
        bottomView.visibility = View.GONE
        navView.setNavigationItemSelectedListener(this)
        bottomView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navTitle = findViewById<TextView>(R.id.nav_title)
        when (item.itemId) {
            R.id.nav_addActivity -> {
                navTitle.text = getString(R.string.add_activity_title)
                bottomView.visibility = View.GONE
                navigateToFragment(AddChoiceActivity.newInstance())
            }
            R.id.nav_viewCalory -> {
                bottomView.visibility = View.VISIBLE
                when(bottomPage){
                    0 -> {
                        navTitle.text = "Day"
                        navigateToFragment(ViewCaloriesActivity.newInstance())
                    }
                    1 -> {
                        navTitle.text = "Week"
                        navigateToFragment(ViewCaloryWeek.newInstance())
                    }
                    2 -> {
                        navTitle.text = "Month"
                        navigateToFragment(ViewCaloryMonth.newInstance())
                    }
                }
            }
            R.id.nav_editGoal -> {
                navTitle.text = "Change Goal"
                bottomView.visibility = View.GONE
                navigateToFragment(EditGoalActivity.newInstance())
            }
            R.id.nav_setting -> {
                navTitle.text = getString(R.string.setting_title)
                bottomView.visibility = View.GONE
                navigateToFragment(SettingActivity.newInstance())
            }
            R.id.nav_logout -> {
                bottomView.visibility = View.GONE
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val navTitle = findViewById<TextView>(R.id.nav_title)
        item.isChecked = true
        when (item.itemId) {
            R.id.view_cal_day -> {
                navTitle.text = "Day"
                bottomPage = 0
                drawerLayout.closeDrawer(GravityCompat.START)
                navigateToFragment(ViewCaloriesActivity.newInstance())
            }
            R.id.view_cal_week -> {
                navTitle.text = "Week"
                bottomPage = 1
                drawerLayout.closeDrawer(GravityCompat.START)
                navigateToFragment(ViewCaloryWeek.newInstance())
            }
            R.id.view_cal_month -> {
                navTitle.text = "Month"
                bottomPage = 2
                drawerLayout.closeDrawer(GravityCompat.START)
                navigateToFragment(ViewCaloryMonth.newInstance())
            }
        }
        false
    }

    private fun navigateToFragment(fragmentToNavigate: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragmentToNavigate)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}