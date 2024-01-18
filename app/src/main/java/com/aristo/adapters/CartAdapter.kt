package com.aristo.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristo.dataholders.CartListDataHolder
import com.aristo.databinding.ViewHolderCartBinding
import com.aristo.data.vos.CartVO
import com.aristo.utils.processColorCode
import com.bumptech.glide.Glide

class CartAdapter(private var listener: CartItemListener) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private var cartVOList = listOf<CartVO>()

    class CartViewHolder(var binding: ViewHolderCartBinding, val context: Context, var listener: CartItemListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartVO: CartVO, position: Int) {

            if (cartVO.product?.colorCode != "" && cartVO.product?.colorCode?.count() in 7..10){
                binding.ivProductImage.foreground = ColorDrawable(Color.parseColor(processColorCode(cartVO.product!!.colorCode)))
            } else if (cartVO.product?.imageURL != "") {
                binding.ivProductImage.foreground = null
                Glide.with(context).load(cartVO.product?.imageURL).into(binding.ivProductImage)
            }

            binding.tvProductName.text = cartVO.product?.title

            val cartList = CartListDataHolder.instance.cartVOList

            var quantity = cartVO.quantity

            binding.tvQuantity.text = getQuantityText(quantity, cartVO.product?.type)

            binding.btnAdd.setOnClickListener {
                quantity+=1
                binding.btnQuantity.text = quantity.toString()
                binding.tvQuantity.text = getQuantityText(quantity, cartVO.product?.type)

                cartList.forEach {
                    if (it.product?.id == cartVO.product?.id) {
                        it.quantity = quantity
                    }
                }
                CartListDataHolder.instance.cartVOList = cartList
                listener.onTapAdd()
            }

            binding.btnMinus.setOnClickListener {
                if (quantity >1) { quantity-=1 }
                else {
                    listener.onTapDelete(cartVO)
                    return@setOnClickListener
                }
                binding.btnQuantity.text = quantity.toString()
                binding.tvQuantity.text = getQuantityText(quantity, cartVO.product?.type)
                cartList.forEach {
                    if (it.product?.id == cartVO.product?.id) {
                        it.quantity = quantity
                    }
                }
                CartListDataHolder.instance.cartVOList = cartList
                listener.onTapMinus()
            }
            binding.btnQuantity.text = quantity.toString()

//            binding.btnDelete.setOnClickListener {
//                listener.onTapDelete(cart)
//            }
        }

        private fun getQuantityText(quantity: Int, type: String?): String {
            var text = "အရေအတွက် - ($quantity) "
            text += type
//            type?.let {
//                when {
//                    it.contains("ထည်") -> text += " ထည် \n"
//                    it.contains("လိပ်") -> text += " လိပ် \n"
//                    it.contains("စီး") -> text += " စီး \n"
//                    it.contains("ကွင်း") -> text += " ကွင်း \n"
//                }
//            }
            return text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ViewHolderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding, parent.context, listener)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        if (cartVOList.isNotEmpty()) {
            holder.bind(cartVOList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return cartVOList.size
    }

    fun setNewData(cartVOS: List<CartVO>) {
        cartVOList = cartVOS
        notifyDataSetChanged()
    }

    interface CartItemListener {
        fun onTapDelete(cartVO: CartVO)
        fun onTapAdd()
        fun onTapMinus()
    }
}