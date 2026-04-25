package com.maheswara660.filora.screen.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FiloraHeader(
    title: String,
    onBackClick: () -> Unit,
    onAddNewTab: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    showActions: Boolean = true,
    centerTitle: Boolean = false,
    hasNewUpdate: Boolean = false,
    customActions: @Composable RowScope.() -> Unit = {}
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = if (centerTitle) Alignment.Center else Alignment.CenterStart
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(
                    start = if (centerTitle) 0.dp else 4.dp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = if (centerTitle) TextAlign.Center else TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }



        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.widthIn(min = 48.dp) // Match back button width
        ) {
            if (showActions) {
                IconButton(onClick = onAddNewTab) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add Tab",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            customActions()

            if (showActions) {
                MoreOptionsButton(hasNewUpdate = hasNewUpdate)
            }
        }
    }
}







