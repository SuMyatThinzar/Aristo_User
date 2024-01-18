package com.aristo.view.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristo.R
import com.aristo.dataholders.CartListDataHolder
import com.aristo.data.vos.CategoryVO
import com.aristo.databinding.*
import com.aristo.data.vos.CartVO
import com.aristo.view.activities.MainActivity
import com.aristo.adapters.CartAdapter
import com.aristo.utils.manager.SharedPreferenceManager
import com.aristo.utils.manager.sendMessageToViber
import com.aristo.utils.showToastMessage

class CartFragment : AbstractBaseFragment(), CartAdapter.CartItemListener {

    lateinit var binding: FragmentCartBinding
    private val mCartAdapter: CartAdapter by lazy { CartAdapter(this) }
    private var cartVOList = arrayListOf<CartVO>()
    private var filteredCartListVO = arrayListOf<CartVO>()

    private var isFound = false
    private var userId = ""
    private var point: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (CartListDataHolder.instance.cartVOList.isNotEmpty()) {
            cartVOList = CartListDataHolder.instance.cartVOList
        }

        userId = SharedPreferenceManager.getData("userId").toString()
        point = SharedPreferenceManager.getData("points").toString()
        binding.tvTotalPoints.text = "* လက်ကျန် ($point) points"

        requestNetworkCall()
        setUpAdapters()
        setUpListener()
    }

    private fun requestNetworkCall() {
        binding.progressBar.visibility = View.VISIBLE

        // For Item's data update
        mFirebaseRealtimeModel.getMainCategoryData { isSuccess, data ->
            if (isSuccess) {
                // find cart items in Realtime data and update with new
                cartVOList.forEach outerLoop@{ cart ->
                    isFound = false
                    data.forEach { mainCategory ->
                        if (isFound) {
                            return@outerLoop
                        } else {
                            findCategoryWithEmptySubcategories(mainCategory, cart)
                        }
                    }
                }
                CartListDataHolder.instance.cartVOList = filteredCartListVO
                mCartAdapter.setNewData(filteredCartListVO)
            } else {
                showToastMessage(requireContext(), "Can't retrieve data.")
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun findCategoryWithEmptySubcategories(rootCategoryVO: CategoryVO, cartVO: CartVO){
        if (isFound) {
            return
        }
        if (rootCategoryVO.subCategories.isEmpty()) {
            isFound = rootCategoryVO.id == cartVO.product!!.id
            if (isFound) {
                filteredCartListVO.add(cartVO)
            }
        }
        for (subCategory in rootCategoryVO.subCategories.values) {
            findCategoryWithEmptySubcategories(subCategory, cartVO)
        }
    }

    private fun setUpAdapters() {
        binding.rvCart.adapter = mCartAdapter
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }
    
    private fun setUpListener() {
        binding.btnOrder.setOnClickListener {

            if (cartVOList.isNotEmpty()) {
                showOrderConfirmAlert()
            } else {
                Toast.makeText(requireContext(), "Please add some products", Toast.LENGTH_LONG).show()
            }
        }

        binding.etPointsToUse.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                point?.let{
                    if (!s.isNullOrEmpty() && s.toString().toInt() > it.toInt()) {
                        binding.etPointsToUse.setText(it)
                        binding.etPointsToUse.setSelection(binding.etPointsToUse.text.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun showOrderConfirmAlert() {
        val builder = AlertDialog.Builder(requireContext())
        val customView = LayoutInflater.from(requireContext()).inflate(R.layout.order_confirm_alert,null)
        builder.setView(customView)
        builder.setCancelable(false)

        val btnCancel : Button = customView.findViewById(R.id.btnCancel)
        val btnConfirm : Button = customView.findViewById(R.id.btnConfirm)
        val gridLayout: GridLayout = customView.findViewById(R.id.gridLayout)
        val tvPointsToUse : TextView = customView.findViewById(R.id.tvPontsToUse)

        val dialog = builder.create()

        var message = "Order List (Customer id: $userId \n===========\n"

        cartVOList.forEach {

            message += "${it.product?.title}  (${it.quantity}) ${it.product?.type} \n"

            val itemNameTextView = TextView(context)
            itemNameTextView.text = "${it.product?.title} \n"
            itemNameTextView.setTextColor(resources.getColor(R.color.black))
            itemNameTextView.textSize = 16f

            val params1 = GridLayout.LayoutParams()
            params1.width = 0
            params1.height = GridLayout.LayoutParams.WRAP_CONTENT
            params1.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1.5f)
            itemNameTextView.layoutParams = params1

            val itemQuantityTextView = TextView(context)
            val text = "${it.quantity} ${it.product?.type}"

            itemQuantityTextView.text = text
            itemQuantityTextView.setTextColor(resources.getColor(R.color.black))
            itemQuantityTextView.textSize = 16f

            val params2 = GridLayout.LayoutParams()
            params2.width = 0
            params2.height = GridLayout.LayoutParams.WRAP_CONTENT
            params2.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 0.5f)
            itemNameTextView.layoutParams = params2

            gridLayout.addView(itemNameTextView)
            gridLayout.addView(itemQuantityTextView)

            if (binding.etPointsToUse.text.isNotEmpty()) {
                tvPointsToUse.text = "*points ${binding.etPointsToUse.text} အသုံးပြုမည်"
            }
            else {
                tvPointsToUse.text = ""
            }

        }

        btnCancel.setOnClickListener {
            dialog.cancel()
        }

        btnConfirm.setOnClickListener {

            message += "*points ${binding.etPointsToUse.text} အသုံးပြုမည်"
            sendMessageToViber(requireActivity(), message)

            CartListDataHolder.instance.cartVOList.clear()
            cartVOList.clear()
//            binding.tvTotalQuantity.text = "0"
            binding.etPointsToUse.setText("")
            mCartAdapter.setNewData(cartVOList)
            val mainActivity = activity as MainActivity
            mainActivity.showBadge()
            dialog.cancel()
        }
        dialog.show()
    }

    private fun deleteCartDialog(cartVO: CartVO){
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = OrderConfirmAlertBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        builder.setCancelable(false)

        val dialog = builder.create()

        dialogBinding.tvAlertTitle.text = "Order List ထဲမှဖျက်ရန်"
        dialogBinding.btnConfirm.text = "Confirm"

        val itemNameTextView = TextView(context)
        itemNameTextView.text = "Order List ထဲမှပစ္စည်းကို ဖျက်ရန်သေချာပါသလား"
        itemNameTextView.setTextColor(resources.getColor(R.color.black))
        itemNameTextView.textSize = 16f
        itemNameTextView.gravity = Gravity.CENTER
        dialogBinding.gridLayout.addView(itemNameTextView)

        dialogBinding.btnConfirm.setOnClickListener {
            val itemToUpdate = cartVOList.find { it.product?.id == cartVO.product?.id }
            if (itemToUpdate != null) {
                cartVOList.remove(itemToUpdate)
            }
            CartListDataHolder.instance.cartVOList = cartVOList
            mCartAdapter.setNewData(cartVOList)

            val mainActivity = activity as MainActivity
            mainActivity.showBadge()
            dialog.cancel()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }

    override fun onTapDelete(cartVO: CartVO) {
        deleteCartDialog(cartVO)
    }

    override fun onTapAdd() {
        if (CartListDataHolder.instance.cartVOList.isNotEmpty()) {
//            binding.tvTotalQuantity.text = CartListDataHolder.instance.cartList.size.toString()
        }
    }

    override fun onTapMinus() {
        if (CartListDataHolder.instance.cartVOList.isNotEmpty()) {
//            binding.tvTotalQuantity.text = CartListDataHolder.instance.cartList.size.toString()
        }
    }
}