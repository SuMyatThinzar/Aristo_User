package com.aristo.data.vos

import java.io.Serializable

data class CategoryVO(
    var id: String = "",
    val title: String ="",
    val price: Int = 0,
    var imageURL: String = "",
    val new: Boolean = false,
    val colorCode : String = "",
    val type : String = "",
    val timeStamp: Long = 0L,
    var subCategories: Map<String, CategoryVO> = mapOf()
) : Serializable
