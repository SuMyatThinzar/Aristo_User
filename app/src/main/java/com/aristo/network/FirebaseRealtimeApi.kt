package com.aristo.network

import com.aristo.data.vos.CategoryVO
import com.aristo.data.vos.CustomerVO
import com.aristo.data.vos.NewProductVO
import com.aristo.data.vos.ShopDetailVO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class FirebaseRealtimeApi {

    private val database = FirebaseDatabase.getInstance()
    private val categoriesRef: DatabaseReference = database.getReference("Products")
    val auth: FirebaseAuth = Firebase.auth

    fun getMainCategoryData(completionHandler: (Boolean, ArrayList<CategoryVO>) -> Unit) {

        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val categoriesSnapshot = snapshot.child("Categories")
                val categoriesList: ArrayList<CategoryVO> = ArrayList()

                for (categorySnapshot in categoriesSnapshot.children) {

                    val categoryVOList = categorySnapshot.getValue(CategoryVO::class.java)
                    categoryVOList?.let {
                        categoriesList.add(it)
                    }
                }
                completionHandler(true, categoriesList)
            }

            override fun onCancelled(error: DatabaseError) {
                completionHandler(false, arrayListOf())
            }
        })
    }
    
    fun getNewProducts(completionHandler: (Boolean, ArrayList<NewProductVO>) -> Unit) {

        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val categoriesSnapshot = snapshot.child("NewProducts")
                val newProductVOList: ArrayList<NewProductVO> = ArrayList()

                for (categorySnapshot in categoriesSnapshot.children) {

                    val newProductsVO = categorySnapshot.getValue(NewProductVO::class.java)
                    newProductsVO?.let {
                        newProductVOList.add(it)
                    }
                }
                completionHandler(true, newProductVOList)
            }

            override fun onCancelled(error: DatabaseError) {
                completionHandler(false, arrayListOf())
            }
        })
    }

    fun getShopDetail(completionHandler: (Boolean, ShopDetailVO?) -> Unit) {

        val detailRef: DatabaseReference = database.getReference("companyInfo")

        detailRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val shopDetailVO = snapshot.getValue(ShopDetailVO::class.java)

                completionHandler(true,shopDetailVO)
            }

            override fun onCancelled(error: DatabaseError) {

                completionHandler(false,null)
            }

        })
    }

    fun signInUser() {
        if (auth.currentUser == null){
            val email = "tunlinaung.tla7@gmail.com"
            val password = "Superst@r7"

            auth.signInWithEmailAndPassword(email,password)
        }
    }

    // Create new user account
    fun signUpUser(customerVO: CustomerVO, completionHandler: (Boolean) -> Unit) {
        database.reference.child("Users").child(customerVO.phone).setValue(customerVO)
            .addOnCompleteListener {
                if (it.isSuccessful) { completionHandler(true) }
                else { completionHandler(false) }
            }
    }

    // To update User's personal data (only name and address)
    fun updateUser(customerVO: CustomerVO, completionHandler: (Boolean) -> Unit) {

        database.reference
            .child("Users")
            .child(customerVO.phone)
            .updateChildren( mapOf("userName" to customerVO.userName,
                                   "address" to customerVO.address))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    completionHandler(true)
                } else {
                    completionHandler(false)
                }
        }
    }

    // Login User
    fun getUser(phoneNumber: String, completionHandler: (String?, CustomerVO?) -> Unit) {

        database.reference
            .child("Users")
            .child(phoneNumber)
            .addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val user = snapshot.getValue(CustomerVO::class.java)
                    if (user != null) {
                        completionHandler(null, user)
                    }
                    else {
                        completionHandler("Account does not exist", null)
                    }
                } else { completionHandler("Account does not exist ", null) }
            }

            override fun onCancelled(error: DatabaseError) {
                completionHandler(error.message, null)
            }
        })
    }
}