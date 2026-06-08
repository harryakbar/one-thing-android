package dev.harryakbar.onething.utils

import dev.harryakbar.onething.data.DailyTask
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object StreakCalculator {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Calculate the current streak from a list of daily tasks.
     *
     * A streak is the count of consecutive days with completed tasks,
     * ending on today or yesterday (to handle the case where today's
     * task hasn't been set yet but yesterday was completed).
     *
     * @param tasks All known DailyTask records (any order).
     * @param today Reference date; defaults to LocalDate.now().
     * @return The current streak count (0 if none).
     */
    fun calculate(tasks: List<DailyTask>, today: LocalDate = LocalDate.now()): Int {
        val completedDates = tasks
            .filter { it.isCompleted }
            .mapNotNull { runCatching { LocalDate.parse(it.date, formatter) }.getOrNull() }
            .toSet()

        if (completedDates.isEmpty()) return 0

        // Determine starting anchor: today if completed, else yesterday
        val anchor = when {
            completedDates.contains(today) -> today
            completedDates.contains(today.minusDays(1)) -> today.minusDays(1)
            else -> return 0
        }

        var streak = 0
        var current = anchor
        while (completedDates.contains(current)) {
            streak++
            current = current.minusDays(1)
        }
        return streak
    }
}
