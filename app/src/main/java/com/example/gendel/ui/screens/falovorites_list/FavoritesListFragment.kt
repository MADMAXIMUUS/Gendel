package com.example.gendel.ui.screens.falovorites_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.NODE_BILLS
import com.example.gendel.database.REF_DATABASE_ROOT
import com.example.gendel.database.USER
import com.example.gendel.database.getCommonModel
import com.example.gendel.databinding.FragmentFavoritesListBinding
import com.example.gendel.ui.screens.bill.NewBillFragment
import com.example.gendel.utilities.*

class FavoritesListFragment : Fragment(R.layout.fragment_favorites_list) {
    private var _binding: FragmentFavoritesListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoritesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesListBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.title=getString(R.string.title_group)
        APP_ACTIVITY.toolbar.visibility = View.VISIBLE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_search).visibility = View.GONE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.settings_exit).visibility = View.GONE
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

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.toolbar.title = getString(R.string.bottom_menu_favorite)
        hideKeyboard()
        initRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        adapter = FavoritesListAdapter()
        USER.favorites.forEach { (key, _) ->
            REF_DATABASE_ROOT.child(NODE_BILLS).child(key)
                .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                    val newModel = dataSnapshot1.getCommonModel()
                    adapter.updateListItems(newModel)
                })
        }
        binding.favoritesListRecycleView.adapter = adapter
        binding.favoritesListRecycleView.addItemDecoration(ListsItemDecoration(30, 60))
    }

}