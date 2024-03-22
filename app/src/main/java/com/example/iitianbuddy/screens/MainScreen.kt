 package com.example.iitianbuddy.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.iitianbuddy.R
import com.example.iitianbuddy.databinding.ActivityMainScreenBinding
import com.example.iitianbuddy.fragments.DiscoverFragment
import com.example.iitianbuddy.fragments.HomeFragment
import com.example.iitianbuddy.fragments.ProfileFragment

 class MainScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMainScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragment(HomeFragment())

        binding.bottomNav.setOnNavigationItemSelectedListener {
            val id: Int = it.itemId
            if(id == R.id.nav_home){
                loadFragment(HomeFragment())
            }
            else if(id == R.id.nav_discover){
                loadFragment(DiscoverFragment())
            }
            else{
                loadFragment(ProfileFragment())
            }
            true
        }
    }

     private fun loadFragment(fragment: Fragment){
         val fm: FragmentManager = supportFragmentManager
         fm.popBackStackImmediate()
         val ft: FragmentTransaction = fm.beginTransaction()
         ft.replace(R.id.container, fragment)
         ft.commit()
     }
}