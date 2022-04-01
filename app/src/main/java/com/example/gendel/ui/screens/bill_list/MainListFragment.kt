package com.example.gendel.ui.screens.bill_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.services.interfaces.BillService
import com.example.gendel.database.services.objects.ServiceBuilder
import com.example.gendel.databinding.FragmentMainListBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.ui.screens.bill.NewBillFragment
import com.example.gendel.utilities.*

class MainListFragment : Fragment(R.layout.fragment_main_list) {

    private var _binding: FragmentMainListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainListBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.visibility = View.VISIBLE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_search).visibility = View.VISIBLE
        APP_ACTIVITY.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setBottomNavigationBarColor(R.color.violet)
        changeFloatButton()
        return binding.root
    }

    private fun changeFloatButton() {
        APP_ACTIVITY.binding.buttonNewBill.setImageResource(R.drawable.ic_add)
        APP_ACTIVITY.binding.buttonNewBill.setOnClickListener {
            replaceFragment(NewBillFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.app_name)
        hideKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = ListAdapter(false)
        val billService = ServiceBuilder.buildService(BillService::class.java)
        val requestCall = billService.getBills()
        requestCall.enqueue(AppCallBack<List<CommonModel>> { response ->
            if (response.isSuccessful) {
                val billList = response.body()!!
                billList.forEach {
                    adapter.updateListItems(it)
                }
            } else {
                showToast(getString(R.string.bill_load_error))
            }
        })
        binding.mainListRecycleView.adapter = adapter
        binding.mainListRecycleView.addItemDecoration(ListsItemDecoration(30, 60))
    }
}