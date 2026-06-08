package dev.harryakbar.onething.ui.complete

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CompleteViewModel @Inject constructor() : ViewModel() {

    fun getCongratsCopy(streak: Int): String = when {
        streak == 1 -> "You started. That's everything."
        streak < 3 -> "Two days strong. Keep going."
        streak < 7 -> "You're building momentum."
        streak == 7 -> "One full week. You're consistent."
        streak < 14 -> "Double digits incoming. Don't stop."
        streak == 14 -> "Two weeks. This is a habit now."
        streak < 30 -> "You're on a serious streak."
        streak == 30 -> "Thirty days. You're unstoppable."
        else -> "Elite. Seriously."
    }
}
