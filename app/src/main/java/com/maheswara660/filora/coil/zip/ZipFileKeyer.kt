package com.maheswara660.filora.coil.zip

import coil3.key.Keyer
import coil3.request.Options
import com.maheswara660.filora.screen.main.tab.files.holder.ZipFileHolder

class ZipFileKeyer : Keyer<ZipFileHolder> {
    override fun key(data: ZipFileHolder, options: Options): String {
        return "zip_file:${data.zipTree.source.file.absolutePath}:${data.uniquePath}"
    }
}