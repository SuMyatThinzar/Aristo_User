package com.aristo.data.model

import com.aristo.data.vos.CategoryVO
import com.aristo.data.vos.CustomerVO
import com.aristo.data.vos.NewProductVO
import com.aristo.data.vos.ShopDetailVO

object FirebaseRealtimeModelImpl : AbstractBaseModel(), FirebaseRealtimeModel {

    override fun getMainCategoryData(completionHandler: (Boolean, ArrayList<CategoryVO>) -> Unit) {
        mFirebaseRealtimeApi.getMainCategoryData(completionHandler)
    }

    override fun getNewProducts(completionHandler: (Boolean, ArrayList<NewProductVO>) -> Unit) {
        mFirebaseRealtimeApi.getNewProducts(completionHandler)
    }

    override fun getShopDetail(completionHandler: (Boolean, ShopDetailVO?) -> Unit) {
        mFirebaseRealtimeApi.getShopDetail(completionHandler)
    }

    override fun signInUser() {
        mFirebaseRealtimeApi.signInUser()
    }

    override fun signUpUser(
        customerVO: CustomerVO,
        completionHandler: (Boolean) -> Unit)
    {
        mFirebaseRealtimeApi.signUpUser(customerVO, completionHandler)
    }

    override fun updateUser(
        customerVO: CustomerVO,
        completionHandler: (Boolean) -> Unit)
    {
        mFirebaseRealtimeApi.updateUser(customerVO, completionHandler)
    }

    override fun getUser(
        phoneNumber: String,
        completionHandler: (String?, CustomerVO?) -> Unit)
    {
        mFirebaseRealtimeApi.getUser(phoneNumber, completionHandler)
    }
}