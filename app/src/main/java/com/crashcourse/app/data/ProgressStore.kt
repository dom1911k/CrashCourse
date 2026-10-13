package com.crashcourse.app.data

import android.content.Context

/** Remembers best scores and the questions the user got wrong, per country. */
class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("crashcourse_progress", Context.MODE_PRIVATE)

    fun bestPercent(country: Country, category: Category): Int? =
        prefs.getInt(bestKey(country, category), -1).takeIf { it >= 0 }

    fun recordFullRun(country: Country, category: Category, correct: Int, total: Int) {
        if (total == 0) return
        val percent = correct * 100 / total
        val best = bestPercent(country, category) ?: -1
        if (percent > best) prefs.edit().putInt(bestKey(country, category), percent).apply()
    }

    fun missedIds(country: Country): Set<String> =
        prefs.getStringSet(missedKey(country), emptySet())?.toSet() ?: emptySet()

    /** A wrong answer adds the question to the review pile, a right one removes it. */
    fun recordAnswer(country: Country, questionId: String, correct: Boolean) {
        val current = missedIds(country).toMutableSet()
        val changed = if (correct) current.remove(questionId) else current.add(questionId)
        if (changed) prefs.edit().putStringSet(missedKey(country), current).apply()
    }

    fun resetCountry(country: Country) {
        prefs.edit().apply {
            remove(missedKey(country))
            Category.entries.forEach { remove(bestKey(country, it)) }
        }.apply()
    }

    private fun bestKey(country: Country, category: Category) = "best_${country.name}_${category.name}"
    private fun missedKey(country: Country) = "missed_${country.name}"
}
