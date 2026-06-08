package dev.harryakbar.onething.ui.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.harryakbar.onething.ui.theme.Accent
import dev.harryakbar.onething.ui.theme.Background
import dev.harryakbar.onething.ui.theme.OutlineColor
import dev.harryakbar.onething.ui.theme.TextPrimary
import dev.harryakbar.onething.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(
    onBack: () -> Unit,
    viewModel: StreakViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        TopAppBar(
            title = {
                Text(
                    "History",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                )
            },
            navigationIcon = {
                TextButton(onClick = onBack) {
                    Text(
                        text = "←",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = TextPrimary
                        )
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Background
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Current streak display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                Text(
                    text = "CURRENT STREAK",
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 3.sp,
                        color = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.currentStreak.toString(),
                        style = TextStyle(
                            fontWeight = FontWeight.Black,
                            fontSize = 80.sp,
                            letterSpacing = (-3).sp,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "🔥",
                        style = TextStyle(fontSize = 48.sp)
                    )
                }
                Text(
                    text = if (uiState.currentStreak == 1) "day" else "days",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                )
            }

            HorizontalDivider(color = OutlineColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            // Section header
            Text(
                text = "LAST 30 DAYS",
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 3.sp,
                    color = TextSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )

            // 30-day calendar grid (6 rows x 5 cols for 30 items, but we use 7 columns for weekdays)
            // Just display as a plain 6x5 grid of 30 items
            CalendarGrid(days = uiState.last30Days)

            Spacer(modifier = Modifier.height(32.dp))

            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(
                    color = Accent,
                    label = "Completed"
                )
                LegendItem(
                    color = Color.Transparent,
                    label = "Missed",
                    bordered = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CalendarGrid(days: List<DayStatus>) {
    val columns = 7
    val rows = (days.size + columns - 1) / columns

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Day-of-week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { label ->
                Text(
                    text = label,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        color = TextSecondary
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Determine leading empty cells based on first day's day-of-week
        val firstDay = days.firstOrNull()
        // Monday = 1, Sunday = 7 in ISO
        val leadingEmpties = if (firstDay != null) {
            (firstDay.date.dayOfWeek.value - 1) % 7
        } else 0

        val totalCells = leadingEmpties + days.size
        val totalRows = (totalCells + columns - 1) / columns

        for (row in 0 until totalRows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (col in 0 until columns) {
                    val cellIndex = row * columns + col
                    val dayIndex = cellIndex - leadingEmpties
                    val modifier = Modifier.weight(1f)

                    if (dayIndex < 0 || dayIndex >= days.size) {
                        // Empty spacer cell
                        Spacer(modifier = modifier.aspectRatio(1f).padding(4.dp))
                    } else {
                        DayCell(
                            dayStatus = days[dayIndex],
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    dayStatus: DayStatus,
    modifier: Modifier = Modifier
) {
    val monthDayFormatter = DateTimeFormatter.ofPattern("d")
    val dayLabel = dayStatus.date.format(monthDayFormatter)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .then(
                    if (dayStatus.isCompleted) {
                        Modifier.background(Accent)
                    } else {
                        Modifier.border(
                            width = if (dayStatus.isToday) 2.dp else 1.dp,
                            color = if (dayStatus.isToday) Accent else OutlineColor,
                            shape = CircleShape
                        )
                    }
                )
        ) {
            Text(
                text = dayLabel,
                style = TextStyle(
                    fontWeight = if (dayStatus.isCompleted) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp,
                    color = if (dayStatus.isCompleted) Color(0xFF0D0D0D) else TextSecondary,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    bordered: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .then(
                    if (bordered) {
                        Modifier.border(1.dp, OutlineColor, CircleShape)
                    } else {
                        Modifier.background(color)
                    }
                )
        )
        Text(
            text = label,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = TextSecondary
            )
        )
    }
}
