package dev.harryakbar.onething.ui.complete

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.harryakbar.onething.ui.theme.Accent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val x: Float,       // 0..1 normalized
    val vy: Float,      // normalized velocity
    val vx: Float,
    val size: Float,
    val color: Color,
    val rotation: Float,
    val shape: Int      // 0=circle, 1=rect
)

@Composable
fun CompleteScreen(
    streak: Int,
    onSeeHistory: () -> Unit,
    onBackToToday: () -> Unit,
    viewModel: CompleteViewModel = hiltViewModel()
) {
    val congratsCopy = remember(streak) { viewModel.getCongratsCopy(streak) }

    // --- Confetti system ---
    val particles = remember {
        List(60) {
            Particle(
                x = Random.nextFloat(),
                vy = 0.15f + Random.nextFloat() * 0.35f,
                vx = (Random.nextFloat() - 0.5f) * 0.08f,
                size = 6f + Random.nextFloat() * 10f,
                color = listOf(
                    Color(0xFF0D0D0D), Color.White,
                    Color(0xFFFFD700), Color(0xFFFF6B35),
                    Color(0xFF00D4AA)
                ).random(),
                rotation = Random.nextFloat() * 360f,
                shape = Random.nextInt(2)
            )
        }
    }
    val confettiProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        confettiProgress.animateTo(
            1f,
            animationSpec = tween(3000, easing = LinearEasing)
        )
    }

    // --- Streak count-up ---
    var displayedStreak by remember { mutableIntStateOf(0) }
    LaunchedEffect(streak) {
        delay(300L)
        val steps = streak.coerceAtMost(30)
        val stepDelay = if (streak <= 10) 80L else (800L / steps)
        for (i in 1..streak) {
            displayedStreak = i
            delay(stepDelay)
        }
    }

    // --- Fire emoji pulse (infinite) ---
    val infiniteTransition = rememberInfiniteTransition(label = "fire")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_scale"
    )

    // --- Staggered element entrance ---
    val fireAlpha = remember { Animatable(0f) }
    val streakAlpha = remember { Animatable(0f) }
    val streakSlide = remember { Animatable(30f) }
    val copyAlpha = remember { Animatable(0f) }
    val btnsAlpha = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            delay(100); fireAlpha.animateTo(1f, tween(300))
        }
        scope.launch {
            delay(250)
            launch { streakAlpha.animateTo(1f, tween(400, easing = EaseOutBack)) }
            launch { streakSlide.animateTo(0f, tween(400, easing = EaseOutBack)) }
        }
        scope.launch {
            delay(500); copyAlpha.animateTo(1f, tween(350))
        }
        scope.launch {
            delay(700); btnsAlpha.animateTo(1f, tween(350))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Accent)
    ) {
        // Confetti canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val t = confettiProgress.value
            particles.forEach { p ->
                val cx = (p.x + p.vx * t * 2f).let { it - it.toInt() } * size.width
                val cy = (p.vy * t) * size.height * 2.5f - size.height * 0.1f
                if (cy > size.height + 20f) return@forEach

                if (p.shape == 0) {
                    drawCircle(color = p.color.copy(alpha = (1f - t * 0.5f).coerceIn(0f, 1f)), radius = p.size, center = Offset(cx, cy))
                } else {
                    drawRect(
                        color = p.color.copy(alpha = (1f - t * 0.5f).coerceIn(0f, 1f)),
                        topLeft = Offset(cx - p.size / 2, cy - p.size / 2),
                        size = androidx.compose.ui.geometry.Size(p.size, p.size * 0.6f)
                    )
                }
            }
        }

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
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🔥",
                    style = TextStyle(fontSize = 64.sp),
                    modifier = Modifier
                        .alpha(fireAlpha.value)
                        .scale(fireScale)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = displayedStreak.toString(),
                    style = TextStyle(
                        fontWeight = FontWeight.Black,
                        fontSize = 128.sp,
                        lineHeight = 128.sp,
                        letterSpacing = (-4).sp,
                        color = Color(0xFF0D0D0D)
                    ),
                    modifier = Modifier
                        .alpha(streakAlpha.value)
                        .offset(y = streakSlide.value.dp)
                )

                Text(
                    text = if (streak == 1) "day streak" else "day streak",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp,
                        color = Color(0xFF0D0D0D).copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.alpha(streakAlpha.value)
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
                    ),
                    modifier = Modifier.alpha(copyAlpha.value)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.alpha(btnsAlpha.value)
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
