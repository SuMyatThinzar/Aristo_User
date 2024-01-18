package com.aristo.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristo.dataholders.CartListDataHolder
import com.aristo.data.vos.CategoryVO
import com.aristo.adapters.ChildCategoryListAdapter
import com.aristo.adapters.MainCategoryListAdapter
import com.aristo.databinding.ActivityMainCategoriesBinding
import com.aristo.data.vos.NewProductVO
import com.aristo.utils.showToastMessage

class MainCategoriesActivity : AbstractBaseActivity<ActivityMainCategoriesBinding>(), MainCategoryListAdapter.MainCategoriesRecyclerViewListener {

    private val mMainCategoryAdapter: MainCategoryListAdapter by lazy { MainCategoryListAdapter(this, this) }
    private val mSubCategoryAdapter: ChildCategoryListAdapter by lazy { ChildCategoryListAdapter(this) }

    private var mainCategoryIndex = 0
    private var categoryVOList: List<CategoryVO> = listOf()
    private var newItemsList: List<NewProductVO> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainCategoryIndex = intent.getIntExtra("main category position", 0)

        setUpListeners()
        setRecyclerViewAdapter()
        requestNetworkCall()
    }

    override fun onResume() {
        super.onResume()
        showBadge()
    }

    private fun requestNetworkCall() {
        mFirebaseRealtimeModel.getNewProducts { isSuccess, data ->
            newItemsList = data
        }

        mFirebaseRealtimeModel.getMainCategoryData { isSuccess, data ->
            if (isSuccess) {
                categoryVOList = data
                mMainCategoryAdapter.setNewData(categoryVOList, mainCategoryIndex)
                mSubCategoryAdapter.setNewData(categoryVOList[mainCategoryIndex].subCategories.values.toList())
            } else {
                showToastMessage(applicationContext, "Can't retrieve data.")
            }
            binding.mainLoading.visibility = View.GONE
        }
    }

    private fun setRecyclerViewAdapter(){
        // Main Categories Recycler View
        binding.rvMainCategories.layoutManager = LinearLayoutManager(this)
        binding.rvMainCategories.adapter = mMainCategoryAdapter

        // Sub Categories Recycler View
        binding.rvSubCategories.layoutManager = GridLayoutManager(this,2)
        binding.rvSubCategories.adapter = mSubCategoryAdapter
    }

    private fun setUpListeners() {

        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.ibCart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToOpen", "Cart")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun showBadge() {
        if (CartListDataHolder.instance.cartVOList.size != 0) {
            binding.tvNotificationBadge.visibility = View.VISIBLE
            binding.tvNotificationBadge.text = CartListDataHolder.instance.cartVOList.size.toString()
        } else {
            binding.tvNotificationBadge.visibility = View.GONE
        }
    }

    // Reload Sub Categories Recycler View when select main categories recycler view
    override fun reloadSubCategoriesRecyclerView(index : Int) {
        mSubCategoryAdapter.setNewData(categoryVOList[index].subCategories.values.toList())
    }
}