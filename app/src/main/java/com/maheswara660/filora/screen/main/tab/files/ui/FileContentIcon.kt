package com.maheswara660.filora.screen.main.tab.files.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.maheswara660.filora.common.icons.Code
import com.maheswara660.filora.common.icons.Iso
import com.maheswara660.filora.common.icons.Java
import com.maheswara660.filora.common.icons.Kotlin
import com.maheswara660.filora.common.icons.Markdown
import com.maheswara660.filora.common.icons.Pdf
import com.maheswara660.filora.common.icons.FiloraIcons
import com.maheswara660.filora.common.icons.Sql
import com.maheswara660.filora.common.icons.Vector
import com.maheswara660.filora.screen.main.tab.files.holder.ContentHolder
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.apkFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.archiveFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.audioFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.codeFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.docFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.editableFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.excelFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.fontFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.imageFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.isoFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.javaFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.kotlinFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.markdownFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.pdfFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.pptFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.filoraPrefsFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.sqlFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.vectorFileType
import com.maheswara660.filora.screen.main.tab.files.misc.FileMimeType.videoFileType

data class FileContentIcon(
    val icon: Any,
    val backgroundColor: Color? = Color(0xFF1C439B),
    val iconColor: Color? = Color.White,
)

@Composable
private fun getContentIcon(content: ContentHolder): FileContentIcon {
    if (content.isFolder) return FileContentIcon(
        icon = Icons.Rounded.Folder,
        backgroundColor = colorScheme.primaryContainer,
        iconColor = colorScheme.onPrimaryContainer
    )


    val extension = content.extension

    if (extension == filoraPrefsFileType) return FileContentIcon(Icons.Default.BuildCircle)
    if (extension == javaFileType) return FileContentIcon(FiloraIcons.Java)
    if (extension == kotlinFileType) return FileContentIcon(FiloraIcons.Kotlin)
    if (extension == markdownFileType) return FileContentIcon(FiloraIcons.Markdown)
    if (extension == isoFileType) return FileContentIcon(FiloraIcons.Iso)
    if (extension == sqlFileType) return FileContentIcon(FiloraIcons.Sql)
    if (extension == pdfFileType) return FileContentIcon(FiloraIcons.Pdf)
    if (extension == apkFileType) return FileContentIcon(Icons.Default.Android)

    if (videoFileType.contains(extension)) return FileContentIcon(Icons.Default.Videocam)
    if (imageFileType.contains(extension)) return FileContentIcon(Icons.Default.Image)
    if (docFileType.contains(extension)) return FileContentIcon(Icons.Default.Description)
    if (excelFileType.contains(extension)) return FileContentIcon(Icons.Default.TableChart)
    if (pptFileType.contains(extension)) return FileContentIcon(Icons.Default.Slideshow)
    if (fontFileType.contains(extension)) return FileContentIcon(Icons.Default.TextFields)
    if (vectorFileType.contains(extension)) return FileContentIcon(FiloraIcons.Vector)
    if (audioFileType.contains(extension)) return FileContentIcon(Icons.Default.Audiotrack)
    if (codeFileType.contains(extension)) return FileContentIcon(FiloraIcons.Code)
    if (editableFileType.contains(extension)) return FileContentIcon(Icons.Default.Description)
    if (archiveFileType.contains(extension)) return FileContentIcon(Icons.Default.Archive)

    return FileContentIcon(Icons.Default.QuestionMark)
}

@Composable
fun FileContentIcon(item: ContentHolder) {
    val fileContentIcon = getContentIcon(item)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = fileContentIcon.backgroundColor ?: colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val fileIcon = fileContentIcon.icon
        if (fileIcon is ImageVector) {
            Icon(
                modifier = Modifier.fillMaxSize(0.7f),
                imageVector = fileIcon,
                contentDescription = null,
                tint = fileContentIcon.iconColor ?: Color.Unspecified
            )
        }
    }
}