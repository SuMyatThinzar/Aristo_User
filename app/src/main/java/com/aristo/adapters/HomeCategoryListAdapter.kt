package com.aristo.adapters

import android.content.Context
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions

class HomeCategoryListAdapter(private val listener: HomeMainCategoryListener) : RecyclerView.Adapter<HomeCategoryListAdapter.HomeCategoryListViewHolder>() {

    private var dataList: List<CategoryVO> = listOf()

    class HomeCategoryListViewHolder(private val binding: ViewHolderCategoryListBinding, val context: Context, val listener: HomeMainCategoryListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryVO: CategoryVO, position: Int) {
            itemView.setOnClickListener {
                listener.onTapHomeMainCategory(position)
            }

            binding.tvFirstCategory.text = categoryVO.title

            if(categoryVO.colorCode != ""){
                binding.progressBar.visibility = View.GONE
                binding.ivFirstCategory.foreground = ColorDrawable(Color.parseColor(processColorCode(categoryVO.colorCode)))
            }
            else{
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryListViewHolder {
        val binding = ViewHolderCategoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeCategoryListViewHolder(binding, parent.context, listener)
    }

    override fun onBindViewHolder(holder: HomeCategoryListViewHolder, position: Int) {
        if (dataList.isNotEmpty()) {
            holder.bind(dataList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setNewData(categoryVOList: List<CategoryVO>) {
        dataList = categoryVOList
        notifyDataSetChanged()
    }

    interface HomeMainCategoryListener {
        fun onTapHomeMainCategory(position: Int)
    }
}