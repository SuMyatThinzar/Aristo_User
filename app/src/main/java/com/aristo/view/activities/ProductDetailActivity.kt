package com.aristo.view.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.aristo.dataholders.CartListDataHolder
import com.aristo.utils.processColorCode
import com.aristo.data.vos.CategoryVO
import com.aristo.databinding.ActivityProductDetailBinding
import com.aristo.data.vos.CartVO
import com.bumptech.glide.Glide

class ProductDetailActivity : AbstractBaseActivity<ActivityProductDetailBinding>() {

    private var product : CategoryVO? = CategoryVO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        product = intent.getSerializableExtra("product") as? CategoryVO

        setUpListeners()
        setUpData()
    }

    private fun setUpData() {
        binding.tvTitle.text = product?.title
        if (product?.price != 0) {
            binding.tvPrice.visibility = View.VISIBLE
            binding.tvPrice.text = "စျေးနှုန်း - ${product?.price.toString()} "
        }

        if (product?.colorCode != "" && product?.colorCode?.count() in 7..10){
            binding.ivProduct.foreground = ColorDrawable(Color.parseColor(processColorCode(product!!.colorCode)))
            binding.ivZoom.visibility = View.GONE
        } else if (product?.imageURL != "") {
            binding.ivProduct.foreground = null
            binding.ivZoom.visibility = View.VISIBLE
            Glide.with(this).load(product?.imageURL).into(binding.ivProduct)
        }
        binding.tvType.text = product?.type
    }

    private fun setUpListeners() {
        var quantity = binding.btnQuantity.text.toString().toInt()

        binding.ibBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.btnAdd.setOnClickListener {
            quantity+=1
            binding.btnQuantity.text = quantity.toString()
        }

        binding.btnMinus.setOnClickListener {
            if (quantity >0) { quantity-=1 }
            binding.btnQuantity.text = quantity.toString()
        }

        binding.btnAddToCart.setOnClickListener {
            var cartVOList = arrayListOf<CartVO>()
            if (CartListDataHolder.instance.cartVOList?.isNotEmpty() == true) {
                cartVOList = CartListDataHolder.instance.cartVOList as ArrayList<CartVO>
            }

            product?.let{ product->
                if (cartVOList.isNotEmpty()) {

                    val itemToUpdate = cartVOList.find { it.product?.id == product.id }
                    if (itemToUpdate != null) {
                        itemToUpdate.quantity += quantity
                    } else {
                        cartVOList.add(CartVO(product, quantity))
                    }
                } else {
                    cartVOList.add(CartVO(product, quantity))
                }
            }
            CartListDataHolder.instance.cartVOList = cartVOList
            finish()
        }

        binding.ivProduct.setOnClickListener {

            if (product?.imageURL != ""){
                val intent = Intent(this, FullImageActivity::class.java)
                intent.putExtra("image",product?.imageURL)
                startActivity(intent)
            }

        }
    }
}