package com.example.gendel.database

import android.net.Uri
import com.example.gendel.models.CommonModel
import com.example.gendel.models.UserModel
import com.example.gendel.utilities.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
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
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun sendMessage(message: String, receivingUserID: String, typeText: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun updateCurrentUsername(newUsername: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
        .setValue(newUsername)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                deleteOldUsername(newUsername)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}

fun deleteOldUsername(newUsername: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnSuccessListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            USER.username = newUsername
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun setBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO).setValue(newBio)
        .addOnSuccessListener {
            USER.bio = newBio
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun setFullnameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_NAME)
        .setValue(fullname)
        .addOnSuccessListener {
            USER.name = fullname
            //APP_ACTIVITY.appDrawer.updateHeader()
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun sendMessageAsFile(
    receivingUserID: String,
    fileUrl: String,
    messageKey: String,
    typeOfMessage: String,
    filename: String,
) {

    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeOfMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsFileForGroup(
    groupID: String,
    fileUrl: String,
    messageKey: String,
    typeOfMessage: String,
    filename: String,
) {
    val refGroup = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeOfMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refGroup/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getFileFromStorage(file: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(file)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun saveToMainList(id: String, type: String) {
    val refUser = "$NODE_MAIN_LIST/$CURRENT_UID/$id"
    val refReceived = "$NODE_MAIN_LIST/$id/$CURRENT_UID"

    val mapUser = hashMapOf<String, Any>()
    val mapReceived = hashMapOf<String, Any>()

    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = type

    mapReceived[CHILD_ID] = CURRENT_UID
    mapReceived[CHILD_TYPE] = type

    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceived] = mapReceived

    REF_DATABASE_ROOT.updateChildren(commonMap)
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

fun clearChatForGroupChat(id: String, function: () -> Unit) {
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

fun deleteChatForGroupChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID).child(id).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }
}

fun createBillAndPushToDatabase(
    storeName: String,
    endDate: String,
    cost: String,
    startDate: String,
    function: () -> Unit,
) {
    val keyBill = REF_DATABASE_ROOT.child(NODE_BILLS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_BILLS).child(keyBill)
    val mapData = hashMapOf<String, Any>()
    mapData[CHILD_ID] = keyBill
    mapData[CHILD_STORE_NAME] = storeName
    mapData[CHILD_END_DATE] = endDate
    mapData[CHILD_COST] = cost
    mapData[CHILD_START_DATE] = startDate
    mapData[CHILD_MEMBERS] = "1"
    path.updateChildren(mapData)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageToGroup(message: String, groupID: String, typeText: String, function: () -> Unit) {
    val refMessages = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"
    val messageKey = REF_DATABASE_ROOT.child(refMessages).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    REF_DATABASE_ROOT.child(refMessages).child(messageKey.toString())
        .updateChildren(mapMessage)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getMessageKey(cid: String) = REF_DATABASE_ROOT.child(NODE_MESSAGES)
    .child(CURRENT_UID).child(cid).push().key.toString()

fun getMessageKeyForGroup(cid: String) = REF_DATABASE_ROOT.child(NODE_GROUPS)
    .child(cid).child(NODE_MESSAGES).push().key.toString()

fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    receivedID: String,
    typeOfMessage: String,
    filename: String = "",
) {
    /*val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(receivedID).child(messageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) { path ->
            sendMessageAsFile(receivedID, path, messageKey, typeOfMessage, filename)
        }
    }
    saveToMainList(receivedID, TYPE_CHAT)*/
}

fun uploadFileToStorageForGroup(
    uri: Uri,
    messageKey: String,
    groupID: String,
    typeOfMessage: String,
    filename: String = "",
) {
    val path = REF_STORAGE_ROOT.child(FOLDER_GROUPS_FILE).child(groupID).child(messageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) { path ->
            sendMessageAsFileForGroup(groupID, path, messageKey, typeOfMessage, filename)
        }
    }
}

fun sendQuiz(
    groupID: String,
    title: String,
    listAnswer: List<String>,
    multi: Boolean,
    function: () -> Unit
) {
    val refMessages = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"
    val messageKey = REF_DATABASE_ROOT.child(refMessages).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    if (!multi)
        mapMessage[CHILD_TYPE] = TYPE_MESSAGE_QUIZ_PO
    else
        mapMessage[CHILD_TYPE] = TYPE_MESSAGE_QUIZ
    mapMessage[CHILD_TEXT] = title
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapAnswers = hashMapOf<String, Any>()
    repeat(listAnswer.size) {
        mapAnswers["answer $it"] = listAnswer[it]
    }
    mapMessage[CHILD_ANSWERS] = mapAnswers

    REF_DATABASE_ROOT.child(refMessages).child(messageKey.toString())
        .updateChildren(mapMessage)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

/*fun createGroupToDatabase(
    nameGroup: String,
    uri: Uri,
    billID: String,
    function: () -> Unit
) {

    val keyGroup = REF_DATABASE_ROOT.child(NODE_BILLS).child(billID).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_GROUPS).child(keyGroup)
    val pathStorage = REF_STORAGE_ROOT.child(FOLDER_GROUPS_IMAGE).child(keyGroup)

    val mapData = hashMapOf<String, Any>()
    mapData[CHILD_ID] = keyGroup
    mapData[CHILD_NAME] = nameGroup
    mapData[CHILD_PHOTO_URL] = "empty"
    val mapMembers = hashMapOf<String, Any>()
    mapMembers[CURRENT_UID] = USER_CREATOR

    mapData[NODE_MEMBERS] = mapMembers

    path.updateChildren(mapData)
        .addOnSuccessListener {

            if (uri != Uri.EMPTY) {
                putFileToStorage(uri, pathStorage) {
                    getUrlFromStorage(pathStorage) {
                        path.child(CHILD_PHOTO_URL).setValue(it)
                        addGroupsToMainList(mapData) {
                            function()
                        }
                    }
                }
            } else {
                addGroupsToMainList(mapData) {
                    function()
                }
            }

        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun addGroupsToMainList(
    mapData: HashMap<String, Any>,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {
    val path = REF_DATABASE_ROOT.child(NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()

    map[CHILD_ID] = mapData[CHILD_ID].toString()
    map[CHILD_TYPE] = TYPE_GROUP
    listContacts.forEach {
        path.child(it.id).child(map[CHILD_ID].toString()).updateChildren(map)
    }
    path.child(CURRENT_UID).child(map[CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}*/
