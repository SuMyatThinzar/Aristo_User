package com.aristo.view.activities

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.aristo.R
import com.aristo.dataholders.CartListDataHolder
import com.aristo.databinding.ActivityMainBinding
import com.aristo.view.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class MainActivity : AbstractBaseActivity<ActivityMainBinding>() {

    private var doubleBackToExitPressedOnce = false
    private var navigateToCart: String? = null

    private lateinit var itemView : BottomNavigationItemView
    private lateinit var badge: View
    lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateToCart = intent.getStringExtra("fragmentToOpen")

        AppCenter.start(
            application, "9e748b4f-85ad-454e-a939-60a8889e7808\"",
            Analytics::class.java, Crashes::class.java
        )

        setUpBottomNavigation()
        setUpNavigationToCart()
        setUpNotificationBadge()
    }

    override fun onResume() {
        super.onResume()
        showBadge()
    }

    private fun setUpBottomNavigation() {

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> supportFragmentManager.beginTransaction().replace(R.id.fl_container, HomeFragment()).commit()
                R.id.action_cart -> supportFragmentManager.beginTransaction().replace(R.id.fl_container, CartFragment()).commit()
                R.id.action_information -> supportFragmentManager.beginTransaction().replace(R.id.fl_container, InformationFragment()).commit()
                R.id.action_profile -> supportFragmentManager.beginTransaction().replace(R.id.fl_container, ProfileFragment()).commit()
            }
            true
        }
    }

    private fun setUpNotificationBadge() {

        itemView = binding.bottomNav.findViewById(R.id.action_cart)
        badge = LayoutInflater.from(this).inflate(R.layout.badge_text, binding.bottomNav, false)
        text = badge.findViewById(R.id.tv_notification_badge)
        itemView.addView(badge)

    }

    private fun setUpNavigationToCart() {
        when (navigateToCart) {
            "Cart" -> {
                supportFragmentManager.beginTransaction().replace(R.id.fl_container, CartFragment()).commit()
                binding.bottomNav.selectedItemId = R.id.action_cart
            }
            else -> {
                supportFragmentManager.beginTransaction().replace(R.id.fl_container, HomeFragment()).commit()
                binding.bottomNav.selectedItemId = R.id.action_home
            }
        }
    }

    fun showBadge() {
        if (CartListDataHolder.instance.cartVOList.size == 0) {
            text.visibility = View.GONE
        } else {
            text.visibility = View.VISIBLE
            text.text = CartListDataHolder.instance.cartVOList.size.toString()
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()

            CartListDataHolder.instance.cartVOList.clear()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click back button again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000L) // Delay for 2 seconds to reset the double tap flag
    }

}