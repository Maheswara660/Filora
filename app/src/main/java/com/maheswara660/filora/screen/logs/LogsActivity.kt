package com.maheswara660.filora.screen.logs

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.maheswara660.filora.base.BaseActivity
import com.maheswara660.filora.screen.logs.ui.LogsScreen
import com.maheswara660.filora.theme.FiloraTheme

class LogsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkPermissions()
    }

    override fun onPermissionGranted() {
        setContent {
            FiloraTheme {
                LogsScreen { onBackPressedDispatcher.onBackPressed() }
            }
        }
    }
}