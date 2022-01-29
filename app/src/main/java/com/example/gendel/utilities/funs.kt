package com.example.gendel.utilities

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.gendel.BuildConfig
import com.example.gendel.MainActivity
import com.example.gendel.R
import com.example.gendel.models.NewVersionModel
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

fun isNewVersionAvailable(function: (NewVersionModel) -> Unit) {
    function(NewVersionModel(
        id = "1",
        version = "0.7.1",
        description = "Изменения в данной версии:\n-Исправление ошибок и прочие улучшения"
    ))
}

fun downloadNewVersion(function: () -> Unit) {
    enqueueDownload {
        function()
    }
}

fun enqueueDownload(function: () -> Unit) {
    val version = "0.6.2"
    if (version.toFloat() <= BuildConfig.VERSION_NAME.toFloat()) {
        function()
    } else {
        var destination =
            APP_ACTIVITY.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
        destination += "app-debug.apk"
        val uri =
            Uri.parse("https://github.com/MADMAXIMUUS/Gendel-releases/releases/download/v$version/app-debug.apk")
        val file = File(destination)
        if (file.exists()) file.delete()
        val downloadManager =
            APP_ACTIVITY.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(uri)
        request.setMimeType("application/vnd.android.package-archive")
        request.setTitle("app-debug.apk")
        request.setDestinationUri(uri)
        showInstallOption(destination)
        downloadManager.enqueue(request)
    }
}

private fun showInstallOption(
    destination: String
) {
    val onComplete = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val contentUri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                File(destination)
            )
            val install = Intent(Intent.ACTION_VIEW)
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            install.data = contentUri
            context.startActivity(install)
            context.unregisterReceiver(this)
        }
    }
    APP_ACTIVITY.registerReceiver(
        onComplete,
        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    )
}