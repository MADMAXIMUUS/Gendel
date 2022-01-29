package com.example.gendel

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gendel.database.*
import com.example.gendel.databinding.ActivityMainBinding
import com.example.gendel.ui.screens.bill.NewBillFragment
import com.example.gendel.ui.screens.chats.ChatsFragment
import com.example.gendel.ui.screens.lists.FavoritesListFragment
import com.example.gendel.ui.screens.lists.MainListFragment
import com.example.gendel.ui.screens.register.RegisterSignInFragment
import com.example.gendel.ui.screens.settings.ProfileFragment
import com.example.gendel.utilities.*
import com.google.android.material.bottomsheet.BottomSheetDialog
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

    override fun onResume() {
        super.onResume()
        isNewVersionAvailable { newVersion, needToDownload ->
            if (needToDownload) {
                val downloadMenu = BottomSheetDialog(APP_ACTIVITY, R.style.SheetDialog)
                downloadMenu.setContentView(R.layout.download_menu)
                downloadMenu.dismiss()
                val version = downloadMenu.findViewById<TextView>(R.id.download_menu_version)
                version!!.text =
                    String.format(getString(R.string.download_menu_version), newVersion.version)
                val description =
                    downloadMenu.findViewById<TextView>(R.id.download_menu_description)
                description!!.text = newVersion.description
                val cancel = downloadMenu.findViewById<TextView>(R.id.download_menu_cancel)
                cancel!!.setOnClickListener {
                    downloadMenu.dismiss()
                }
                val confirm =
                    downloadMenu.findViewById<ConstraintLayout>(R.id.download_menu_confirm)
                confirm!!.setOnClickListener {
                    downloadMenu.dismiss()
                    downloadNewVersion(newVersion)
                }
                downloadMenu.show()
            }
        }
    }

    private fun initFunc() {
        setSupportActionBar(toolbar)
        if (USER.verified == "false") {
            binding.buttonNewBill.isEnabled = false
        }
        binding.buttonNewBill.setOnClickListener {
            replaceFragment(NewBillFragment())
        }
        binding.bottomNavigationMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.ic_home -> replaceFragment(MainListFragment(), true)
                R.id.ic_profile -> replaceFragment(ProfileFragment(), true)
                R.id.ic_favorite -> replaceFragment(FavoritesListFragment(), true)
                R.id.ic_chats -> replaceFragment(ChatsFragment(), true)
            }
            true
        }
        if (AUTH.currentUser != null) {
            if (USER.verified == "false")
                if (AUTH.currentUser!!.isEmailVerified) {
                    val mapData = hashMapOf<String, Any>()
                    mapData[CHILD_VERIFIED] = "true"
                    /*mapData[CHILD_TOKEN] = TOKEN*/
                    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
                        .updateChildren(mapData)
                        .addOnSuccessListener {
                            USER.verified = "true"
                            /*USER.token = TOKEN*/
                        }
                        .addOnFailureListener { }
                }
            if (USER.verified == "true") {
                binding.verificationText.visibility = View.GONE
                binding.buttonNewBill.isEnabled = true
                /*REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
                    .child(CHILD_TOKEN).setValue(TOKEN)
                    .addOnSuccessListener { USER.token = TOKEN }
                    .addOnFailureListener { }*/
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
        if (USER.verified == "false") {
            binding.buttonNewBill.isEnabled = false
        }
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        DRAW_FRAGMENT.binding.graffitiCanvas.paint.color = color
        DRAW_FRAGMENT.binding.graffitiColorPicker.backgroundTintList = ColorStateList.valueOf(color)
    }

    override fun onDialogDismissed(dialogId: Int) {

    }
}
