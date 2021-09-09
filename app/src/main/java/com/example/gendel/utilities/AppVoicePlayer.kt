package com.example.gendel.utilities

import android.media.MediaPlayer
import com.example.gendel.database.getFileFromStorage
import java.io.File

class AppVoicePlayer {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var file: File

    fun play(messageKey: String, fileUrl: String, function: () -> Unit) {
        file = File(APP_ACTIVITY.filesDir, messageKey)
        if (file.exists() && file.length() > 0 && file.isFile) {
            startPlay {
                function()
            }
        } else {
            file.createNewFile()
            getFileFromStorage(file, fileUrl) {
                startPlay {
                    function()
                }
            }
        }
    }

    private fun startPlay(function: () -> Unit) {
        try {
            mediaPlayer.setDataSource(file.absolutePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                stop {
                    function()
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
            function()
        }
    }

    fun stop(function: () -> Unit) {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            function()
        } catch (e: Exception) {
            showToast(e.message.toString())
            function()
        }
    }

    fun realize() {
        mediaPlayer.release()
    }

    fun init(){
        mediaPlayer = MediaPlayer()
    }
}