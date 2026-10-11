package com.example.taskmaster.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.taskmaster.data.local.DailyProgress
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CompletionCalendarView(
    history: List<DailyProgress>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Completion History",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (history.isEmpty()) {
            Text(
                text = "No history recorded yet. Complete tasks to build your streak!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(7), // 7 days a week layout
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.height(180.dp)
            ) {
                items(history) { day ->
                    val fraction = if (day.totalCount > 0) {
                        day.completedCount.toFloat() / day.totalCount.toFloat()
                    } else {
                        0f
                    }

                    // Dynamic color coding based on completion rate
                    val cellColor = when {
                        fraction == 1f -> MaterialTheme.colorScheme.primary          // 100% Done
                        fraction > 0f -> MaterialTheme.colorScheme.primaryContainer   // Partial
                        else -> MaterialTheme.colorScheme.surfaceVariant              // 0% / Empty
                    }

                    val dateString = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(day.dateMillis))

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(cellColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dateString.substringAfter(" "), // Show day number
                            style = MaterialTheme.typography.labelSmall,
                            color = if (fraction == 1f) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}