package dev.harryakbar.onething.ui.complete

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.harryakbar.onething.ui.theme.Accent

@Composable
fun CompleteScreen(
    streak: Int,
    onSeeHistory: () -> Unit,
    onBackToToday: () -> Unit,
    viewModel: CompleteViewModel = hiltViewModel()
) {
    val congratsCopy = remember(streak) { viewModel.getCongratsCopy(streak) }

    // Entrance animation for the streak number
    val streakScale = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) {
        streakScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Accent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Streak number with fire emoji
                Text(
                    text = "🔥",
                    style = TextStyle(fontSize = 64.sp),
                    modifier = Modifier.scale(streakScale.value)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = streak.toString(),
                    style = TextStyle(
                        fontWeight = FontWeight.Black,
                        fontSize = 120.sp,
                        lineHeight = 120.sp,
                        letterSpacing = (-4).sp,
                        color = Color(0xFF0D0D0D)
                    ),
                    modifier = Modifier.scale(streakScale.value)
                )

                Text(
                    text = if (streak == 1) "day streak" else "day streak",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp,
                        color = Color(0xFF0D0D0D).copy(alpha = 0.6f)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = congratsCopy,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        lineHeight = 30.sp,
                        color = Color(0xFF0D0D0D),
                        textAlign = TextAlign.Center
                    )
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSeeHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D0D0D),
                        contentColor = Accent
                    )
                ) {
                    Text(
                        text = "SEE HISTORY",
                        style = TextStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            letterSpacing = 2.sp
                        )
                    )
                }

                TextButton(
                    onClick = onBackToToday,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Back to today",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFF0D0D0D).copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    }
}
