package com.maheswara660.filora.screen.viewer.pdf.misc

data class PdfMetadata(
    val path: String,
    val name: String,
    val size: Long,
    val lastModified: Long,
    val pages: Int,
)