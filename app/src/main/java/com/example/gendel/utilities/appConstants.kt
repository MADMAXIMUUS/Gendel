package com.example.gendel.utilities

import com.example.gendel.MainActivity
import com.example.gendel.ui.screens.draw.DrawFragment

lateinit var APP_ACTIVITY: MainActivity
lateinit var DRAW_FRAGMENT: DrawFragment
const val TYPE_MESSAGE_IMAGE = "image"
const val TYPE_MESSAGE_TEXT = "text"
const val TYPE_MESSAGE_VOICE = "voice"
const val TYPE_MESSAGE_FIlE = "file"
const val TYPE_MESSAGE_QUIZ = "quiz"
const val TYPE_MESSAGE_QUIZ_PO = "quiz_po"

const val TYPE_CHAT = "chat"
const val TYPE_GROUP = "group"
const val TYPE_CHANNEL = "channel"

const val PICK_FILE_REQUEST_CODE = 301