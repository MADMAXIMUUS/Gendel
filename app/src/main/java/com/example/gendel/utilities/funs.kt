package com.example.gendel.utilities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gendel.MainActivity
import com.example.gendel.R
import com.example.gendel.models.UpdateModel
import com.squareup.picasso.Picasso
import java.io.File
import java.security.MessageDigest
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

fun ImageView.downloadAndSetImage(url: String) {
    Picasso.get()
        .load(url)
        .fit()
        .placeholder(R.drawable.placeholder_image)
        .into(this)
}

fun dpInPx(unit: Int, value: Float, metrics: android.util.DisplayMetrics): Int {
    return TypedValue.applyDimension(unit, value, metrics).toInt()
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

/*fun getFileNameFromUri(uri: Uri): String {
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
}*/

fun getPlurals(plurals: Int, count: Int) = APP_ACTIVITY.resources.getQuantityString(
    plurals, count, count
)

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

fun setBottomNavigationBarColor(color: Int) {
    val window = APP_ACTIVITY.window
    window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.navigationBarColor = ContextCompat.getColor(APP_ACTIVITY, color)
    if (Build.VERSION.SDK_INT >= 29)
        window.isNavigationBarContrastEnforced = true
}

fun isNewVersionAvailable(function: (UpdateModel, Boolean) -> Unit) {
    function(
        UpdateModel(
            version = "0.7.1",
            description = "Исправление ошибок и прочие улучшения",
            fileUrl = "https://github.com/MADMAXIMUUS/Gendel-releases/releases/download/v0.7.1/app-debug.apk"
        ),
        false
    )
}

fun downloadNewVersion(update: UpdateModel) {
    val downloadController = DownloadController(APP_ACTIVITY, update.fileUrl)
    downloadController.enqueueDownload()
}

fun String.md5(): String {
    return hashString(this, "MD5")
}

fun String.sha256(): String = hashString(this, "SHA-256")

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}