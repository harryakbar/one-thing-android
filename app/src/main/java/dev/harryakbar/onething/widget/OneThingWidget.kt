package dev.harryakbar.onething.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
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
import androidx.glance.color.ColorProviders
import androidx.glance.appwidget.cornerRadius

class OneThingWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Read today's task directly from Room (widget runs outside Hilt graph)
        val db = AppDatabase.getInstance(context)
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val task = db.dailyTaskDao().getByDate(today).firstOrNull()

        provideContent {
            val background = ColorProvider(android.graphics.Color.parseColor("#0D0D0D"))
            val accent = ColorProvider(android.graphics.Color.parseColor("#E8A838"))
            val textPrimary = ColorProvider(android.graphics.Color.parseColor("#FAFAFA"))
            val textSecondary = ColorProvider(android.graphics.Color.parseColor("#888888"))
            val surface = ColorProvider(android.graphics.Color.parseColor("#1A1A1A"))

            val openAppAction = actionStartActivity<MainActivity>()

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(background)
                    .cornerRadius(20)
                    .clickable(openAppAction),
                contentAlignment = Alignment.Center
            ) {
                if (task == null) {
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
                                color = accent,
                                fontWeight = FontWeight.Bold,
                                fontSize = androidx.glance.unit.Sp(10f)
                            )
                        )
                        Spacer(GlanceModifier.height(8.dp))
                        Text(
                            text = "What's your\none thing today?",
                            style = TextStyle(
                                color = textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = androidx.glance.unit.Sp(18f)
                            )
                        )
                        Spacer(GlanceModifier.height(10.dp))
                        Box(
                            modifier = GlanceModifier
                                .background(accent)
                                .cornerRadius(12)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Set it now →",
                                style = TextStyle(
                                    color = ColorProvider(android.graphics.Color.parseColor("#0D0D0D")),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = androidx.glance.unit.Sp(13f)
                                )
                            )
                        }
                    }
                } else if (task.isCompleted) {
                    // Completed state — amber background
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(accent)
                            .cornerRadius(20)
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✓  Done today",
                            style = TextStyle(
                                color = ColorProvider(android.graphics.Color.parseColor("#0D0D0D")),
                                fontWeight = FontWeight.Bold,
                                fontSize = androidx.glance.unit.Sp(14f)
                            )
                        )
                        Spacer(GlanceModifier.height(6.dp))
                        Text(
                            text = task.title,
                            style = TextStyle(
                                color = ColorProvider(android.graphics.Color.parseColor("#0D0D0D")),
                                fontWeight = FontWeight.Bold,
                                fontSize = androidx.glance.unit.Sp(17f)
                            )
                        )
                    }
                } else {
                    // Task set but not completed
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S ONE THING",
                            style = TextStyle(
                                color = accent,
                                fontWeight = FontWeight.Bold,
                                fontSize = androidx.glance.unit.Sp(9f)
                            )
                        )
                        Spacer(GlanceModifier.height(6.dp))
                        Text(
                            text = task.title,
                            style = TextStyle(
                                color = textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = androidx.glance.unit.Sp(20f)
                            )
                        )
                        Spacer(GlanceModifier.height(10.dp))
                        Text(
                            text = "Tap to complete →",
                            style = TextStyle(
                                color = textSecondary,
                                fontWeight = FontWeight.Normal,
                                fontSize = androidx.glance.unit.Sp(12f)
                            )
                        )
                    }
                }
            }
        }
    }
}
