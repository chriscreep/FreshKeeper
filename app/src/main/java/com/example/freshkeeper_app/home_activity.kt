package com.example.freshkeeper_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class home_activity : AppCompatActivity() {

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, home_fragment())
                .commit()
            currentIndex = 0
        }


        bottomNav.setOnItemSelectedListener { item ->

            var newIndex = currentIndex
            var newFragment: Fragment = home_fragment()

            if (item.itemId == R.id.navigation_home) {
                newIndex = 0
                newFragment = home_fragment()
            } else if (item.itemId == R.id.navigation_add) {
                newIndex = 1
                newFragment = productos_fragment()
            } else if (item.itemId == R.id.navigation_user) {
                newIndex = 3
                newFragment = usuario_fragment()
            }


            if (newIndex == currentIndex) {
                return@setOnItemSelectedListener false
            }


            val transaction = supportFragmentManager.beginTransaction()

            if (newIndex > currentIndex) {

                transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            } else {

                transaction.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }


            transaction.replace(R.id.frame_container, newFragment).commit()


            currentIndex = newIndex
            true
        }
    }
}
