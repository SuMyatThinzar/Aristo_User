package com.aristo.data.model

import com.aristo.network.FirebaseRealtimeApi

abstract class AbstractBaseModel {
    protected val mFirebaseRealtimeApi : FirebaseRealtimeApi = FirebaseRealtimeApi()
}