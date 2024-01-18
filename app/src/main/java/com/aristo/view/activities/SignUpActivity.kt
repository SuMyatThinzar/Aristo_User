package com.aristo.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aristo.utils.manager.SharedPreferenceManager
import com.aristo.databinding.ActivitySignUpBinding
import com.aristo.data.vos.CustomerVO
import com.aristo.utils.modifyPhoneNumber
import com.aristo.utils.showToastMessage

class SignUpActivity : AbstractBaseActivity<ActivitySignUpBinding>() {

    private var customerVO = CustomerVO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpListeners()
    }

    private fun setUpListeners() {

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnSignUp.setOnClickListener {

            if (validateFields()) {
                requestNetworkCall()
            }
        }
    }

    private fun requestNetworkCall() {
        binding.progressBar.visibility = View.VISIBLE
        var found = false

        mFirebaseRealtimeModel.getUser(customerVO.phone) { message, user ->
            if (!found) {
                found = true
                if (user != null) {
                    binding.tvError.visibility = View.VISIBLE
                    Toast.makeText(this, "Phone Number is already registered", Toast.LENGTH_SHORT).show()
                } else {
                    mFirebaseRealtimeModel.signUpUser(customerVO) { successSignUp ->
                        if (successSignUp) {
                            SharedPreferenceManager.initialize(this)
                            SharedPreferenceManager.setData("UserLogIn", customerVO.phone)

                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        } else {
                            showToastMessage(applicationContext, "Failed to Sign Up")
                        }
                    }
                }
            }
            binding.progressBar.visibility = View.GONE
        }

    }
    private fun validateFields() : Boolean{
        val phoneNo = modifyPhoneNumber(binding.etPhoneNumber.text.toString())
        val userName = binding.etUserName.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val address = binding.etAddress.text.toString()

        return if (userName.isEmpty() || phoneNo.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || address.isEmpty()) {
            showToastMessage(applicationContext, "Please enter User Name, Phone Number, Password and Address")
            false
        } else if (password != confirmPassword) {
            showToastMessage(applicationContext, "Passwords do not match")
            false
        } else if (password.length < 6) {
            showToastMessage(applicationContext, "Password is too short")
            false
        } else if (phoneNo.length < 10 || phoneNo.length > 16) {
            showToastMessage(applicationContext, "Invalid Phone Number")
            false
        } else {
            customerVO = CustomerVO(userName = userName, phone = phoneNo, password = password, address = address)
            true
        }
    }

}