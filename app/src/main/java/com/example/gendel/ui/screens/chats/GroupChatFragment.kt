package com.example.gendel.ui.screens.chats

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
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
import com.example.gendel.utilities.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupChatFragment(private val group: CommonModel) :
    BaseChatFragment(R.layout.fragment_chat) {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var listenerInfoToolbar: AppValueEventListener
    private lateinit var receivingUser: UserModel
    private lateinit var toolbarInfo: View
    private lateinit var refUser: DatabaseReference
    private lateinit var refMessages: DatabaseReference
    private lateinit var adapter: GroupChatAdapter
    private lateinit var messagesListener: AppChildEventListener
    private var countMessages = 15
    private var isScrolling = false
    private var smoothScrollToPosition = true
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var appVoiceRecorder: AppVoiceRecorder
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<*>
    private lateinit var bottomSheetBehaviourQuiz: BottomSheetBehavior<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        APP_ACTIVITY.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setBottomNavigationBarColor(R.color.white_pink)
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
        bottomSheetBehaviourQuiz = BottomSheetBehavior
            .from(binding.coordinatorLayout.findViewById(R.id.bottom_sheet_create_quiz))
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        binding.chatInputMessage.addTextChangedListener(AppTextWatcher {
            val string = binding.chatInputMessage.text.toString()
            if (string.isEmpty() || string == "Запись") {
                binding.chatButtonSendMessage.visibility = View.GONE
                binding.chatButtonAttach.visibility = View.VISIBLE
                binding.chatButtonVoice.visibility = View.VISIBLE
            } else {
                binding.chatButtonSendMessage.visibility = View.VISIBLE
                binding.chatButtonAttach.visibility = View.GONE
                binding.chatButtonVoice.visibility = View.GONE
            }
        })
        binding.chatButtonAttach.setOnClickListener { attach() }
        CoroutineScope(Dispatchers.IO).launch {
            binding.chatButtonVoice.setOnTouchListener { _, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        binding.chatInputMessage.setText("Запись")
                        binding.chatButtonVoice.setColorFilter(
                            R.attr.colorPrimary
                        )
                        val messageKey = getMessageKey(group.id)
                        appVoiceRecorder.startRecord(messageKey)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        binding.chatInputMessage.setText("")
                        appVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(
                                Uri.fromFile(file),
                                messageKey,
                                group,
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
        binding.coordinatorLayout.findViewById<ImageView>(R.id.button_attach_quiz)
            .setOnClickListener { attachQuiz() }
    }

    private fun attachQuiz() {
        hideKeyboard()
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehaviourQuiz.peekHeight = 800
        bottomSheetBehaviourQuiz.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehaviourQuiz.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val quiz =
                    binding.coordinatorLayout.findViewById<ScrollView>(R.id.bottom_sheet_create_quiz)
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                    quiz.findViewById<View>(R.id.create_quiz_handle).visibility = View.GONE
                    quiz.findViewById<ConstraintLayout>(R.id.create_quiz_header).background =
                        ContextCompat.getDrawable(
                            APP_ACTIVITY, R.drawable.bg_bottom_radius
                        )
                    quiz.background = ContextCompat.getDrawable(
                        APP_ACTIVITY, R.drawable.placeholder_image
                    )
                }
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    quiz.findViewById<View>(R.id.create_quiz_handle).visibility = View.VISIBLE
                    quiz.findViewById<ConstraintLayout>(R.id.create_quiz_header).background =
                        ContextCompat.getDrawable(
                            APP_ACTIVITY, R.drawable.bg_all_radius
                        )
                    quiz.background = ContextCompat.getDrawable(
                        APP_ACTIVITY, R.drawable.bg_top_radius
                    )
                }
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    quiz.findViewById<EditText>(R.id.create_quiz_title)
                    quiz.findViewById<LinearLayout>(R.id.create_quiz_answers).removeAllViews()
                    quiz.findViewById<LinearLayout>(R.id.create_quiz_answers)
                        .addView(
                            layoutInflater.inflate(R.layout.create_quiz_answer_element, null)
                        )
                    quiz.findViewById<TextView>(R.id.create_quiz_add_answer_count)
                        .text = getPlurals(
                        R.plurals.count_answers,
                        10 - binding.coordinatorLayout
                            .findViewById<LinearLayout>(R.id.create_quiz_answers).childCount
                    )
                    quiz.findViewById<SwitchMaterial>(R.id.create_quiz_settings_multi_switch)
                        .isChecked = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {


            }

        })
        createQuizSetClicker()
    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    private fun createQuizSetClicker() {
        val answers = binding.coordinatorLayout.findViewById<LinearLayout>(R.id.create_quiz_answers)
        answers.setOnClickListener {
            deleteAnswer(0)
        }
        binding.coordinatorLayout
            .findViewById<LinearLayout>(R.id.create_quiz_add_answer_big_button)
            .setOnClickListener {
                if (answers.childCount < 10) {
                    val answerView =
                        layoutInflater.inflate(R.layout.create_quiz_answer_element, null)

                    answerView.tag = "answer ${answers.childCount}"
                    answerView.findViewById<ImageView>(R.id.create_quiz_answer_delete)
                        .setOnClickListener {
                            val index = answers.indexOfChild(answerView)
                            deleteAnswer(index)
                        }
                    answers.addView(answerView)
                    binding.coordinatorLayout
                        .findViewById<TextView>(R.id.create_quiz_add_answer_count).text =
                        getPlurals(R.plurals.count_answers, 10 - answers.childCount)
                } else {
                    showToast("Добавлено уже максимальное количество ответов")
                }
            }
        binding.coordinatorLayout.findViewById<FloatingActionButton>(R.id.create_quiz_create_button)
            .setOnClickListener { createQuiz(answers) }
        binding.coordinatorLayout.findViewById<ConstraintLayout>(R.id.create_quiz_choose_answer_type)
            .setOnClickListener {
                val switch =
                    binding.coordinatorLayout.findViewById<SwitchMaterial>(R.id.create_quiz_settings_multi_switch)
                switch.isChecked = !switch.isChecked
            }
    }

    private fun createQuiz(answers: LinearLayout) {
        val title = binding.coordinatorLayout
            .findViewById<EditText>(R.id.create_quiz_title).text.toString()

        val isPeekMulti: Boolean = binding.coordinatorLayout
            .findViewById<SwitchMaterial>(R.id.create_quiz_settings_multi_switch).isChecked

        val listAnswer = mutableListOf<String>()

        answers.children.forEach {
            if (it.findViewById<EditText>(R.id.create_quiz_answer_text).text.toString() != "")
                listAnswer.add(it.findViewById<EditText>(R.id.create_quiz_answer_text).text.toString())
        }

        sendQuiz(group.id, title, listAnswer, isPeekMulti) {
            bottomSheetBehaviourQuiz.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehaviourQuiz.peekHeight = 0
        }
    }

    private fun deleteAnswer(id: Int) {
        binding.coordinatorLayout.findViewById<LinearLayout>(R.id.create_quiz_answers)
            .removeViewAt(id)
        binding.coordinatorLayout.findViewById<TextView>(R.id.create_quiz_add_answer_count).text =
            getPlurals(
                R.plurals.count_answers,
                10 - binding.coordinatorLayout
                    .findViewById<LinearLayout>(R.id.create_quiz_answers).childCount
            )
    }

    private fun attachGraffiti() {
        replaceFragment(DrawFragment(group))
    }

    private fun attachFile() {
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        //val intent = Intent(Intent.ACTION_GET_CONTENT)
        //intent.type = "*/*"
        //startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun attachImage() {
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessageKeyForGroup(group.id)
                    uploadFileToStorageForGroup(
                        uri,
                        messageKey,
                        group.id,
                        TYPE_MESSAGE_IMAGE,
                        "Изображение"
                    )
                    smoothScrollToPosition = true
                }
                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessageKeyForGroup(group.id)
                    val filename = getFileNameFromUri(uri!!)
                    uploadFileToStorageForGroup(
                        uri,
                        messageKey,
                        group.id,
                        TYPE_MESSAGE_FIlE,
                        filename
                    )
                    smoothScrollToPosition = true
                }
            }
        }
    }*/

    private fun initRecycleView() {
        adapter = GroupChatAdapter()
        refMessages = REF_DATABASE_ROOT
            .child(NODE_CHATS)
            .child(CURRENT_UID)
            .child(group.id)
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

        refUser = REF_DATABASE_ROOT.child(NODE_USERS).child(group.id)
        refUser.addValueEventListener(listenerInfoToolbar)
        binding.chatButtonSendMessage.setOnClickListener {
            smoothScrollToPosition = true
            val message = binding.chatInputMessage.text.toString()
            if (message.isNotEmpty())
                sendMessageToGroup(message, TYPE_TEXT, group) {
                    binding.chatInputMessage.setText("")
                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initInfoToolbar() {
        toolbarInfo.findViewById<ShapeableImageView>(R.id.toolbar_chat_image)
            .downloadAndSetImage(group.photoUrl)
        toolbarInfo
            .findViewById<TextView>(R.id.toolbar_chat_fullname)
            .text = group.storeName
        toolbarInfo.findViewById<TextView>(R.id.toolbar_chat_status).text =
            "Участников - ${group.memberCount}"
        toolbarInfo.findViewById<TextView>(R.id.toolbar_placeholder_name).text =
            group.storeName[0].toString()
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
        replaceFragment(ChatSettingsFragment(group))
        return super.onOptionsItemSelected(item)
    }
}