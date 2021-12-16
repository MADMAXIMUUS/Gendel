package com.example.gendel.ui.screens.chats

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.databinding.FragmentChatSettingsBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.ui.screens.main_list.MainListFragment
import com.example.gendel.utilities.*


class ChatSettingsFragment(private val group: CommonModel) :
    Fragment(R.layout.fragment_chat_settings) {
    private var _binding: FragmentChatSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatSettingsBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.visibility = View.VISIBLE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_search).visibility = View.GONE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.settings_exit).visibility = View.GONE
        APP_ACTIVITY.toolbar.title=getString(R.string.title_group)
        APP_ACTIVITY.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setBottomNavigationBarColor(R.color.white)
        initFunc()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initFunc() {
        binding.chatSettingsStoreName.text = group.storeName
        binding.chatSettingsCount.text = getString(R.string.count_member_title) + group.memberCount
        binding.profilePlaceholderName.text = group.storeName[0].toString()
        binding.chatSettingsChatIcon.downloadAndSetImage(group.photoUrl)
        binding.chatSettingsNotificationButton.setOnClickListener {
            binding.chatSettingsNotificationSwitch.isChecked =
                !binding.chatSettingsNotificationSwitch.isChecked
        }
        binding.chatSettingsLeaveChatButton.setOnClickListener {
            deleteChat(group.id) {
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_REGISTERED)
                    .child(group.id)
                    .removeValue().addOnSuccessListener {
                        USER.registered.remove(group.id)
                        val mapData = hashMapOf<String, Any>()
                        mapData[CHILD_ID] = group.id
                        mapData[CHILD_MEMBERS_COUNT] = "${group.memberCount.toInt() - 1}"
                        mapData[CHILD_STORE_NAME] = group.storeName
                        mapData[CHILD_START_DATE] = group.startDate
                        mapData[CHILD_END_DATE] = group.endDate
                        mapData[CHILD_COST] = group.cost
                        for (i in 0..group.memberCount.toInt())
                            if (CURRENT_UID == group.members["member $i"]) {
                                group.members.remove("member $i")
                                break;
                            }
                        mapData[CHILD_MEMBERS] = group.members
                        REF_DATABASE_ROOT.child(NODE_BILLS).child(group.id).updateChildren(mapData)
                            .addOnSuccessListener {
                                APP_ACTIVITY.binding.bottomNavigationMenuRoot.visibility = View.VISIBLE
                                APP_ACTIVITY.binding.bottomNavigationMenu.selectedItemId = R.id.ic_home
                                APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                                replaceFragment(MainListFragment())
                            }
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
            }
        }
        binding.chatSettingsClearHistoryButton.setOnClickListener {
            clearChat(group.id) {
            }
        }
        binding.chatSettingsChatIcon.setOnClickListener {

        }
    }
}