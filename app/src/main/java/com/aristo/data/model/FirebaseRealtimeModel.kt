package com.aristo.data.model

import com.aristo.data.vos.CategoryVO
import com.aristo.data.vos.CustomerVO
import com.aristo.data.vos.NewProductVO
import com.aristo.data.vos.ShopDetailVO

interface FirebaseRealtimeModel {
    fun getMainCategoryData(completionHandler: (Boolean, ArrayList<CategoryVO>) -> Unit)
    fun getNewProducts(completionHandler: (Boolean, ArrayList<NewProductVO>) -> Unit)
    fun getShopDetail(completionHandler: (Boolean, ShopDetailVO?) -> Unit)
    fun signInUser()
    fun signUpUser(customerVO: CustomerVO, completionHandler: (Boolean) -> Unit)
    fun updateUser(customerVO: CustomerVO, completionHandler: (Boolean) -> Unit)
    fun getUser(phoneNumber: String, completionHandler: (String?, CustomerVO?) -> Unit)
}
