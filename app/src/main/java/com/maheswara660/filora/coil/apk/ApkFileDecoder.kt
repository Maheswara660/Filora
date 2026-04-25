package com.maheswara660.filora.coil.apk

import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.common.drawableToBitmap
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.apkFileType
import java.io.File

class ApkFileDecoder(
    private val source: File,
) : Decoder {
    override suspend fun decode(): DecodeResult? {
        val packageManager = globalClass.packageManager
        packageManager.getPackageArchiveInfo(source.absolutePath, 0)?.let { apkInfo ->
            apkInfo.applicationInfo?.apply {
                sourceDir = source.absolutePath
                publicSourceDir = source.absolutePath
                loadIcon(packageManager).drawableToBitmap()?.let { bitmap ->
                    return DecodeResult(bitmap.asImage(), false)
                }
            }
        }
        return null
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            val file = result.source.file().toFile()
            if (file.exists() && file.extension.lowercase() == "apk") {
                return ApkFileDecoder(file)
            }
            return null
        }
    }
}