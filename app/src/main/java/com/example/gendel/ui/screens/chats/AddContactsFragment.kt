package com.example.gendel.ui.screens.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.databinding.FragmentAddContactsBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.ui.screens.base.BaseFragment
import com.example.gendel.utilities.*

class AddContactsFragment : BaseFragment(R.layout.fragment_add_contacts) {

    private var _binding: FragmentAddContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AddContactsAdapter
    //private val refContactsList = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
    private val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var listItems = listOf<CommonModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        listContacts.clear()
        APP_ACTIVITY.title = getString(R.string.add_participant)
        hideKeyboard()
        /*initRecyclerView()*/
        binding.addContactsButtonNext.setOnClickListener {
            if (listContacts.isEmpty()) {
                showToast("Добавьте участников")
            } else {
                replaceFragment(CreateGroupFragment(listContacts))
            }
        }
    }

    /*private fun initRecyclerView() {
        adapter = AddContactsAdapter()
        refContactsList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            listItems = dataSnapshot.children.map { it.getCommonModel() }
            listItems.forEach { model ->
                refUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                        val newModel = dataSnapshot1.getCommonModel()

                        refMessages.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener {
                                if (newModel.fullname.isEmpty()) {
                                    newModel.fullname = newModel.phone
                                }
                                adapter.updateListItems(newModel)
                            })
                    })
            }
        })

        binding.addContactsRecycleView.adapter = adapter
    }*/

    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }
}