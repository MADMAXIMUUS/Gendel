package com.example.gendel.database

import android.net.Uri
import com.example.gendel.R
import com.example.gendel.models.CommonModel
import com.example.gendel.models.UserModel
import com.example.gendel.utilities.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    REF_DATABASE_ROOT.child(NODE_BILLS).keepSynced(true)
    REF_DATABASE_ROOT.child(NODE_CHATS).child(CURRENT_UID).keepSynced(true)
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).keepSynced(true)
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference

    FirebaseMessaging.getInstance().token.addOnCompleteListener OnCompleteListener@{
        if (!it.isSuccessful)
            return@OnCompleteListener

        TOKEN = it.result.toString()
    }
}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getUserModel()
            function()
        })
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun setFullnameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_NAME)
        .setValue(fullname)
        .addOnSuccessListener {
            USER.name = fullname
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun setEmailToDatabase(email: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_EMAIL)
        .setValue(email)
        .addOnSuccessListener {
            USER.email = email
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun sendMessageAsNotTextForGroup(
    group: CommonModel,
    fileUrl: String,
    messageKey: String,
    typeOfMessage: String,
    filename: String,
) {
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeOfMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    for (i in 0 until group.memberCount.toInt()) {
        val refMessages = "$NODE_CHATS/${group.members["member $i"]}/${group.id}/"
        REF_DATABASE_ROOT.child(refMessages).child(messageKey)
            .updateChildren(mapMessage)
            .addOnFailureListener { showToast(it.message.toString()) }
    }
}

fun getFileFromStorage(file: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(file)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun clearChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(id).child(CURRENT_UID)
                .removeValue()
                .addOnSuccessListener { function() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
}

fun deleteChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID).child(id).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }
}

fun createBillAndPushToDatabase(
    storeName: String,
    endDate: String,
    cost: String,
    startDate: String,
    tags: MutableList<String>,
    function: (keyBill: String) -> Unit,
) {
    val keyBill = REF_DATABASE_ROOT.child(NODE_BILLS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_BILLS).child(keyBill)
    val mapData = hashMapOf<String, Any>()
    mapData[CHILD_ID] = keyBill
    mapData[CHILD_STORE_NAME] = storeName
    mapData[CHILD_END_DATE] = endDate
    mapData[CHILD_COST] = cost
    mapData[CHILD_START_DATE] = startDate
    mapData[CHILD_MEMBERS_COUNT] = "1"


    val membersData = hashMapOf<String, Any>()
    membersData["member 0"] = CURRENT_UID
    mapData[CHILD_MEMBERS] = membersData

    val tagsData = hashMapOf<String, Any>()
    for (i in 0 until tags.size) {
        tagsData["tag $i"] = tags[i]
    }
    mapData[CHILD_TAGS] = tagsData

    path.updateChildren(mapData)
        .addOnSuccessListener {
            function(keyBill)
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessage(message: String, type: String, group: CommonModel, function: () -> Unit) {
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = type
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    val messageKey = REF_DATABASE_ROOT
        .child("$NODE_CHATS/$CURRENT_UID/${group.id}/")
        .push().key
    for (i in 0 until group.memberCount.toInt()) {
        val refMessages = "$NODE_CHATS/${group.members["member $i"]}/${group.id}/"
        mapMessage[CHILD_ID] = messageKey.toString()
        REF_DATABASE_ROOT.child(refMessages).child(messageKey.toString())
            .updateChildren(mapMessage)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }
    }
}

fun getMessageKey(cid: String) = REF_DATABASE_ROOT.child(NODE_MESSAGES)
    .child(CURRENT_UID).child(cid).push().key.toString()


fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    group: CommonModel,
    typeOfMessage: String,
    filename: String = "",
) {
    val path = REF_STORAGE_ROOT.child(FOLDER_GROUPS_FILE).child(group.id).child(messageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) { path ->
            sendMessageAsNotTextForGroup(group, path, messageKey, typeOfMessage, filename)
        }
    }
}

fun sendQuiz(
    group: CommonModel,
    title: String,
    listAnswer: List<String>,
    multi: Boolean,
    function: () -> Unit
) {
    val messageKey = REF_DATABASE_ROOT
        .child("/$NODE_CHATS/$CURRENT_UID/${group.id}/")
        .push().key.toString()

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    if (!multi)
        mapMessage[CHILD_TYPE] = TYPE_MESSAGE_QUIZ_PO
    else
        mapMessage[CHILD_TYPE] = TYPE_MESSAGE_QUIZ
    mapMessage[CHILD_TEXT] = title
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapAnswers = hashMapOf<String, Any>()
    repeat(listAnswer.size) {
        mapAnswers["answer $it"] = listAnswer[it]
    }
    mapMessage[CHILD_ANSWERS] = mapAnswers

    val map: HashMap<String, Any?> = hashMapOf()
    group.members.forEach {
        map["/$NODE_CHATS/${it.value}/${group.id}/$messageKey"] = mapMessage
    }
    REF_DATABASE_ROOT
        .updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun createNewAccount(name:String, email:String, password: String, function: () -> Unit) {
    AUTH.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            AUTH.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val uid = AUTH.currentUser?.uid.toString()
                    val dateMap = hashMapOf<String, Any>()
                    dateMap[CHILD_ID] = uid
                    dateMap[CHILD_NAME] = name
                    dateMap[CHILD_EMAIL] = email
                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                        .addListenerForSingleValueEvent(AppValueEventListener {
                            REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                                .updateChildren(dateMap)
                                .addOnSuccessListener {
                                    function()
                                }
                                .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.account_login_error)) }
                        })
                } else showToast(it.exception?.message.toString())
            }
        } else showToast(task.exception?.message.toString())
    }
}

fun logInAccount(email:String, password:String, function: () -> Unit) {
    AUTH.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                function()
            } else {
                showToast(APP_ACTIVITY.getString(R.string.account_login_error))
            }
        }
}

fun getReceivedName(id: String, function: (name: String) -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(id)
        .addListenerForSingleValueEvent(AppValueEventListener {
            function(it.getCommonModel().name)
        })
}

fun getBill(
    id: String,
    function: (CommonModel) -> Unit
) {
    REF_DATABASE_ROOT.child(NODE_BILLS).child(id)
        .addListenerForSingleValueEvent(AppValueEventListener {
            val model = it.getCommonModel()
            function(model)
        })
}

fun deleteMessageForSingle(groupId: String, messageId: String) {
    REF_DATABASE_ROOT
        .child(NODE_CHATS)
        .child(CURRENT_UID)
        .child(groupId)
        .child(messageId)
        .removeValue()
        .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.error_deleting_message)) }
}

fun deleteMessageForAll(group: CommonModel, messageId: String, fileUrl: String) {
    if (fileUrl == "empty") {
        val map: HashMap<String, Any?> = hashMapOf()
        group.members.forEach {
            map["/$NODE_CHATS/${it.value}/${group.id}/$messageId"] = null
        }
        REF_DATABASE_ROOT
            .updateChildren(map)
    } else {
        REF_STORAGE_ROOT
            .child(FOLDER_GROUPS_FILE)
            .child(group.id)
            .child(messageId)
            .delete()
            .addOnSuccessListener {
                val map: HashMap<String, Any?> = hashMapOf()
                group.members.forEach {
                    map["/$NODE_CHATS/${it.value}/${group.id}/$messageId"] = null
                }
                REF_DATABASE_ROOT
                    .updateChildren(map)
            }
    }

}

fun addTagToDatabase(tag: String, function: () -> Unit) {
    val id = REF_DATABASE_ROOT.child(NODE_TAGS).push().key.toString()
    REF_DATABASE_ROOT.child(NODE_TAGS).child(id).setValue(tag).addOnSuccessListener {
        function()
    }.addOnFailureListener {
        showToast(it.message.toString())
    }
}

fun getTagsFromDatabase(function: (tags: MutableList<String>) -> Unit) {
    val tags: MutableList<String> = mutableListOf()
    REF_DATABASE_ROOT.child(NODE_TAGS)
        .addListenerForSingleValueEvent(AppValueEventListener { dataSnapShot ->
            dataSnapShot.children.forEach {
                tags.add(it.value.toString())
            }
            function(tags)
        })
}

fun updateMessage(
    message: CommonModel,
    group: CommonModel,
    newMessage: String,
    function: () -> Unit
) {
    val map: HashMap<String, Any?> = hashMapOf()
    val mapMessage = hashMapOf<String, Any?>()
    mapMessage[CHILD_TEXT] = newMessage
    mapMessage[CHILD_TYPE] = message.type
    mapMessage[CHILD_FROM] = message.from
    mapMessage[CHILD_TIMESTAMP] = message.timeStamp
    mapMessage[CHILD_ID] = message.id
    when (message.type) {
        TYPE_MESSAGE_VOICE, TYPE_MESSAGE_IMAGE, TYPE_MESSAGE_FIlE ->
            mapMessage[CHILD_FILE_URL] = message.fileUrl
        else ->
            mapMessage[CHILD_FILE_URL] = null
    }
    group.members.forEach {
        map["/$NODE_CHATS/${it.value}/${group.id}/${message.id}"] = mapMessage
    }
    REF_DATABASE_ROOT
        .updateChildren(map)
        .addOnSuccessListener { function() }
}
