package com.aristo.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.aristo.utils.manager.openDialApp
import com.aristo.utils.manager.openFacebookApp
import com.aristo.utils.manager.openViberApp
import com.aristo.utils.manager.replacePrefix
import com.aristo.utils.manager.setUnderLineForLinks
import com.aristo.databinding.FragmentShopInformationBinding

class ShopInformationFragment : AbstractBaseFragment() {

    lateinit var binding: FragmentShopInformationBinding
    private var fbPageLink : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
        requestNetworkCall()
    }

    private fun setUpListeners() {
        binding.tvFacebook.setOnClickListener {
            openFacebookApp(requireActivity(), fbPageLink)
        }

        binding.tvPhoneNo.setOnClickListener {
            openDialApp(requireActivity(), binding.tvPhoneNo.text.toString())
        }

        binding.tvViber.setOnClickListener {
            val phoneNo = binding.tvViber.text.toString()
            val modifiedPhoneNo = replacePrefix(phoneNo)
            openViberApp(requireActivity(),modifiedPhoneNo)
        }
    }

    private fun requestNetworkCall(){
        binding.progressBar.visibility = View.VISIBLE

        mFirebaseRealtimeModel.getShopDetail { isSuccess, data ->
            binding.progressBar.visibility = View.GONE

            if (isSuccess){

                if (data != null){

                    setViewVisibility(data.address, binding.locationLayout,  binding.tvAddress)
                    setViewVisibility(data.viber, binding.viberLayout,  binding.tvViber)
                    setViewVisibility(data.phone, binding.phoneLayout,  binding.tvPhoneNo)
                    setViewVisibility(data.fbPage, binding.facebookLayout,  binding.tvFacebook)

                    if (data.fbPageLink != ""){
                        fbPageLink = data.fbPageLink
                    }

                    setUnderLine()
                }
                else {
                    hideAllView()
                }
            }
        }
    }

    private fun hideAllView(){
        binding.phoneLayout.visibility = View.GONE
        binding.facebookLayout.visibility = View.GONE
        binding.viberLayout.visibility = View.GONE
        binding.locationLayout.visibility = View.GONE
    }

    private fun setViewVisibility(address: String, locationLayout: LinearLayout, tvAddress: TextView) {
        if (address == ""){
            locationLayout.visibility = View.GONE
        }
        else{
            locationLayout.visibility = View.VISIBLE
            tvAddress.text = address
        }
    }

    private fun setUnderLine(){
        setUnderLineForLinks(binding.tvPhoneNo)
        setUnderLineForLinks(binding.tvFacebook)
        setUnderLineForLinks(binding.tvViber)

    }
}