package com.example.pomodora.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodora.model.FocusEntry
import com.example.pomodora.repo.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatsViewModel(private val repository: StatsRepository) : ViewModel() {

    private val _allYearEntries = MutableStateFlow<List<FocusEntry>>(emptyList())

    // UI State for Heatmap
    val heatmapEntries = _allYearEntries.asStateFlow()

    // UI State for Bar Graph (Filtered: Sunday to Saturday)
    val weeklyBarEntries = _allYearEntries.map { entries ->
        val today = LocalDate.now()
        // Get Sunday of the current week
        val currentDayValue = today.dayOfWeek.value % 7
        val sunday = today.minusDays(currentDayValue.toLong())

        // Filter and ensure all 7 days exist (fill 0 for missing days)
        (0..6).map { i ->
            val targetDate = sunday.plusDays(i.toLong())
            entries.find { it.date == targetDate } ?: FocusEntry(targetDate, 0)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun fetchStats(year: String) {
        viewModelScope.launch {
            repository.getYearlyActivity(year).collect { _allYearEntries.value = it }
        }
    }
}