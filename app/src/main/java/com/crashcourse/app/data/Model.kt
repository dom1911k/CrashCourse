package com.crashcourse.app.data

import com.crashcourse.app.signs.SignDraw

enum class Country(val label: String, val flag: String, val drivesOn: String) {
    GERMANY("Germany", "🇩🇪", "Drives on the right"),
    JAPAN("Japan", "🇯🇵", "Drives on the left"),
}

enum class Category(val label: String) {
    SIGNS("Street signs"),
    SCENARIOS("Traffic scenarios"),
}

/**
 * One multiple-choice question. [wrong] holds exactly three distractors; the
 * four options are shuffled every time the question is shown.
 */
data class Question(
    val id: String,
    val prompt: String,
    val correct: String,
    val wrong: List<String>,
    val explanation: String,
    /** Optional sign drawing shown above the prompt. */
    val sign: SignDraw? = null,
    /** Official name in the local language, shown after answering. */
    val localName: String? = null,
) {
    init {
        require(wrong.size == 3) { "Question $id needs exactly 3 wrong answers" }
    }
}

fun signQuestion(
    id: String,
    localName: String,
    correct: String,
    wrong: List<String>,
    explanation: String,
    sign: SignDraw,
) = Question(
    id = id,
    prompt = "What does this sign mean?",
    correct = correct,
    wrong = wrong,
    explanation = explanation,
    sign = sign,
    localName = localName,
)

object QuestionBank {
    fun questions(country: Country, category: Category): List<Question> = when (country) {
        Country.GERMANY -> when (category) {
            Category.SIGNS -> GermanySigns.all
            Category.SCENARIOS -> GermanyScenarios.all
        }
        Country.JAPAN -> when (category) {
            Category.SIGNS -> JapanSigns.all
            Category.SCENARIOS -> JapanScenarios.all
        }
    }

    fun byId(country: Country, id: String): Question? =
        Category.entries.asSequence().flatMap { questions(country, it) }.firstOrNull { it.id == id }
}
