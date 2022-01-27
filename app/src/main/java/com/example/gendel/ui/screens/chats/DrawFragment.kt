package com.example.gendel.ui.screens.chats

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.gendel.R
import com.example.gendel.database.getMessageKey
import com.example.gendel.database.uploadFileToStorage
import com.example.gendel.databinding.FragmentDrawBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.utilities.*
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorShape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class DrawFragment(private val dialog: CommonModel) :
    Fragment(R.layout.fragment_draw) {
    private var _binding: FragmentDrawBinding? = null
    val binding get() = _binding!!
    private lateinit var inst: Instrumentation
    private lateinit var file: File

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideKeyboard()
        APP_ACTIVITY.toolbar.visibility = View.GONE
        _binding = FragmentDrawBinding.inflate(inflater, container, false)
        DRAW_FRAGMENT = this
        APP_ACTIVITY.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        setBottomNavigationBarColor(R.color.blue_pink)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onResume() {
        super.onResume()
        binding.graffitiColorPicker.backgroundTintList =
            ContextCompat.getColorStateList(
                APP_ACTIVITY,
                R.color.pink
            )
        binding.graffitiRemoveLast.setOnClickListener {
                binding.graffitiCanvas.undo()
        }
        binding.graffitiLineWeightPicker.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            var value: Float = 20.0f
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                val layoutParams = binding.graffitiLineWeightPickerImage.layoutParams
                layoutParams.width = progress
                layoutParams.height = progress
                binding.graffitiLineWeightPickerImage.layoutParams = layoutParams
                value = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                val layoutParams = binding.graffitiLineWeightPickerImage.layoutParams
                layoutParams.width = binding.graffitiLineWeightPicker.progress
                layoutParams.height = binding.graffitiLineWeightPicker.progress
                binding.graffitiLineWeightPickerImage.layoutParams = layoutParams
                binding.graffitiLineWeightPickerImage.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                binding.graffitiCanvas.paint.strokeWidth = value
                binding.graffitiLineWeightPickerImage.visibility = View.GONE
            }

        })
        binding.graffitiClose.setOnClickListener {
            backToChat()
        }
        binding.graffitiEraser.setOnClickListener {
            binding.graffitiEraser.backgroundTintList =
                ContextCompat.getColorStateList(
                    APP_ACTIVITY,
                    R.color.violet
                )
            binding.graffitiColorPicker.backgroundTintList =
                ContextCompat.getColorStateList(
                    APP_ACTIVITY,
                    R.color.white_pink
                )
            binding.graffitiCanvas.paint.color =
                ResourcesCompat.getColor(resources, R.color.white_pink, null)
        }
        binding.graffitiColorPicker.setOnClickListener {
            binding.graffitiEraser.backgroundTintList =
                ContextCompat.getColorStateList(
                    APP_ACTIVITY,
                    R.color.white_pink
                )
            createColorPickerDialog()
        }
        binding.graffitiButtonOk.setOnClickListener {
            sendGraffitiInChat()
        }
    }

    private fun sendGraffitiInChat() {
        val bitmap = Bitmap.createBitmap(binding.graffitiCanvas.extraBitmap)
        val messageKey = getMessageKey(dialog.id)
        file = File(APP_ACTIVITY.filesDir, messageKey)
        file.writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 85)
        file.createNewFile()
        val uri = Uri.fromFile(file)
        uploadFileToStorage(
            uri,
            messageKey,
            dialog,
            TYPE_MESSAGE_IMAGE,
            "Графити"
        )
        file.delete()
        backToChat()
    }

    private fun backToChat() {
        APP_ACTIVITY.toolbar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            inst = Instrumentation()
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ResourceType")
    private fun createColorPickerDialog() {
        ColorPickerDialog.newBuilder()
            .setColor(ContextCompat.getColor(APP_ACTIVITY, R.color.pink))
            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
            .setAllowCustom(true)
            .setAllowPresets(true)
            .setColorShape(ColorShape.CIRCLE)
            .setDialogId(1)
            .show(APP_ACTIVITY)
    }
}