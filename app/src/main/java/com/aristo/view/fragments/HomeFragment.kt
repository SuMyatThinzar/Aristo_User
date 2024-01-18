package com.aristo.view.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.aristo.utils.manager.SharedPreferenceManager
import com.aristo.data.vos.CategoryVO
import com.aristo.databinding.FragmentHomeBinding
import com.aristo.data.vos.NewProductVO
import com.aristo.network.FirebaseRealtimeApi
import com.aristo.view.activities.ChildCategoriesActivity
import com.aristo.view.activities.MainCategoriesActivity
import com.aristo.view.activities.ProductDetailActivity
import com.aristo.adapters.HomeCategoryListAdapter
import com.aristo.adapters.NewItemsAdapter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class HomeFragment : AbstractBaseFragment(), HomeCategoryListAdapter.HomeMainCategoryListener, NewItemsAdapter.NewItemListener {

    lateinit var binding: FragmentHomeBinding
    private val mNewItemsAdapter: NewItemsAdapter by lazy { NewItemsAdapter(this) }
    private val mCategoryAdapter: HomeCategoryListAdapter by lazy { HomeCategoryListAdapter(this) }

    private var categoryVOList: List<CategoryVO> = listOf()
    private var sortedList: List<NewProductVO> = arrayListOf()
    private var newCategoryListVO: ArrayList<CategoryVO> = arrayListOf()

    private var selectedCategoryVO : CategoryVO? = null
    private var isFound = false
    private var isFoundAll = false
    private var userId = ""

    companion object {
        const val TOPIC = "/topics/myTopic2"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBarNewItem.visibility = View.VISIBLE
        binding.progressBarMain.visibility = View.VISIBLE

        setUpAdapters()
        setUpListeners()
        fetchData()
        setDeviceToken()
    }

    private fun setUpListeners() {
        binding.btnSeeMore.setOnClickListener {
            val intent = Intent(activity, MainCategoriesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setDeviceToken(){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    addUserDeviceToken(token)
                    Log.d("FCM Token", token)
                } else {
                    Log.e("FCM Token", "Failed to get FCM token")
                }
            }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
    }

    private fun addUserDeviceToken(token : String){
        val databaseReference = FirebaseDatabase.getInstance().getReference("Tokens")

        // Check if the key is not null before adding the data
        databaseReference.child(userId).child("token").setValue(token)
            .addOnSuccessListener {
                // Data successfully added to the database
                Log.d("Firebase", "Token added successfully")
            }
            .addOnFailureListener { e ->
                // Failed to add token to the database
                Log.w("Firebase", "Error adding token to database", e)
            }
    }

    private fun fetchData(){

        getUser()

        mFirebaseRealtimeModel.getMainCategoryData { isSuccess, data ->
            if (isSuccess) {
                newCategoryListVO.clear()
                if (data.isNotEmpty()) {
                    categoryVOList = data
                    mCategoryAdapter.setNewData(data)
                } else {
                    binding.rvCategoryList.visibility = View.GONE
                    binding.titleMainCategory.visibility = View.GONE
                }
            } else {
                binding.rvCategoryList.visibility = View.GONE
                binding.titleMainCategory.visibility = View.GONE
                Toast.makeText(requireContext(), "Can't retrieve data.", Toast.LENGTH_LONG).show()
            }
            binding.progressBarMain.visibility = View.GONE
        }

        mFirebaseRealtimeModel.getNewProducts { isSuccess, data ->

            if (isSuccess) {
                sortedList = arrayListOf()
                newCategoryListVO.clear()
                if (data.isNotEmpty()) {
                    sortedList = data.sortedByDescending { it.timeStamp }

                    // new item list
                    sortedList.forEach {  newCategory ->
                        isFoundAll = false
                        categoryVOList.forEach { allCategory ->
                            if (!isFoundAll) {
                                findAllNewCategories(allCategory, newCategory)
                            }
                        }
                    }
                    mNewItemsAdapter.setNewData(newCategoryListVO, sortedList)


                    binding.flNewItem.visibility = View.VISIBLE
                    binding.tvNewItemTitle.visibility = View.VISIBLE
                    binding.dotsIndicatorNewItems.visibility = View.VISIBLE
                } else {
                    binding.flNewItem.visibility = View.GONE
                    binding.tvNewItemTitle.visibility = View.GONE
                    binding.dotsIndicatorNewItems.visibility = View.GONE
                }
            } else {
                binding.flNewItem.visibility = View.GONE
                binding.tvNewItemTitle.visibility = View.GONE
                binding.dotsIndicatorNewItems.visibility = View.GONE
                Toast.makeText(requireContext(), "Can't retrieve data.", Toast.LENGTH_LONG).show()
            }
            binding.progressBarNewItem.visibility = View.GONE
        }

    }

    private fun setUpAdapters() {

        binding.viewPagerNewItems.adapter = mNewItemsAdapter
        binding.dotsIndicatorNewItems.attachTo(binding.viewPagerNewItems)

        // Auto scrolling ViewPager2
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = binding.viewPagerNewItems.currentItem
                val nextItem = if (currentItem == mNewItemsAdapter.itemCount - 1) 0 else currentItem + 1

                binding.viewPagerNewItems.currentItem = nextItem

                handler.removeCallbacks(this)
                handler.postDelayed(this, 2500)
            }
        }

        val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        handler.removeCallbacks(runnable)
                    }
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        handler.postDelayed(runnable, 2500)
                    }
                }
            }
        }
        binding.viewPagerNewItems.registerOnPageChangeCallback(pageChangeCallback)
        handler.postDelayed(runnable, 2500)

        binding.rvCategoryList.adapter = mCategoryAdapter
        binding.rvCategoryList.layoutManager = GridLayoutManager(requireContext(),2)
    }

    override fun onTapHomeMainCategory(position: Int) {
        val intent = Intent(context, MainCategoriesActivity::class.java)
        intent.putExtra("main category position", position)
        startActivity(intent)
    }

    override fun onTapNewItem(categoryVO: CategoryVO) {

        isFound = false

        if (categoryVO.subCategories.isEmpty()) {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("product", categoryVO)
            startActivity(intent)
        } else {
            categoryVOList.forEach { mainCategory ->
                if(!isFound) {
                    findSelectedCategory(mainCategory, categoryVO)
                }
            }
            selectedCategoryVO?.let {
                val intent = Intent(context, ChildCategoriesActivity:: class.java)
                intent.putExtra("childCategoriesList", selectedCategoryVO)
                startActivity(intent)
            }
        }
    }

    private fun findSelectedCategory(rootCategoryVO: CategoryVO, currentCategoryVO: CategoryVO?) {
        if (rootCategoryVO.id == currentCategoryVO?.id) {
            isFound = true
            selectedCategoryVO = rootCategoryVO
        }

        for (subCategory in rootCategoryVO.subCategories.values) {
            findSelectedCategory(subCategory, currentCategoryVO)
        }
    }


    private fun findAllNewCategories(rootCategoryVO: CategoryVO, newProductVO: NewProductVO?) {
        if (rootCategoryVO.id == newProductVO?.id) {
            isFoundAll = true
            newCategoryListVO.add(rootCategoryVO)
        }

        for (subCategory in rootCategoryVO.subCategories.values) {
            findAllNewCategories(subCategory, newProductVO)
        }
    }

    private fun getUser(){
        SharedPreferenceManager.initialize(requireContext())
        val phone = SharedPreferenceManager.getData("UserLogIn")

        phone?.let {
            mFirebaseRealtimeModel.getUser(phone) { message, user ->
                if (user != null) {
                    userId = user.userId.toString()
                    SharedPreferenceManager.setData("userId", userId)
                    SharedPreferenceManager.setData("points", user.point.toString())
                    setDeviceToken()

                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}