package dev.harryakbar.onething

import dev.harryakbar.onething.data.DailyTask
import dev.harryakbar.onething.utils.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    private val today = LocalDate.of(2024, 3, 15)

    private fun task(date: LocalDate, completed: Boolean = true) = DailyTask(
        date = date.toString(),
        title = "test",
        isCompleted = completed
    )

    @Test
    fun `empty list returns zero streak`() {
        assertEquals(0, StreakCalculator.calculate(emptyList(), today))
    }

    @Test
    fun `single completed task today returns streak of 1`() {
        val tasks = listOf(task(today))
        assertEquals(1, StreakCalculator.calculate(tasks, today))
    }

    @Test
    fun `single completed task yesterday returns streak of 1`() {
        val tasks = listOf(task(today.minusDays(1)))
        assertEquals(1, StreakCalculator.calculate(tasks, today))
    }

    @Test
    fun `completed task two days ago with no yesterday returns zero`() {
        val tasks = listOf(task(today.minusDays(2)))
        assertEquals(0, StreakCalculator.calculate(tasks, today))
    }

    @Test
    fun `active streak of 5 ending today`() {
        val tasks = (0L..4L).map { task(today.minusDays(it)) }
        assertEquals(5, StreakCalculator.calculate(tasks, today))
    }

    @Test
    fun `active streak of 5 ending yesterday`() {
        val tasks = (1L..5L).map { task(today.minusDays(it)) }
        assertEquals(5, StreakCalculator.calculate(tasks, today))
    }

    @Test
    fun `broken streak - gap in middle`() {
        // Completed today and 5 days ago, but gap in between
        val tasks = listOf(
            task(today),
            task(today.minusDays(5))
        )
        // Streak anchors at today but yesterday is not completed, so streak = 1
        assertEquals(1, StreakCalculator.calculate(tasks, today))
    }

    @Test
    fun `incomplete tasks do not count toward streak`() {
        val tasks = listOf(
            task(today, completed = true),
            task(today.minusDays(1), completed = false),
            task(today.minusDays(2), completed = true)
        )
        // Gap at yesterday breaks the streak
        assertEquals(1, StreakCalculator.calculate(tasks, today))
    }

    @Test
    fun `long consecutive streak`() {
        val tasks = (0L..29L).map { task(today.minusDays(it)) }
        assertEquals(30, StreakCalculator.calculate(tasks, today))
    }

    @Test
    fun `no tasks returns zero`() {
        assertEquals(0, StreakCalculator.calculate(listOf(), today))
    }

    @Test
    fun `tasks exist but none completed returns zero`() {
        val tasks = listOf(
            task(today, completed = false),
            task(today.minusDays(1), completed = false)
        )
        assertEquals(0, StreakCalculator.calculate(tasks, today))
    }

    @Test
    fun `streak calculation ignores future dates`() {
        val tasks = listOf(
            task(today),
            task(today.plusDays(1)) // future — should not affect streak
        )
        assertEquals(1, StreakCalculator.calculate(tasks, today))
    }
}
