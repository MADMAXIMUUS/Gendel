package com.example.gendel.ui.screens.main_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.NODE_BILLS
import com.example.gendel.database.REF_DATABASE_ROOT
import com.example.gendel.database.getCommonModel
import com.example.gendel.databinding.FragmentMainListBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.utilities.*

class MainListFragment : Fragment(R.layout.fragment_main_list) {

    private var _binding: FragmentMainListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MainListAdapter
    private val refMainList = REF_DATABASE_ROOT.child(NODE_BILLS)
    private var listItems = listOf<CommonModel>()

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
        return binding.root
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
        adapter = MainListAdapter()
        refMainList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            listItems = dataSnapshot.children.map { it.getCommonModel() }
            listItems.forEach { model ->
                REF_DATABASE_ROOT.child(NODE_BILLS).child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                        val newModel = dataSnapshot1.getCommonModel()
                        adapter.updateListItems(newModel)
                    })
            }
        })
        binding.mainListRecycleView.adapter = adapter
        binding.mainListRecycleView.addItemDecoration(ListsItemDecoration(30,60))
    }
}