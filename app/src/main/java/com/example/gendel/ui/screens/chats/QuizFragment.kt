package com.example.gendel.ui.screens.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.sendQuiz
import com.example.gendel.databinding.FragmentQuizBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.utilities.*

class QuizFragment(private val dialog: CommonModel) : Fragment(R.layout.fragment_quiz) {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        APP_ACTIVITY.toolbar.visibility = View.VISIBLE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_search).visibility = View.GONE
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.settings_exit).visibility = View.GONE
        APP_ACTIVITY.toolbar.title = getString(R.string.new_quiz)
        APP_ACTIVITY.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setBottomNavigationBarColor(R.color.white_pink)
        initFunction()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFunction() {
        binding.createQuizAnswers.setOnClickListener {
            deleteAnswer(0)
        }
        binding.createQuizAddAnswerBigButton
            .setOnClickListener {
                if (binding.createQuizAnswers.childCount < 10) {
                    val answerView =
                        layoutInflater.inflate(R.layout.create_quiz_answer_element, null)

                    answerView.tag = "answer ${binding.createQuizAnswers.childCount}"
                    answerView.findViewById<ImageView>(R.id.create_quiz_answer_delete)
                        .setOnClickListener {
                            val index = binding.createQuizAnswers.indexOfChild(answerView)
                            deleteAnswer(index)
                        }
                    binding.createQuizAnswers.addView(answerView)
                    binding.createQuizAddAnswerCount.text =
                        getPlurals(
                            R.plurals.count_answers,
                            10 - binding.createQuizAnswers.childCount
                        )
                } else {
                    showToast(getString(R.string.max_answers))
                }
            }
        binding.createQuizCreateButton
            .setOnClickListener {
                if (binding.createQuizTitle.text.isEmpty())
                    showToast(getString(R.string.create_quiz_title_hint))
                else if (binding.createQuizAnswers.childCount < 1)
                    showToast(getString(R.string.add_answer_hint))
                else
                    createQuiz(binding.createQuizAnswers)

            }
        binding.createQuizChooseAnswerType
            .setOnClickListener {
                binding.createQuizSettingsMultiSwitch.isChecked =
                    !binding.createQuizSettingsMultiSwitch.isChecked
            }
    }

    private fun createQuiz(answers: LinearLayout) {
        val title = binding.createQuizTitle.text.toString()

        val isPeekMulti: Boolean = binding.createQuizSettingsMultiSwitch.isChecked

        val listAnswer = mutableListOf<String>()

        answers.children.forEach {
            if (it.findViewById<EditText>(R.id.create_quiz_answer_text).text.toString() != "")
                listAnswer.add(it.findViewById<EditText>(R.id.create_quiz_answer_text).text.toString())
        }
        if (listAnswer.size == 0) {
            showToast(getString(R.string.add_answer_hint))
        } else
            sendQuiz(dialog, title, listAnswer, isPeekMulti) {
                replaceFragment(GroupChatFragment(dialog))
            }
    }

    private fun deleteAnswer(id: Int) {
        binding.createQuizAnswers.removeViewAt(id)
        binding.createQuizAddAnswerCount.text =
            getPlurals(
                R.plurals.count_answers,
                10 - binding.createQuizAnswers.childCount
            )
    }
}