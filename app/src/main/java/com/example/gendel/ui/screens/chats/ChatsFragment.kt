package com.example.gendel.ui.screens.chats

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.databinding.FragmentChatsListBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.ui.screens.bill.NewBillFragment
import com.example.gendel.ui.screens.settings.SettingsFragment
import com.example.gendel.utilities.*

class ChatsFragment : Fragment(R.layout.fragment_chats_list) {

    private var _binding: FragmentChatsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ChatsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatsListBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.visibility = View.VISIBLE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_search).visibility = View.GONE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.settings_exit).visibility = View.GONE
        APP_ACTIVITY.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        APP_ACTIVITY.toolbar.title=getString(R.string.title_chats)
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
                    showGroup(newModel)
                })
        }
        binding.chatsListRecycleView.adapter = adapter
        binding.chatsListRecycleView.addItemDecoration(ChatsListsItemDecoration(1))
    }

    private fun showGroup(model: CommonModel) {
        REF_DATABASE_ROOT.child(NODE_BILLS).child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                val newModel = dataSnapshot1.getCommonModel()

                REF_DATABASE_ROOT.child(NODE_CHATS).child(CURRENT_UID).child(model.id)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                        val tempList = dataSnapshot2.children.map { it.getCommonModel() }
                        if (tempList.isNotEmpty()) {
                            if (tempList[0].from == CURRENT_UID) {
                                newModel.lastMessage =
                                    getString(R.string.message_from_you) + ": " + tempList[0].text
                                adapter.updateListItems(newModel)
                            } else {
                                getReceivedName(tempList[0].from) {
                                    newModel.lastMessage = it + ": " + tempList[0].text
                                    adapter.updateListItems(newModel)
                                }
                            }
                        } else
                            adapter.updateListItems(newModel)
                    })
            })

    }
}