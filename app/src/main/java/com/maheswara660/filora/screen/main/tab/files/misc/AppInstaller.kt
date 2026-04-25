package com.maheswara660.filora.screen.main.tab.files.misc

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.R
import net.lingala.zip4j.ZipFile
import java.io.File
import java.io.FileInputStream

object AppInstaller {

    fun installApk(context: Context, apkFile: File) {
        val uri = FileProvider.getUriForFile(
            globalClass,
            "com.maheswara660.filora.provider",
            apkFile
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    suspend fun installSplitApk(context: Context, archiveFile: File) {
        if (archiveFile.extension.lowercase() == "aab") {
            globalClass.showMsg("AAB is a publishing format and cannot be installed directly. Please convert it to APKS first.")
            return
        }

        val tempDir = File(context.cacheDir, "installer_temp_${System.currentTimeMillis()}")

        tempDir.mkdirs()

        try {
            // Extract APKs
            ZipFile(archiveFile).extractAll(tempDir.absolutePath)

            val apkFiles = tempDir.listFiles { file ->
                file.extension.lowercase() == "apk"
            } ?: emptyArray()

            if (apkFiles.isEmpty()) {
                globalClass.showMsg("No APK files found in bundle.")
                return
            }

            installApks(context, apkFiles.toList())
        } catch (e: Exception) {
            globalClass.logger.logError(e)
            globalClass.showMsg("Failed to extract installer bundle.")
        }
    }

    private fun installApks(context: Context, apkFiles: List<File>) {
        val packageInstaller = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        
        try {
            val sessionId = packageInstaller.createSession(params)
            val session = packageInstaller.openSession(sessionId)

            apkFiles.forEachIndexed { index, file ->
                val inputStream = FileInputStream(file)
                val outputStream = session.openWrite("split_$index", 0, file.length())
                
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                session.fsync(outputStream)
            }

            val intent = Intent(context, InstallerReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
            )

            session.commit(pendingIntent.intentSender)
            session.close()
        } catch (e: Exception) {
            globalClass.logger.logError(e)
            globalClass.showMsg("Installation failed: ${e.message}")
        }
    }
}
