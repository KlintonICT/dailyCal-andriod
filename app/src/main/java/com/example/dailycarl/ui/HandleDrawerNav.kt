/**
 * Vipawan  Jarukitpipat 6088044
 * Klinton  Chhun        6088111
 *
 * Navigation Side Bar using Drawer
 */
package com.example.dailycarl.ui

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.dailycarl.R
import com.example.dailycarl.database.UserDB
import com.example.dailycarl.helper.ContextWrapper
import com.example.dailycarl.helper.Preference
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class HandleDrawerNav : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var bottomView: BottomNavigationView
    private var bottomPage = 0
    var mAuth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private lateinit var preference: Preference
    /**
     * @param: savedInstanceState: Bundle?
     *
     * When users click icon, it will show navigation side bar
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.handle_drawer_nav)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        handleHeaderDrawerNav()

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
    /**
     * @param: -
     *
     * Show nagivation bar with user name
     */
    private fun handleHeaderDrawerNav(){
        var navigationView = findViewById<NavigationView>(R.id.nav_view);
        var headerView = navigationView.getHeaderView(0)
        var username = headerView.findViewById<TextView>(R.id.header_nav_username)
        var profile = headerView.findViewById<ImageView>(R.id.userProfile)
        var userId = ""
        var currentUser = mAuth!!.currentUser
        /** Get User Name from database and show profile picture**/
        currentUser?.let { userId = currentUser.uid }
        database.child("Users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userDB = dataSnapshot.getValue<UserDB>()
                    username.text = userDB!!.username.toString()
                    if(userDB!!.profilePic.toString().isNotEmpty()){
                        val img = Base64.decode(userDB!!.profilePic.toString(), Base64.DEFAULT) //convert image to base64
                        val image = BitmapFactory.decodeByteArray(img, 0, img.size)
                        profile.setImageBitmap(image) //map base64 back to picture
                    }
                }
            })
    }
    /**
     * @param: item:MenuItem
     * @return: Boolean
     *
     * Navigate to the page according to the selected menu
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navTitle = findViewById<TextView>(R.id.nav_title)
        when (item.itemId) {
            /**Go to AddActivity**/
            R.id.nav_addActivity -> {
                navTitle.text = getString(R.string.add_activity_title)
                bottomView.visibility = View.GONE
                navigateToFragment(AddChoiceActivity.newInstance())
            }
            /**Go to ViewCaloriesActivity**/
            R.id.nav_viewCalory -> {
                bottomView.visibility = View.VISIBLE
                when(bottomPage){
                    /**Select whether in Day/ Week/ Month View at the botton**/
                    0 -> {
                        navTitle.text = getString(R.string.day)
                        navigateToFragment(ViewCaloriesActivity.newInstance())
                    }
                    1 -> {
                        navTitle.text = getString(R.string.week)
                        navigateToFragment(ViewCaloryWeek.newInstance())
                    }
                    2 -> {
                        navTitle.text = getString(R.string.month)
                        navigateToFragment(ViewCaloryMonth.newInstance())
                    }
                }
            }
            /**Go to EditGoalActivity**/
            R.id.nav_editGoal -> {
                navTitle.text = getString(R.string.change_goal)
                bottomView.visibility = View.GONE
                navigateToFragment(EditGoalActivity.newInstance())
            }
            /**Go to SettingActivity**/
            R.id.nav_setting -> {
                navTitle.text = getString(R.string.setting_title)
                bottomView.visibility = View.GONE
                navigateToFragment(SettingActivity.newInstance())
            }
            /**Go to MainActivity**/
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
                navTitle.text = getString(R.string.day)
                bottomPage = 0
                drawerLayout.closeDrawer(GravityCompat.START)
                navigateToFragment(ViewCaloriesActivity.newInstance())
            }
            R.id.view_cal_week -> {
                navTitle.text = getString(R.string.week)
                bottomPage = 1
                drawerLayout.closeDrawer(GravityCompat.START)
                navigateToFragment(ViewCaloryWeek.newInstance())
            }
            R.id.view_cal_month -> {
                navTitle.text = getString(R.string.month)
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

    override fun attachBaseContext(newBase: Context?) {
        preference = Preference(newBase!!)
        val lang = preference.getLoginCount()
        super.attachBaseContext(lang?.let { ContextWrapper.wrap(newBase, it) })
    }
}