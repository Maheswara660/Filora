package com.maheswara660.filora.screen.preferences

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maheswara660.filora.R
import com.maheswara660.filora.base.BaseActivity
import com.maheswara660.filora.common.ui.SafeSurface
import com.maheswara660.filora.screen.preferences.ui.AppInfoContainer
import com.maheswara660.filora.screen.preferences.ui.AppearanceContainer
import com.maheswara660.filora.screen.preferences.ui.BehaviorContainer
import com.maheswara660.filora.screen.preferences.ui.FileListContainer
import com.maheswara660.filora.screen.preferences.ui.FileOperationContainer
import com.maheswara660.filora.screen.preferences.ui.RecentFilesContainer
import com.maheswara660.filora.screen.preferences.ui.SingleChoiceDialog
import com.maheswara660.filora.screen.preferences.ui.TextEditorContainer
import com.maheswara660.filora.screen.main.ui.FiloraHeader
import com.maheswara660.filora.theme.FiloraTheme

class PreferencesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkPermissions()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onPermissionGranted() {
        setContent {
            FiloraTheme {
                SafeSurface {
                    FiloraHeader(
                        title = stringResource(id = R.string.preferences),
                        onBackClick = { onBackPressedDispatcher.onBackPressed() },
                        showActions = false,
                        centerTitle = true
                    )


                    SingleChoiceDialog()

                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AppearanceContainer()
                        FileListContainer()
                        FileOperationContainer()
                        BehaviorContainer()
                        RecentFilesContainer()
                        TextEditorContainer()
                        AppInfoContainer()
                    }
                }
            }
        }
    }
}