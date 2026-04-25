package com.maheswara660.filora.screen.main.tab.files.coil

import com.maheswara660.filora.screen.main.tab.files.holder.ContentHolder
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.apkFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.audioFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.imageFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.pdfFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.videoFileType

fun canUseCoil(contentHolder: ContentHolder): Boolean {
    return (contentHolder.isFile()
            && imageFileType.contains(contentHolder.extension)
            || videoFileType.contains(contentHolder.extension)
            || audioFileType.contains(contentHolder.extension)
            || contentHolder.extension == apkFileType
            || contentHolder.extension == pdfFileType)
}