package com.darax.instagrotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.darax.instagrotlin.navigation.AlarmFragment
import com.darax.instagrotlin.navigation.DetailViewFragment
import com.darax.instagrotlin.navigation.GridFragment
import com.darax.instagrotlin.navigation.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.action_home ->{
                var detailViewFragment= DetailViewFragment()
                supportFragmentManager.beginTransaction().replace(R.id.maincontent,detailViewFragment).commit()
                return true
            }
            R.id.action_search ->{
                var gridFragment= GridFragment()
                supportFragmentManager.beginTransaction().replace(R.id.maincontent,gridFragment).commit()
                return true
            }
            R.id.action_add_photo ->{

                return true
            }
            R.id.action_favorite_alarm ->{
                var alarmFragment= AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.maincontent,alarmFragment).commit()
                return true
            }
            R.id.action_account ->{
                var userFragment= UserFragment()
                supportFragmentManager.beginTransaction().replace(R.id.maincontent,userFragment).commit()
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_navigation.setOnNavigationItemSelectedListener(this)
    }
}
