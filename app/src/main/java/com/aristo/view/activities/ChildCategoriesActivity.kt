package com.aristo.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.aristo.dataholders.CartListDataHolder
import com.aristo.data.vos.CategoryVO
import com.aristo.adapters.ChildCategoryListAdapter
import com.aristo.databinding.ActivityChildCategoriesBinding

class ChildCategoriesActivity : AbstractBaseActivity<ActivityChildCategoriesBinding>() {

    private val mSubCategoryAdapter: ChildCategoryListAdapter by lazy { ChildCategoryListAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerViewAdapter()
        setUpListeners()
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

    override fun onResume() {
        super.onResume()
        showBadge()
    }

    private fun showBadge() {
        if (CartListDataHolder.instance.cartVOList.size != 0) {
            binding.tvNotificationBadge.visibility = View.VISIBLE
            binding.tvNotificationBadge.text = CartListDataHolder.instance.cartVOList.size.toString()
        } else {
            binding.tvNotificationBadge.visibility = View.GONE
        }
    }

    private fun setRecyclerViewAdapter(){
        // Inside your DestinationActivity's onCreate() or wherever you need to access the ArrayList
        val subCategoryVO = intent.getSerializableExtra("childCategoriesList") as CategoryVO?
        binding.tvTitle.text = subCategoryVO?.title

        // Sub Categories Recycler View
        binding.rvSubCategories.layoutManager = GridLayoutManager(this,2)
        binding.rvSubCategories.adapter = mSubCategoryAdapter

        if (subCategoryVO != null) {
            mSubCategoryAdapter.setNewData(subCategoryVO.subCategories.values.toList())
        }
    }
}