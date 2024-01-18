package com.aristo.dataholders

import com.aristo.data.vos.CartVO

class CartListDataHolder private constructor(){

    var cartVOList : ArrayList<CartVO> = arrayListOf()

    companion object {
        val instance: CartListDataHolder = CartListDataHolder()
    }

}