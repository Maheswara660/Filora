package com.maheswara660.filora.screen.preferences.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.R
import com.maheswara660.filora.common.emptyString
import com.maheswara660.filora.common.showMsg
import com.maheswara660.filora.screen.preferences.misc.exportPreferences
import java.io.File
import android.content.Intent
import com.maheswara660.filora.screen.about.AboutActivity
import kotlinx.coroutines.launch


@Composable
fun AppInfoContainer() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Container(title = stringResource(R.string.other)) {
        PreferenceItem(
            label = stringResource(R.string.export_preferences),
            supportingText = stringResource(R.string.export_preferences_desc),
            icon = Icons.Rounded.Upload,
            onClick = {
                scope.launch {
                    val data = exportPreferences()
                    File(globalClass.appFiles.file, "preferences.filoraPrefs").writeText(data)
                    showMsg(globalClass.getString(R.string.exported_filora_preferences))
                }
            }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            thickness = 3.dp
        )

        PreferenceItem(
            label = stringResource(R.string.about),
            supportingText = emptyString,
            icon = Icons.Rounded.Info,
            onClick = {
                context.startActivity(Intent(context, AboutActivity::class.java))
            }
        )
    }
}