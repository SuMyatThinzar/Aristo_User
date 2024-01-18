package com.aristo.view.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.aristo.data.model.FirebaseRealtimeModel
import com.aristo.data.model.FirebaseRealtimeModelImpl

abstract class AbstractBaseActivity<VB: ViewBinding> : AppCompatActivity()  {
    lateinit var binding: VB
    val mFirebaseRealtimeModel: FirebaseRealtimeModel by lazy { FirebaseRealtimeModelImpl }

}