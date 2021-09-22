package com.example.gendel.ui.screens.single_chat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.databinding.FragmentChatBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.models.UserModel
import com.example.gendel.ui.message_recycler_view.views.AppViewFactory
import com.example.gendel.ui.screens.base.BaseChatFragment
import com.example.gendel.ui.screens.draw.DrawFragment
import com.example.gendel.ui.screens.main_list.MainListFragment
import com.example.gendel.utilities.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleChatFragment(private val contact: CommonModel) :
    BaseChatFragment(R.layout.fragment_chat) {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var listenerInfoToolbar: AppValueEventListener
    private lateinit var receivingUser: UserModel
    private lateinit var toolbarInfo: View
    private lateinit var refUser: DatabaseReference
    private lateinit var refMessages: DatabaseReference
    private lateinit var adapter: SingleChatAdapter
    private lateinit var messagesListener: AppChildEventListener
    private var countMessages = 15
    private var isScrolling = false
    private var smoothScrollToPosition = true
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var appVoiceRecorder: AppVoiceRecorder
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        appVoiceRecorder.releaseRecorder()
        adapter.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecycleView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        setHasOptionsMenu(true)
        layoutManager = LinearLayoutManager(this.context)
        appVoiceRecorder = AppVoiceRecorder()
        bottomSheetBehaviour = BottomSheetBehavior
            .from(binding.coordinatorLayout.findViewById(R.id.bottom_sheet_choice))
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        binding.chatInputMessage.addTextChangedListener(AppTextWatcher {
            val string = binding.chatInputMessage.text.toString()
            if (string.isEmpty() || string == "Запись") {
                binding.chatButtonSendMessage.visibility = View.GONE
                binding.chatButtonAttachFile.visibility = View.VISIBLE
                binding.chatButtonVoice.visibility = View.VISIBLE
            } else {
                binding.chatButtonSendMessage.visibility = View.VISIBLE
                binding.chatButtonAttachFile.visibility = View.GONE
                binding.chatButtonVoice.visibility = View.GONE
            }
        })
        binding.chatButtonAttachFile.setOnClickListener { attach() }
        CoroutineScope(Dispatchers.IO).launch {
            binding.chatButtonVoice.setOnTouchListener { _, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val messageKey = getMessageKey(contact.id)
                        appVoiceRecorder.startRecord(messageKey)
                        binding.chatInputMessage.setText("Запись")
                        binding.chatButtonVoice.setColorFilter(
                                R.attr.colorPrimary
                            )
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        binding.chatInputMessage.setText("")
                        appVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(
                                Uri.fromFile(file),
                                messageKey,
                                contact.id,
                                TYPE_MESSAGE_VOICE,
                                "Голосовое сообщение"
                            )
                            smoothScrollToPosition = true
                        }
                        binding.chatButtonVoice.colorFilter = null
                    }
                }
                true
            }
        }
    }

    private fun attach() {
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        binding.coordinatorLayout.findViewById<ImageView>(R.id.button_attach_image)
            .setOnClickListener { attachImage() }
        binding.coordinatorLayout.findViewById<ImageView>(R.id.button_attach_file)
            .setOnClickListener { attachFile() }
        binding.coordinatorLayout.findViewById<ImageView>(R.id.button_attach_graffiti)
            .setOnClickListener { attachGraffiti() }
    }

    private fun attachGraffiti() {
        //replaceFragment(DrawFragment(contact.id, TYPE_CHAT))
    }

    private fun attachFile() {
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun attachImage() {
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessageKey(contact.id)
                    uploadFileToStorage(
                        uri,
                        messageKey,
                        contact.id,
                        TYPE_MESSAGE_IMAGE,
                        "Изображение"
                    )
                    smoothScrollToPosition = true

                }
                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessageKey(contact.id)
                    val filename = getFileNameFromUri(uri!!)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_FIlE, filename)
                    smoothScrollToPosition = true
                }
            }
        }
    }


    private fun initRecycleView() {
        adapter = SingleChatAdapter()
        refMessages = REF_DATABASE_ROOT
            .child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        binding.chatRecycleView.adapter = adapter
        binding.chatRecycleView.setHasFixedSize(true)
        binding.chatRecycleView.isNestedScrollingEnabled = false
        binding.chatRecycleView.layoutManager = layoutManager
        messagesListener = AppChildEventListener {
            val message = it.getCommonModel()
            if (smoothScrollToPosition) {
                adapter.addItemToBottom(AppViewFactory.getView(message)) {
                    binding.chatRecycleView.smoothScrollToPosition(adapter.itemCount)
                }

            } else {
                adapter.addItemToTop(AppViewFactory.getView(message)) {
                    binding.chatSwipeRefresh.isRefreshing = false
                }

            }
        }

        refMessages.limitToLast(countMessages).addChildEventListener(messagesListener)

        binding.chatRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy < 0 && layoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })

        binding.chatSwipeRefresh.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        smoothScrollToPosition = false
        isScrolling = false
        countMessages += 10
        refMessages.removeEventListener(messagesListener)
        refMessages.limitToLast(countMessages).addChildEventListener(messagesListener)
    }

    private fun initToolbar() {
        toolbarInfo = APP_ACTIVITY.toolbar.findViewById(R.id.toolbar_info)
        toolbarInfo.visibility = View.VISIBLE
        listenerInfoToolbar = AppValueEventListener {
            receivingUser = it.getUserModel()
            initInfoToolbar()
        }

        refUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        refUser.addValueEventListener(listenerInfoToolbar)
        binding.chatButtonSendMessage.setOnClickListener {
            smoothScrollToPosition = true
            val message = binding.chatInputMessage.text.toString()
            if (message.isNotEmpty())
                sendMessage(message, contact.id, TYPE_TEXT) {
                    /*saveToMainList(contact.id)
                    binding.chatInputMessage.setText("")*/
                }
        }
    }

    private fun initInfoToolbar() {
        toolbarInfo.findViewById<CircleImageView>(R.id.toolbar_chat_image)
            .downloadAndSetImage(receivingUser.photoUrl, "contact")
        if (receivingUser.name.isEmpty()) {
            toolbarInfo
                .findViewById<TextView>(R.id.toolbar_chat_fullname)
                .text = contact.fullname
        } else {
            toolbarInfo
                .findViewById<TextView>(R.id.toolbar_chat_fullname)
                .text = receivingUser.name
        }
        toolbarInfo.findViewById<TextView>(R.id.toolbar_chat_status).text = receivingUser.state
    }

    override fun onPause() {
        super.onPause()
        toolbarInfo.visibility = View.GONE
        refUser.removeEventListener(listenerInfoToolbar)
        refMessages.removeEventListener(messagesListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        appVoiceRecorder.releaseRecorder()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_chat -> clearChat(contact.id) {
                showToast("Чат очищен")
                replaceFragment(MainListFragment())
            }
            R.id.menu_delete_chat -> deleteChat(contact.id) {
                showToast("Чат удален")
                replaceFragment(MainListFragment())
            }
        }
        return true
    }
}