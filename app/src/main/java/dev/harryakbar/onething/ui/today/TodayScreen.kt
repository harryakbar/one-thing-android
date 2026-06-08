package dev.harryakbar.onething.ui.today

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.harryakbar.onething.ui.theme.Accent
import dev.harryakbar.onething.ui.theme.Background
import dev.harryakbar.onething.ui.theme.TextPrimary
import dev.harryakbar.onething.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot

@Composable
fun TodayScreen(
    onTaskCompleted: (streak: Int) -> Unit,
    viewModel: TodayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var animating by remember { mutableStateOf(false) }

    // Ripple from button center
    val rippleRadius = remember { Animatable(0f) }
    var rippleOrigin by remember { mutableStateOf(Offset.Zero) }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        if (uiState is TodayUiState.Completed && !animating) {
            onTaskCompleted((uiState as TodayUiState.Completed).streak)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .onGloballyPositioned { screenSize = it.size }
    ) {
        when (val state = uiState) {
            is TodayUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Accent
                )
            }

            is TodayUiState.Empty -> {
                EmptyState(onTaskSet = { title -> viewModel.setTask(title) })
            }

            is TodayUiState.TaskSet -> {
                TaskSetState(
                    task = state.task,
                    onComplete = { buttonCenter ->
                        scope.launch {
                            animating = true
                            rippleOrigin = buttonCenter
                            hapticFeedback(context)
                            val maxRadius = hypot(
                                screenSize.width.toFloat(),
                                screenSize.height.toFloat()
                            )
                            rippleRadius.snapTo(0f)
                            rippleRadius.animateTo(
                                targetValue = maxRadius,
                                animationSpec = tween(
                                    durationMillis = 650,
                                    easing = FastOutSlowInEasing
                                )
                            )
                            viewModel.completeTask(state.task)
                        }
                    }
                )

                // Canvas ripple overlay drawn on top
                if (animating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                drawRipple(rippleOrigin, rippleRadius.value, Accent)
                            }
                    )
                }
            }

            is TodayUiState.Completed -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Accent)
                )
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is TodayUiState.Completed && animating) {
            onTaskCompleted((uiState as TodayUiState.Completed).streak)
        }
    }
}

private fun DrawScope.drawRipple(origin: Offset, radius: Float, color: Color) {
    drawCircle(color = color, radius = radius, center = origin)
}

@Composable
private fun EmptyState(onTaskSet: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Staggered word entrance: each word fades + slides up
    val words = listOf("What's your", "one thing", "today?")
    val wordAlphas = words.indices.map { i ->
        val anim = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(i * 120L)
            anim.animateTo(1f, animationSpec = tween(400, easing = EaseOutCubic))
        }
        anim.value
    }
    val wordOffsets = words.indices.map { i ->
        val anim = remember { Animatable(24f) }
        LaunchedEffect(Unit) {
            delay(i * 120L)
            anim.animateTo(0f, animationSpec = tween(400, easing = EaseOutCubic))
        }
        anim.value
    }

    // Input + button fade in after words
    val inputAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(480L)
        inputAlpha.animateTo(1f, animationSpec = tween(350))
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            words.forEachIndexed { i, word ->
                Text(
                    text = word,
                    style = TextStyle(
                        fontWeight = FontWeight.Black,
                        fontSize = 48.sp,
                        lineHeight = 54.sp,
                        letterSpacing = (-1.5).sp,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .alpha(wordAlphas[i])
                        .offset(y = wordOffsets[i].dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(inputAlpha.value)
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
                .height(56.dp)
                .alpha(inputAlpha.value),
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
    onComplete: (buttonCenter: Offset) -> Unit
) {
    // Title slides up + fades in
    val titleAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(40f) }
    LaunchedEffect(Unit) {
        launch {
            titleAlpha.animateTo(1f, tween(500, easing = EaseOutCubic))
        }
        launch {
            titleOffset.animateTo(0f, tween(500, easing = EaseOutCubic))
        }
    }

    // "TODAY" label fades in slightly before
    val labelAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        labelAlpha.animateTo(1f, tween(300))
    }

    // Button pulses subtly on loop
    val infiniteTransition = rememberInfiniteTransition(label = "btn_pulse")
    val btnScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_scale"
    )

    var buttonPosition by remember { mutableStateOf(Offset.Zero) }

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
                ),
                modifier = Modifier.alpha(labelAlpha.value)
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
                ),
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .offset(y = titleOffset.value.dp)
            )
        }

        Button(
            onClick = { onComplete(buttonPosition) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .onGloballyPositioned { coords ->
                    val pos = coords.positionInRoot()
                    val size = coords.size
                    buttonPosition = Offset(
                        pos.x + size.width / 2f,
                        pos.y + size.height / 2f
                    )
                },
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
    } catch (_: Exception) {}
}
