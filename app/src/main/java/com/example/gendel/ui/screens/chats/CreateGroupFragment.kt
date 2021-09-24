package com.example.gendel.ui.screens.chats

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gendel.R
import com.example.gendel.databinding.FragmentCreateGroupBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.ui.screens.base.BaseFragment
import com.example.gendel.utilities.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class CreateGroupFragment(private var listContacts: List<CommonModel>) :
    BaseFragment(R.layout.fragment_create_group) {
    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AddContactsAdapter
    private var uri = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        hideKeyboard()
        initRecyclerView()
        binding.createGroupPhoto.setOnClickListener { addPhoto() }
        binding.createGroupButtonDone.setOnClickListener {
            val nameGroup = binding.createGroupInputName.text.toString()
            if (nameGroup.isEmpty()) {
                showToast("Введите имя")
            } else {
                /*createBillAndPushToDatabase(nameGroup, uri, listContacts) {
                    replaceFragment(MainListFragment())
                }*/
            }
        }
        binding.createGroupInputName.requestFocus()
        binding.createGroupCounts.text = getPlurals(R.plurals.count_members, listContacts.size)
    }

    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecyclerView() {
        adapter = AddContactsAdapter()
        binding.createGroupRecycleView.adapter = adapter
        listContacts.forEach { adapter.updateListItems(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            uri = CropImage.getActivityResult(data).uri
            binding.createGroupPhoto.setImageURI(uri)
        }
    }
}