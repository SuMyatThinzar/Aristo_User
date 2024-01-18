package com.aristo.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.aristo.utils.manager.SharedPreferenceManager
import com.aristo.databinding.ActivityLoginBinding
import com.aristo.utils.modifyPhoneNumber
import com.aristo.utils.showToastMessage

class LoginActivity : AbstractBaseActivity<ActivityLoginBinding>() {

    private var phoneNumber = ""
    private var password = ""

    override fun onStart() {
        super.onStart()

        // check if user is already logged in or not, if yes redirect to HomeScreen, if not stay
        SharedPreferenceManager.initialize(this)
        if (SharedPreferenceManager.getData("UserLogIn") != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else {
            mFirebaseRealtimeModel.signInUser()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpListeners()
    }

    private fun setUpListeners() {

        binding.btnLogin.setOnClickListener {
            if (validateFields()) {
                requestNetworkCall()
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun validateFields() : Boolean{
        val phoneNo = modifyPhoneNumber(binding.etPhoneNumber.text.toString())
        val pass = binding.etPassword.text.toString()

        return if (phoneNo.length < 10 || phoneNo.length > 16) {
            showToastMessage(applicationContext, "Invalid Phone Number")
            false
        } else if (phoneNo.isEmpty() || pass.isEmpty()) {
            showToastMessage(applicationContext, "Please enter Phone Number, Password")
            false
        } else if (pass.length < 6) {
            showToastMessage(applicationContext, "Password is too short")
            false
        } else {
            phoneNumber = phoneNo
            password = pass
            true
        }
    }

    private fun requestNetworkCall() {
        binding.progressBar.visibility = View.VISIBLE

        var found = false

        mFirebaseRealtimeModel.getUser(phoneNumber) { message, user ->
            if (!found) {
                found = true
                if (user != null) {
                    if (password == user.password) {
                        SharedPreferenceManager.setData("UserLogIn", user.phone)
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    } else {
                        showToastMessage(applicationContext, "Wrong Phone Number or Password")
                    }
                } else {
                    showToastMessage(applicationContext, message)
                }
            }
            binding.progressBar.visibility = View.GONE
        }
    }
}