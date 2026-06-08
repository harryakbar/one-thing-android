package dev.harryakbar.onething.widget

import android.content.Context
import android.content.Intent
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

class OneThingWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getInstance(context)
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val task = db.dailyTaskDao().getByDate(today).firstOrNull()

        provideContent {
            val bgColor = ColorProvider(android.graphics.Color.parseColor("#0D0D0D"))
            val accentColor = ColorProvider(android.graphics.Color.parseColor("#E8A838"))
            val textPrimary = ColorProvider(android.graphics.Color.parseColor("#FAFAFA"))
            val textSecondary = ColorProvider(android.graphics.Color.parseColor("#888888"))
            val darkText = ColorProvider(android.graphics.Color.parseColor("#0D0D0D"))

            val openApp = actionStartActivity(Intent(context, MainActivity::class.java))

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(bgColor)
                    .cornerRadius(20)
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
                                text = "ONE THING",
                                style = TextStyle(
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Spacer(GlanceModifier.height(8.dp))
                            Text(
                                text = "What's your one thing today?",
                                style = TextStyle(
                                    color = textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            )
                            Spacer(GlanceModifier.height(10.dp))
                            Box(
                                modifier = GlanceModifier
                                    .background(accentColor)
                                    .cornerRadius(12)
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Set it now →",
                                    style = TextStyle(
                                        color = darkText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }

                    task.isCompleted -> {
                        // Completed state
                        Column(
                            modifier = GlanceModifier
                                .fillMaxSize()
                                .background(accentColor)
                                .cornerRadius(20)
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✓  Done today",
                                style = TextStyle(
                                    color = darkText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(GlanceModifier.height(6.dp))
                            Text(
                                text = task.title,
                                style = TextStyle(
                                    color = darkText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
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
                                text = "TODAY'S ONE THING",
                                style = TextStyle(
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                            Spacer(GlanceModifier.height(6.dp))
                            Text(
                                text = task.title,
                                style = TextStyle(
                                    color = textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                            Spacer(GlanceModifier.height(10.dp))
                            Text(
                                text = "Tap to complete →",
                                style = TextStyle(
                                    color = textSecondary,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
