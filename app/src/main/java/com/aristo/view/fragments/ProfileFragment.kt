package com.aristo.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.aristo.utils.manager.SharedPreferenceManager
import com.aristo.databinding.FragmentProfileBinding
import com.aristo.data.vos.CustomerVO
import com.aristo.network.FirebaseRealtimeApi
import com.aristo.utils.showToastMessage

class ProfileFragment: AbstractBaseFragment() {

    lateinit var binding: FragmentProfileBinding
    private var phone : String? = null
    private var loggedInUser : CustomerVO? = null

    private var isEdit = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SharedPreferenceManager.initialize(requireContext())
        phone = SharedPreferenceManager.getData("UserLogIn")

        setUpListeners()
        setUpData()


    }

    private fun setUpData() {
        phone?.let {
            mFirebaseRealtimeModel.getUser(it) { message, user ->
                if (user != null) {
                    loggedInUser = user
                    binding.etName.setText(user.userName)
                    binding.etPhoneNumber.setText(user.phone)
                    binding.etUserId.setText(user.userId.toString())
                    binding.etAddress.setText(user.address)
                    binding.tvPoints.text = "Points :  ${user.point}"
                } else {
                    showToastMessage(requireContext(), message)
                }
            }
        }
    }

    private fun setUpListeners() {

        binding.btnEdit.setOnClickListener {
            if (!isEdit) {
                makeViewsClickable()
            } else {
                makeViewsUnClickable()
            }
        }

        binding.btnSave.setOnClickListener {
            loggedInUser?.let {
                it.userName = binding.etName.text.toString()
                it.address = binding.etAddress.text.toString()
                binding.progressBar.visibility = View.VISIBLE

                mFirebaseRealtimeModel.updateUser(loggedInUser!!) { isSuccess ->
                    if (isSuccess) {
                        makeViewsUnClickable()
                        showToastMessage(requireContext(), "Update Successful")
                    } else {
                        showToastMessage(requireContext(), "Failed to update")
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun makeViewsUnClickable() {
        isEdit = false
        binding.btnSave.visibility = View.GONE
        binding.btnEdit.text = "Edit"
        binding.etName.isFocusable = false
        binding.etAddress.isFocusable = false
    }

    private fun makeViewsClickable() {
        isEdit = true
        binding.btnSave.visibility = View.VISIBLE
        binding.btnEdit.text = "Cancel"
        binding.etName.isFocusable = true
        binding.etName.isFocusableInTouchMode = true
        binding.etAddress.isFocusable = true
        binding.etAddress.isFocusableInTouchMode = true
    }

}