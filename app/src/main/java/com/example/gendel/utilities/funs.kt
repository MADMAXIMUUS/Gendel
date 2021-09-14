package com.example.gendel.utilities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gendel.MainActivity
import com.example.gendel.R
import com.example.gendel.database.USER
import com.example.gendel.database.updatePhonesToDatabase
import com.example.gendel.models.CommonModel
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun replaceFragment(fragment: Fragment, addStack: Boolean = true) {
    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.data_container, fragment
            ).commit()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(
                R.id.data_container,
                fragment
            ).commit()
    }

}

fun hideKeyboard() {
    val imm: InputMethodManager = APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

fun ImageView.downloadAndSetImage(url: String, type: String) {
    when (type) {
        "contact" ->
            Picasso.get()
                .load(url)
                .fit()
                .placeholder(R.drawable.default_photo)
                .into(this)
        "main_list" ->
            Picasso.get()
                .load(url)
                .fit()
                .placeholder(R.drawable.default_photo_chat)
                .into(this)
        "group" ->
            Picasso.get()
                .load(url)
                .fit()
                .placeholder(R.drawable.default_photo_chat)
                .into(this)
        "message" -> {
            Picasso.get()
                .load(url)
                .fit()
                .noPlaceholder()
                .into(this)
        }
    }
}

fun dpInPx(unit: Int, value: Float, metrics: android.util.DisplayMetrics): Int {
    return TypedValue.applyDimension(unit, value, metrics).toInt()
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

fun getFileNameFromUri(uri: Uri): String {
    var result = ""
    val cursor = APP_ACTIVITY.contentResolver.query(uri, null, null, null, null)
    try {
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    } catch (e: Exception) {
        showToast(e.message.toString())
    } finally {
        cursor?.close()
        return result
    }
}

fun getPlurals(plurals: Int, count: Int) = APP_ACTIVITY.resources.getQuantityString(
    plurals, count, count
)

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}