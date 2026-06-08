package dev.harryakbar.onething.ui.today

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.harryakbar.onething.ui.theme.Accent
import dev.harryakbar.onething.ui.theme.Background
import dev.harryakbar.onething.ui.theme.TextPrimary
import dev.harryakbar.onething.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun TodayScreen(
    onTaskCompleted: (streak: Int) -> Unit,
    viewModel: TodayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Track animation trigger
    var animating by remember { mutableStateOf(false) }
    val animationScale = remember { Animatable(0f) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        if (uiState is TodayUiState.Completed && !animating) {
            // Already completed (app re-opened) — navigate immediately
            val streak = (uiState as TodayUiState.Completed).streak
            onTaskCompleted(streak)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        when (val state = uiState) {
            is TodayUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Accent
                )
            }

            is TodayUiState.Empty -> {
                EmptyState(
                    onTaskSet = { title -> viewModel.setTask(title) }
                )
            }

            is TodayUiState.TaskSet -> {
                // Completion animation overlay
                if (animating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(animationScale.value)
                            .background(Accent)
                    )
                }

                TaskSetState(
                    task = state.task,
                    onComplete = {
                        scope.launch {
                            animating = true
                            hapticFeedback(context)
                            // Animate fill from 0 to 3 (overshoots screen)
                            animationScale.animateTo(
                                targetValue = 3f,
                                animationSpec = tween(durationMillis = 600)
                            )
                            viewModel.completeTask(state.task)
                        }
                    }
                )
            }

            is TodayUiState.Completed -> {
                // Navigate in LaunchedEffect above, show amber while transitioning
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Accent)
                )
            }
        }
    }

    // Observe completion to navigate after animation
    LaunchedEffect(uiState) {
        if (uiState is TodayUiState.Completed && animating) {
            val streak = (uiState as TodayUiState.Completed).streak
            onTaskCompleted(streak)
        }
    }
}

@Composable
private fun EmptyState(onTaskSet: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's your\none thing\ntoday?",
            style = TextStyle(
                fontWeight = FontWeight.Black,
                fontSize = 48.sp,
                lineHeight = 52.sp,
                letterSpacing = (-1.5).sp,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1A1A1A))
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = TextPrimary
                ),
                cursorBrush = SolidColor(Accent),
                singleLine = false,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (text.isNotBlank()) {
                            keyboardController?.hide()
                            onTaskSet(text)
                        }
                    }
                ),
                decorationBox = { inner ->
                    if (text.isEmpty()) {
                        Text(
                            "Type your task…",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp,
                                color = TextSecondary
                            )
                        )
                    }
                    inner()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    keyboardController?.hide()
                    onTaskSet(text)
                }
            },
            enabled = text.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = Color(0xFF0D0D0D),
                disabledContainerColor = Color(0xFF333333),
                disabledContentColor = TextSecondary
            )
        ) {
            Text(
                text = "SET FOR TODAY",
                style = TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp
                )
            )
        }
    }
}

@Composable
private fun TaskSetState(
    task: dev.harryakbar.onething.data.DailyTask,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TODAY",
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 3.sp,
                    color = Accent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = task.title,
                style = TextStyle(
                    fontWeight = FontWeight.Black,
                    fontSize = 56.sp,
                    lineHeight = 60.sp,
                    letterSpacing = (-2).sp,
                    color = TextPrimary
                )
            )
        }

        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = Color(0xFF0D0D0D)
            )
        ) {
            Text(
                text = "COMPLETE",
                style = TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    letterSpacing = 3.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun hapticFeedback(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        }
    } catch (_: Exception) {
        // Vibration is best-effort
    }
}
