package com.aristo.view.fragments

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.aristo.data.model.FirebaseRealtimeModel
import com.aristo.data.model.FirebaseRealtimeModelImpl

abstract class AbstractBaseFragment : Fragment() {

    val mFirebaseRealtimeModel: FirebaseRealtimeModel by lazy { FirebaseRealtimeModelImpl }
}