package com.example.dailycarl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView

class HandleDrawerNav : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.handle_drawer_nav)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigateToFragment(AddChoiceActivity.newInstance())

        val nav_title = findViewById<TextView>(R.id.nav_title)
        nav_title.text = getString(R.string.add_activity_title)
        navView.setNavigationItemSelectedListener(this)

        val logout = findViewById<TextView>(R.id.nav_logout)
        val logout_btn = findViewById<ImageView>(R.id.logout_logo)
        val login_page = Intent(this, MainActivity::class.java)
        logout.setOnClickListener{ startActivity(login_page) }
        logout_btn.setOnClickListener { startActivity(login_page) }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val nav_title = findViewById<TextView>(R.id.nav_title)
//        val add_eat = findViewById<TextView>(R.id.add_eat_act_btn)
//        val add_exercise = findViewById<TextView>(R.id.add_not_food_act)
        when (item.itemId) {
            R.id.nav_addActivity -> {
                nav_title.text = getString(R.string.add_activity_title)
                navigateToFragment(AddChoiceActivity.newInstance())
            }
            R.id.nav_viewCalory -> {
                Toast.makeText(this, "view clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_editGoal -> {
                Toast.makeText(this, "edit clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_setting-> {
                nav_title.text = getString(R.string.setting_title)
                navigateToFragment(SettingActivity.newInstance())
            }
        }
//        add_eat.setOnClickListener {
//            Log.d("ths","plesae show of your self")
//            nav_title.text = "Hello world"
//            Toast.makeText(this, "view clicked", Toast.LENGTH_SHORT).show()
////            navigateToFragment(EatAndExActivity.newInstance())
//        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun navigateToFragment(fragmentToNavigate: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragmentToNavigate)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}