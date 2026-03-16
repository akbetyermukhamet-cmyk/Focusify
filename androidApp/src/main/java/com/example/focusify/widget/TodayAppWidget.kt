/*
 * Copyright (c) 2025 Akbet Ereke
 *
 * This file is part of Tomato - a minimalist pomodoro timer for Android.
 *
 * Tomato is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tomato is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tomato.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.focusify.widget

import android.content.Context
import android.os.Build
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.GlanceTheme.colors
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import com.example.focusify.MainActivity
import com.example.focusify.R
import com.example.focusify.data.AppStatRepository
import com.example.focusify.data.Stat
import com.example.focusify.data.StatRepository
import com.example.focusify.utils.millisecondsToHoursMinutes
import com.example.focusify.utils.millisecondsToMinutes
import com.example.focusify.widget.TomatoWidgetSize.Height2
import com.example.focusify.widget.TomatoWidgetSize.Width4
import com.example.focusify.widget.componenets.HorizontalStackedBarGlance
import java.time.LocalDate

class TodayAppWidget : GlanceAppWidget(), KoinComponent {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val statRepository: StatRepository = get()
        val stat = statRepository.getTodayStat().first()
            ?: Stat(LocalDate.now(), 0, 0, 0, 0, 0)

        provideContent {
            key(LocalSize.current) {
                GlanceTheme {
                    Content(stat)
                }
            }
        }
    }

    @Composable
    private fun Content(stat: Stat) {
        val context = LocalContext.current
        val size = LocalSize.current
        val scope = rememberCoroutineScope()
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = GlanceModifier
                .then(
                    if (Build.VERSION.SDK_INT >= 31) GlanceModifier.background(colors.widgetBackground)
                    else GlanceModifier.background(
                        ImageProvider(R.drawable.rounded_24dp),
                        colorFilter = ColorFilter.tint(colors.widgetBackground)
                    )
                )
                .padding(16.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            Column(GlanceModifier.fillMaxSize()) {
                Text(
                    context.getString(R.string.focus),
                    style = TextStyle(
                        color = colors.onSurface,
                        fontSize = typography.titleMedium.fontSize
                    )
                )
                Text(
                    millisecondsToHoursMinutes(
                        stat.totalFocusTime(),
                        context.getString(R.string.hours_and_minutes_format)
                    ),
                    style = TextStyle(
                        color = colors.onSurface,
                        fontSize = typography.displaySmall.fontSize,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )

                Spacer(GlanceModifier.defaultWeight())

                if (size.height >= Height2) {
                    val values = listOf(
                        stat.focusTimeQ1,
                        stat.focusTimeQ2,
                        stat.focusTimeQ3,
                        stat.focusTimeQ4
                    )
                    if (size.width >= Width4) {
                        Row {
                            values.fastForEach {
                                Text(
                                    if (it <= 60 * 60 * 1000)
                                        millisecondsToMinutes(
                                            it,
                                            context.getString(R.string.minutes_format)
                                        )
                                    else millisecondsToHoursMinutes(
                                        it,
                                        context.getString(R.string.hours_and_minutes_format)
                                    ),
                                    style = TextStyle(
                                        color = colors.onSurfaceVariant,
                                        fontSize = typography.bodyLarge.fontSize,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.glance.text.TextAlign.Center
                                    ),
                                    modifier = GlanceModifier.width(((size.width.value - 32f) / 4).dp)
                                )
                            }
                        }
                    }
                    Spacer(GlanceModifier.height(8.dp))
                    HorizontalStackedBarGlance(
                        values = values,
                        width = size.width - 32.dp
                    )
                }
            }

            if (size.width >= Width4) {
                Image(
                    provider = ImageProvider(R.drawable.refresh),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colors.onSurface),
                    modifier = GlanceModifier
                        .cornerRadius(12.dp)
                        .clickable {
                            scope.launch { this@TodayAppWidget.updateAll(context) }
                        }
                )
            }
        }
    }
}
