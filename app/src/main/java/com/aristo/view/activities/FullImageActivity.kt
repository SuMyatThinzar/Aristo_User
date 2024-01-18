package com.aristo.view.activities

import android.os.Bundle
import com.aristo.databinding.ActivityFullImageBinding
import com.bumptech.glide.Glide

class FullImageActivity : AbstractBaseActivity<ActivityFullImageBinding>() {

    private var fullImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fullImageUrl = intent.getStringExtra("image")

        setUpData()
        setUpListeners()
    }

    private fun setUpData() {
        Glide.with(this)
            .load(fullImageUrl)
            .into(binding.ivFullImage)
    }

    private fun setUpListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

}