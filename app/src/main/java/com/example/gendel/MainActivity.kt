package com.example.gendel

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gendel.database.AUTH
import com.example.gendel.database.initFirebase
import com.example.gendel.database.initUser
import com.example.gendel.databinding.ActivityMainBinding
import com.example.gendel.ui.screens.bill.NewBillFragment
import com.example.gendel.ui.screens.chats.ChatsFragment
import com.example.gendel.ui.screens.falovorites_list.FavoritesListFragment
import com.example.gendel.ui.screens.main_list.MainListFragment
import com.example.gendel.ui.screens.register.RegisterSignInFragment
import com.example.gendel.ui.screens.settings.ProfileFragment
import com.example.gendel.utilities.*
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener


class MainActivity : AppCompatActivity(), ColorPickerDialogListener {

    lateinit var binding: ActivityMainBinding
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {
            initFields()
            initFunc()
        }
        binding.bottomNavigationMenu.background = null
        binding.bottomNavigationMenu.menu.getItem(2).isEnabled = false
    }

    private fun initFunc() {
        setSupportActionBar(toolbar)
        binding.buttonNewChat.setOnClickListener {
            replaceFragment(NewBillFragment())
        }
        binding.bottomNavigationMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.ic_home -> replaceFragment(MainListFragment(), false)
                R.id.ic_profile -> replaceFragment(ProfileFragment())
                R.id.ic_favorite -> replaceFragment(FavoritesListFragment())
                R.id.ic_chats -> replaceFragment(ChatsFragment())
            }
            true
        }
        if (AUTH.currentUser != null) {
            if (AUTH.currentUser!!.isEmailVerified) {
                binding.verificationText.visibility = View.GONE
            } else {
                showToast(AUTH.currentUser?.isEmailVerified.toString())
            }
            binding.bottomNavigationMenuRoot.visibility = View.VISIBLE
            replaceFragment(MainListFragment(), false)
        } else {
            binding.bottomNavigationMenuRoot.visibility = View.GONE
            replaceFragment(RegisterSignInFragment(), false)
        }
    }

    private fun initFields() {
        toolbar = binding.mainToolbar
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        DRAW_FRAGMENT.binding.graffitiCanvas.paint.color = color
        DRAW_FRAGMENT.binding.graffitiColorPicker.background.current.setTint(color)
    }

    override fun onDialogDismissed(dialogId: Int) {

    }
}
