package com.aristo.data.vos

import java.io.Serializable

data class CustomerVO (
    var userId: Long = System.currentTimeMillis(),
    var userName: String = "",
    var phone: String = "",
    var address: String = "",
    var password: String = "",
    var point: Int = 0,
) : Serializable