package com.aristo.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristo.R
import com.aristo.utils.processColorCode
import com.aristo.data.vos.CategoryVO
import com.aristo.databinding.ViewHolderCategoryListBinding
import com.aristo.network.FirebaseRealtimeApi
import com.aristo.view.activities.ChildCategoriesActivity
import com.aristo.view.activities.ProductDetailActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions

class ChildCategoryListAdapter(private val context: Context) : RecyclerView.Adapter<ChildCategoryListAdapter.SubCategoryListViewHolder>() {

    private var subCategoryListVO: List<CategoryVO> = listOf()

    class SubCategoryListViewHolder(private var binding: ViewHolderCategoryListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(categoryVO: CategoryVO, context: Context, position: Int) {
            binding.tvFirstCategory.text = categoryVO.title

            FirebaseRealtimeApi().getNewProducts { success, data ->
                if (success) {
                    if (data != null) {
                        for (it in data) {
                            if (categoryVO.id == it.id) {
                                binding.ivNew.visibility = View.VISIBLE
                                break
                            } else {
                                binding.ivNew.visibility = View.GONE
                            }
                        }
                    }
                }
            }


            if (categoryVO.colorCode != "" && categoryVO.colorCode.count() in 7..10){
                binding.progressBar.visibility = View.GONE
                binding.ivFirstCategory.foreground = ColorDrawable(Color.parseColor(processColorCode(categoryVO.colorCode)))
            } else {
                binding.ivFirstCategory.foreground = null

                binding.progressBar.visibility = View.VISIBLE
                Glide.with(context).load(categoryVO.imageURL).apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            binding.progressBar.visibility = View.GONE
                            binding.ivFirstCategory.setImageResource(R.drawable.ic_placeholder)
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            binding.progressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding.ivFirstCategory)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryListViewHolder {
        val itemView = ViewHolderCategoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubCategoryListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return subCategoryListVO.size
    }

    override fun onBindViewHolder(holder: SubCategoryListViewHolder, position: Int) {

        holder.bind(subCategoryListVO[position],context,position)

        holder.itemView.setOnClickListener {

            if (subCategoryListVO[position].subCategories.isNotEmpty()){
                val intent = Intent(context, ChildCategoriesActivity:: class.java)
                intent.putExtra("childCategoriesList", subCategoryListVO[position])
                context.startActivity(intent)
            } else {
                val intent = Intent(context, ProductDetailActivity:: class.java)
                intent.putExtra("product", subCategoryListVO[position])
                context.startActivity(intent)
            }
        }
    }

    fun setNewData(categories: List<CategoryVO>) {
        subCategoryListVO = categories
        notifyDataSetChanged()
    }

}