package com.example.gendel

import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.gendel.database.AUTH
import com.example.gendel.database.USER
import com.example.gendel.database.initFirebase
import com.example.gendel.database.initUser
import com.example.gendel.databinding.ActivityMainBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.ui.screens.contacts.ContactsFragment
import com.example.gendel.ui.screens.main_list.MainListFragment
import com.example.gendel.ui.screens.register.RegisterSignInFragment
import com.example.gendel.ui.screens.single_chat.SingleChatFragment
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
    }

    private fun initFunc() {
        setSupportActionBar(toolbar)
        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_home -> replaceFragment(MainListFragment(), false)
                R.id.ic_bookmark -> replaceFragment(
                    SingleChatFragment(
                        CommonModel(
                            id = USER.id,
                            username = USER.username,
                            fullname = USER.fullname,
                            photoUrl = USER.photoUrl
                        )
                    )
                )
                R.id.ic_contacts -> replaceFragment(ContactsFragment())
                R.id.ic_profile -> replaceFragment(SettingsFragment())
            }
            true
        }
        if (AUTH.currentUser != null) {
            binding.bottomNavigationMenu.visibility = View.VISIBLE
            replaceFragment(MainListFragment(), false)
        } else {
            binding.bottomNavigationMenu.visibility = View.GONE
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        DRAW_FRAGMENT.binding.graffitiCanvas.paint.color = color
        DRAW_FRAGMENT.binding.graffitiColorPicker.background.current.setTint(color)
    }

    override fun onDialogDismissed(dialogId: Int) {

    }
}
