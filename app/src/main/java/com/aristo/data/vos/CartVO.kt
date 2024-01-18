package com.aristo.data.vos

import java.io.Serializable

data class CartVO (
    var product: CategoryVO? = null,
    var quantity: Int = 0,
): Serializable