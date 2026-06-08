package dev.harryakbar.onething.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.harryakbar.onething.MainActivity
import dev.harryakbar.onething.data.AppDatabase
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Color(Long) overload used — 0xFF... hex literals exceed Int so Kotlin infers Long
private val BgColor       = ColorProvider(Color(0xFF0D0D0D))
private val AccentColor   = ColorProvider(Color(0xFFE8A838))
private val TextPrimary   = ColorProvider(Color(0xFFFAFAFA))
private val TextSecondary = ColorProvider(Color(0xFF888888))
private val DarkText      = ColorProvider(Color(0xFF0D0D0D))

class OneThingWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val task = try {
            val db = AppDatabase.getInstance(context)
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            db.dailyTaskDao().getByDate(today).firstOrNull()
        } catch (_: Exception) {
            null
        }

        val openApp = actionStartActivity(Intent(context, MainActivity::class.java))

        provideContent {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(BgColor)
                    .cornerRadius(20.dp)
                    .clickable(openApp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    task == null -> {
                        // Empty state
                        Column(
                            modifier = GlanceModifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "ONE THING",
                                style = TextStyle(color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            )
                            Spacer(GlanceModifier.height(8.dp))
                            Text(
                                "What's your one thing today?",
                                style = TextStyle(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            )
                            Spacer(GlanceModifier.height(12.dp))
                            Box(
                                modifier = GlanceModifier
                                    .background(AccentColor)
                                    .cornerRadius(12.dp)
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    "Set it now →",
                                    style = TextStyle(color = DarkText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                )
                            }
                        }
                    }

                    task.isCompleted -> {
                        // Completed — amber background
                        Column(
                            modifier = GlanceModifier
                                .fillMaxSize()
                                .background(AccentColor)
                                .cornerRadius(20.dp)
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "✓  Done today",
                                style = TextStyle(color = DarkText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            )
                            Spacer(GlanceModifier.height(6.dp))
                            Text(
                                task.title,
                                style = TextStyle(color = DarkText, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            )
                        }
                    }

                    else -> {
                        // Task set, not yet completed
                        Column(
                            modifier = GlanceModifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "TODAY'S ONE THING",
                                style = TextStyle(color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            )
                            Spacer(GlanceModifier.height(6.dp))
                            Text(
                                task.title,
                                style = TextStyle(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            )
                            Spacer(GlanceModifier.height(10.dp))
                            Text(
                                "Tap to complete →",
                                style = TextStyle(color = TextSecondary, fontWeight = FontWeight.Normal, fontSize = 12.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}
