package com.maheswara660.filora.screen.preferences.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.R
import com.maheswara660.filora.common.emptyString
import com.maheswara660.filora.screen.preferences.constant.ThemePreference
import androidx.compose.material.icons.rounded.Palette
import com.maheswara660.filora.theme.CustomAccents

@Composable
fun AppearanceContainer() {
    val prefs = globalClass.preferencesManager

    Container(title = stringResource(R.string.appearance)) {
        PreferenceItem(
            label = stringResource(R.string.theme),
            supportingText = when (prefs.theme) {
                ThemePreference.LIGHT.ordinal -> stringResource(R.string.light)
                ThemePreference.DARK.ordinal -> stringResource(R.string.dark)
                ThemePreference.AMOLED.ordinal -> "Amoled Black"
                else -> stringResource(R.string.follow_system)
            },
            icon = Icons.Rounded.Nightlight,
            onClick = {
                prefs.singleChoiceDialog.show(
                    title = globalClass.getString(R.string.theme),
                    description = globalClass.getString(R.string.select_theme_preference),
                    choices = listOf(
                        globalClass.getString(R.string.light),
                        globalClass.getString(R.string.dark),
                        "Amoled Black",
                        globalClass.getString(R.string.follow_system)
                    ),
                    selectedChoice = prefs.theme,
                    onSelect = { prefs.theme = it }
                )
            }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            thickness = 3.dp
        )

        val accentChoices = mutableListOf("Default (Teal)", "Material You")
        accentChoices.addAll(CustomAccents.map { it.name })

        PreferenceItem(
            label = "Accent Color",
            supportingText = accentChoices.getOrNull(prefs.accentColor) ?: "Default (Teal)",
            icon = Icons.Rounded.Palette,
            onClick = {
                prefs.singleChoiceDialog.show(
                    title = "Accent Color",
                    description = "Choose your favorite accent color",
                    choices = accentChoices,
                    selectedChoice = prefs.accentColor,
                    onSelect = { prefs.accentColor = it }
                )
            }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            thickness = 3.dp
        )

        PreferenceItem(
            label = stringResource(R.string.show_bottom_bar_labels),
            supportingText = emptyString,
            icon = Icons.AutoMirrored.Rounded.Label,
            switchState = prefs.showBottomBarLabels,
            onSwitchChange = { prefs.showBottomBarLabels = it }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            thickness = 3.dp
        )

        val commonDateFormat = arrayListOf(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MM-yyyy",
            "MMM dd, yyyy HH:mm:ss",
            "MMM dd, yyyy",
            "MMMM dd, yyyy",
            "EEE, MMM dd, yyyy"
        )

        PreferenceItem(
            label = stringResource(R.string.date_time_format),
            supportingText = prefs.dateTimeFormat,
            icon = Icons.Rounded.CalendarToday,
            onClick = {
                prefs.singleChoiceDialog.show(
                    title = globalClass.getString(R.string.date_time_format),
                    description = globalClass.getString(R.string.select_date_format),
                    choices = commonDateFormat,
                    selectedChoice = commonDateFormat.indexOf(prefs.dateTimeFormat),
                    onSelect = { prefs.dateTimeFormat = commonDateFormat[it] }
                )
            }
        )
    }
}