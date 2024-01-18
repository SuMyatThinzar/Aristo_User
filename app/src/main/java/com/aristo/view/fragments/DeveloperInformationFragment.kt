package com.aristo.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aristo.utils.manager.openEmailApp
import com.aristo.utils.manager.openFacebookApp
import com.aristo.utils.manager.openViberApp
import com.aristo.utils.manager.replacePrefix
import com.aristo.utils.manager.setUnderLineForLinks
import com.aristo.databinding.FragmentDeveloperInformationBinding

class DeveloperInformationFragment : AbstractBaseFragment() {

    lateinit var binding: FragmentDeveloperInformationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeveloperInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
        setUnderLine()
    }

    private fun setUpListeners() {

        binding.tvViber.setOnClickListener {
            val phoneNo = binding.tvViber.text.toString()
            val modifiedPhoneNo = replacePrefix(phoneNo)
            openViberApp(requireActivity(),modifiedPhoneNo)
        }

        binding.tvEmail.setOnClickListener {
            openEmailApp(requireActivity(), binding.tvEmail.text.toString())
        }

        binding.tvFacebook.setOnClickListener {
            openFacebookApp(requireActivity(),"https://www.facebook.com/profile.php?id=61552194935730&mibextid=LQQJ4d")
        }

    }

    private fun setUnderLine(){
        setUnderLineForLinks(binding.tvEmail)
        setUnderLineForLinks(binding.tvFacebook)
        setUnderLineForLinks(binding.tvViber)

    }

}