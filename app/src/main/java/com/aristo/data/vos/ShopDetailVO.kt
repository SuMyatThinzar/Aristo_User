package com.aristo.data.vos

import java.io.Serializable

data class ShopDetailVO(
    var address: String = "",
    val companyName: String ="",
    val fbPage: String ="",
    val fbPageLink: String = "",
    val phone: String = "",
    val viber: String = "",
): Serializable