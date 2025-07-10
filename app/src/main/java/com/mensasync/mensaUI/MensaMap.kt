package com.mensasync.mensaUI

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.*
import com.mensasync.mensaData.Table
import com.mensasync.mensaData.TableType
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.clickable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import com.mensasync.mensaControl.MensaViewModel

@Composable
fun MensaMap(
    tables: List<Table>,
    viewModel: MensaViewModel,
    onTableClick: (Table) -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset: Offset by remember { mutableStateOf(Offset.Zero) }

    val gestureModifier = Modifier.pointerInput(Unit) {
        detectTransformGestures { _, pan, zoom, _ ->
            scale = (scale * zoom).coerceIn(0.2f, 2.5f)
            offset += pan
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mapArea")
            .then(gestureModifier)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
    ) {
        tables.forEach { table ->
            key(table.id to table.occupiedBy) {
                val size = when (table.type) {
                    TableType.QUADRAT -> DpSize(48.dp, 48.dp)
                    TableType.LANG -> DpSize(200.dp, 48.dp)
                    TableType.KURZ -> DpSize(48.dp, 64.dp)
                }

                val farbe = when {
                    table.occupiedBy.isEmpty() -> Color.Green
                    viewModel.username.value in table.occupiedBy -> Color.Yellow
                    viewModel.searchQuery.value in table.occupiedBy -> Color.Cyan
                    else -> Color.Red
                }
                val isHighlighted = viewModel.searchQuery.value in table.occupiedBy
                val tag = if (isHighlighted) "table_${table.id}_highlighted" else "table_${table.id}"

                Box(
                    modifier = Modifier
                        .offset(x = table.x.dp, y = table.y.dp)
                        .size(size.width, size.height)
                        .background(farbe, RoundedCornerShape(8.dp))
                        .testTag(tag)
                        .clickable { onTableClick(table) },
                    contentAlignment = Alignment.Center
                ) {
                    val namenText = if (table.occupiedBy.isEmpty()) "Frei"
                    else "Belegt"
                    Text(namenText)
                }
            }
        }
    }
}
