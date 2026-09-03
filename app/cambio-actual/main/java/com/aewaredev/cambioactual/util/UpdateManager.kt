package com.aewaredev.cambioactual.util

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.aewaredev.cambioactual.data.model.UpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateManager @Inject constructor(
    private val application: Application
) {

    suspend fun downloadAndInstallUpdate(info: UpdateInfo, onProgress: (Boolean) -> Unit) {
        onProgress(true)
        val file = withContext(Dispatchers.IO) {
            downloadFile(info.apkUrl, "update.apk")
        }
        onProgress(false)
        if (file != null) {
            installApk(file)
        }
    }

    private fun downloadFile(urlStr: String, fileName: String): File? {
        return try {
            val updateDir = File(application.cacheDir, "updates")
            updateDir.mkdirs()
            val outputFile = File(updateDir, fileName)
            
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return null
            
            connection.inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            outputFile
        } catch (e: Exception) {
            null
        }
    }

    private fun installApk(file: File) {
        val apkUri = FileProvider.getUriForFile(application, "${application.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
    }

    fun getCurrentVersionCode(): Long {
        val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    }
}
