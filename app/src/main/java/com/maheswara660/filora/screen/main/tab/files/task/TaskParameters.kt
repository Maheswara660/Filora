package com.maheswara660.filora.screen.main.tab.files.task

import com.maheswara660.filora.screen.main.tab.files.holder.ContentHolder

interface TaskParameters

data class CopyTaskParameters(
    val destHolder: ContentHolder
) : TaskParameters

class DeleteTaskParameters : TaskParameters

data class CompressTaskParameters(
    val destPath: String
) : TaskParameters

data class RenameTaskParameters(
    val newName: String,
    val toFind: String,
    val toReplace: String,
    val useRegex: Boolean
) : TaskParameters

data class ApksMergeTaskParameters(
    val autoSign: Boolean
) : TaskParameters
