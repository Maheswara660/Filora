package com.maheswara660.filora.screen.viewer.image.misc

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.R
import com.maheswara660.filora.common.emptyString
import com.maheswara660.filora.common.toFormattedDate
import com.maheswara660.filora.common.toFormattedSize

// Data class for image information
data class ImageInfo(
    val name: String,
    val size: String,
    val dimensions: String,
    val format: String,
    val lastModified: String,
    val path: String
) {
    companion object {
        // Helper function to extract image information
        fun extractImageInfo(uri: Uri, width: String, height: String): ImageInfo {
            val file = DocumentFile.fromSingleUri(globalClass, uri)

            return ImageInfo(
                name = file?.name ?: emptyString,
                size = (file?.length() ?: 0L).toFormattedSize(),
                dimensions = if (width.isNotEmpty() && height.isNotEmpty()) "$width × $height" else globalClass.getString(
                    R.string.unknown
                ),
                format = globalClass.contentResolver.getType(uri)
                    ?.substringAfter("image/", globalClass.getString(R.string.not_available))
                    ?.uppercase()
                    ?: globalClass.getString(R.string.not_available),
                lastModified = (file?.lastModified() ?: 0L).toFormattedDate(),
                path = uri.path.toString()
            )
        }
    }
}