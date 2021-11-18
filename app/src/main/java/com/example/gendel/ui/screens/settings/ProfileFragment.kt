package com.example.gendel.ui.screens.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.AUTH
import com.example.gendel.database.USER
import com.example.gendel.databinding.FragmentProfileBinding
import com.example.gendel.utilities.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var exitView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_search).visibility = View.GONE
        exitView = APP_ACTIVITY.toolbar.findViewById(R.id.settings_exit)
        exitView.visibility = View.VISIBLE
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        APP_ACTIVITY.toolbar.title = resources.getString(R.string.bottom_menu_profile)
        initFields()
    }

    @SuppressLint("SetTextI18n")
    private fun initFields() {
        binding.profileTextViewName.text = USER.name
        binding.profileTextViewEmail.text = USER.email
        binding.profilePlaceholderName.text = USER.name[0].toString()
        exitView.findViewById<ConstraintLayout>(R.id.exit_button).setOnClickListener {
            AppStates.updateState(AppStates.OFFLINE)
            AUTH.signOut()
            restartActivity()
        }
        binding.profileTextViewName.setOnClickListener {
            replaceFragment(ChangeNameFragment())
        }
        binding.settingsUserPhoto.downloadAndSetImage(USER.photoUrl)
    }
}