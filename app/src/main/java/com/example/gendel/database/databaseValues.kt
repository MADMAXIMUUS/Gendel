package com.example.gendel.database

import com.example.gendel.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: UserModel

const val TYPE_TEXT = "text"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_MESSAGES = "messages"
const val NODE_MAIN_LIST = "main_list"
const val NODE_GROUPS = "groups"
const val NODE_MEMBERS = "members"
const val NODE_BILLS = "bills"

const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_FILES = "files"
const val FOLDER_GROUPS_IMAGE = "groups_image"
const val FOLDER_GROUPS_FILE = "groups_file"

const val CHILD_ID = "id"
const val CHILD_EMAIL = "email"
const val CHILD_PASSWORD = "password"
const val CHILD_USERNAME = "username"
const val CHILD_NAME = "name"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"
const val CHILD_END_DATE = "endDate"
const val CHILD_COST = "cost"
const val CHILD_STORE_NAME="storeName"

const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timeStamp"
const val CHILD_FILE_URL = "fileUrl"
const val CHILD_ANSWERS = "answers"

const val USER_CREATOR = "creator"
const val USER_ADMIN = "admin"
const val USER_MEMBER = "members"