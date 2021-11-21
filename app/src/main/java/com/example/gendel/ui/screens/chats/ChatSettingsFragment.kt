package com.example.gendel.ui.screens.chats

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.USER
import com.example.gendel.database.clearChat
import com.example.gendel.database.deleteChat
import com.example.gendel.databinding.FragmentChatSettingsBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.downloadAndSetImage
import com.example.gendel.utilities.showToast


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
        APP_ACTIVITY.toolbar.title = "Беседа"
        val window = APP_ACTIVITY.window
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = ContextCompat.getColor(APP_ACTIVITY, R.color.white);
        if (Build.VERSION.SDK_INT >= 29)
            window.isNavigationBarContrastEnforced = true
        initFunc()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initFunc() {
        binding.chatSettingsStoreName.text = group.storeName
        binding.chatSettingsCount.text = "Участников - ${group.memberCount}"
        binding.profilePlaceholderName.text = group.storeName[0].toString()
        binding.chatSettingsChatIcon.downloadAndSetImage(group.photoUrl)
        binding.chatSettingsNotificationButton.setOnClickListener {
            binding.chatSettingsNotificationSwitch.isChecked =
                !binding.chatSettingsNotificationSwitch.isChecked
        }
        binding.chatSettingsLeaveChatButton.setOnClickListener {
            deleteChat(group.id) {
                USER.registered.remove(group.id)
                showToast("Вы покинули чат!")
            }
        }
        binding.chatSettingsClearHistoryButton.setOnClickListener {
            clearChat(group.id) {
                showToast("Чат очищен")
            }
        }
        binding.chatSettingsChatIcon.setOnClickListener {

        }
    }
}