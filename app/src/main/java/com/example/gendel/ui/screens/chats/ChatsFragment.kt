package com.example.gendel.ui.screens.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gendel.R
import com.example.gendel.database.NODE_BILLS
import com.example.gendel.database.REF_DATABASE_ROOT
import com.example.gendel.database.USER
import com.example.gendel.database.getCommonModel
import com.example.gendel.databinding.FragmentChatsBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.ui.screens.base.BaseChatFragment
import com.example.gendel.ui.screens.main_list.MainListAdapter
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.AppValueEventListener
import com.example.gendel.utilities.ListsItemDecoration
import com.example.gendel.utilities.hideKeyboard

class ChatsFragment : BaseChatFragment(R.layout.fragment_chats) {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ChatsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.visibility = View.VISIBLE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_search).visibility = View.GONE
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.bottom_menu_chats)
        hideKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = ChatsListAdapter()
        USER.registered.forEach { (key, _) ->
            REF_DATABASE_ROOT.child(NODE_BILLS).child(key)
                .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                    val newModel = dataSnapshot1.getCommonModel()
                    adapter.updateListItems(newModel)
                })
        }
        binding.chatsListRecycleView.adapter = adapter
    }
}