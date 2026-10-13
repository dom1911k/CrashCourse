package com.crashcourse.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crashcourse.app.data.Category
import com.crashcourse.app.data.Country
import com.crashcourse.app.data.ProgressStore
import com.crashcourse.app.data.Question
import com.crashcourse.app.data.QuestionBank
import com.crashcourse.app.signs.SignDraw
import com.crashcourse.app.signs.SignImage
import com.crashcourse.app.scenes.SceneImage
import androidx.compose.ui.draw.clip

private const val QUICK_ROUND = 10

private sealed interface Screen {
    data object CountryPick : Screen
    data class Menu(val country: Country) : Screen
    data class Browse(val country: Country) : Screen
    data class Guide(val country: Country) : Screen

    class Quiz(
        val country: Country,
        val title: String,
        val questions: List<Question>,
        /** Set when the run covers a whole category, so the best score is recorded. */
        val fullRun: Category?,
    ) : Screen

    class Results(
        val country: Country,
        val title: String,
        val answers: List<Answer>,
        val fullRun: Category?,
    ) : Screen
}

private data class Answer(val question: Question, val chosen: String) {
    val isCorrect get() = chosen == question.correct
}

@Composable
fun CrashCourseApp(store: ProgressStore) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.CountryPick) }
    val current = backStack.last()
    val scope = rememberCoroutineScope()
    val updates = remember { UpdateController(scope) }
    LaunchedEffect(Unit) { updates.check(silent = true) }

    fun push(s: Screen) = backStack.add(s)
    fun pop() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }
    fun replace(s: Screen) {
        backStack.removeAt(backStack.lastIndex); backStack.add(s)
    }

    BackHandler(enabled = backStack.size > 1) { pop() }

    // Re-read progress whenever the screen changes so the menu shows fresh numbers.
    key(current) {
        when (val s = current) {
            Screen.CountryPick -> CountryPickScreen(updates) { push(Screen.Menu(it)) }

            is Screen.Menu -> MenuScreen(
                country = s.country,
                store = store,
                onBack = ::pop,
                onStart = { category, quick ->
                    val all = QuestionBank.questions(s.country, category)
                    val qs = if (quick) all.shuffled().take(QUICK_ROUND) else all.shuffled()
                    val title = if (quick) "${category.label} · quick $QUICK_ROUND" else category.label
                    push(Screen.Quiz(s.country, title, qs, if (quick) null else category))
                },
                onReview = {
                    val qs = store.missedIds(s.country).mapNotNull { QuestionBank.byId(s.country, it) }.shuffled()
                    if (qs.isNotEmpty()) push(Screen.Quiz(s.country, "Review mistakes", qs, null))
                },
                onBrowse = { push(Screen.Browse(s.country)) },
                onGuide = { push(Screen.Guide(s.country)) },
            )

            is Screen.Browse -> BrowseScreen(s.country, onBack = ::pop)
            is Screen.Guide -> GuideScreen(s.country, onBack = ::pop)

            is Screen.Quiz -> QuizScreen(
                screen = s,
                onAnswer = { q, ok -> store.recordAnswer(s.country, q.id, ok) },
                onQuit = ::pop,
                onFinished = { answers ->
                    s.fullRun?.let { store.recordFullRun(s.country, it, answers.count { a -> a.isCorrect }, answers.size) }
                    replace(Screen.Results(s.country, s.title, answers, s.fullRun))
                },
            )

            is Screen.Results -> ResultsScreen(
                screen = s,
                onRetryMistakes = {
                    val wrong = s.answers.filterNot { it.isCorrect }.map { it.question }.shuffled()
                    replace(Screen.Quiz(s.country, "Retry mistakes", wrong, null))
                },
                onRetryAll = {
                    replace(Screen.Quiz(s.country, s.title, s.answers.map { it.question }.shuffled(), s.fullRun))
                },
                onMenu = ::pop,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Shared bits
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScaffold(
    title: String,
    onBack: (() -> Unit)?,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, maxLines = 1) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
            )
        },
        content = content,
    )
}

/** Signs are always shown on a light plate so they look right in dark mode too. */
@Composable
private fun SignPlate(draw: SignDraw, modifier: Modifier = Modifier, padding: Int = 12) {
    Box(
        modifier
            .background(Color(0xFFF1F3F6), RoundedCornerShape(16.dp))
            .padding(padding.dp),
        contentAlignment = Alignment.Center,
    ) {
        SignImage(draw, Modifier.fillMaxSize())
    }
}

// ---------------------------------------------------------------------------
// Country selection
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryPickScreen(updates: UpdateController, onPick: (Country) -> Unit) {
    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))
            Text("CrashCourse", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Refresh your road signs and traffic rules before getting back behind the wheel.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))
            Text("Where are you driving?", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Country.entries.forEach { country ->
                Card(
                    onClick = { onPick(country) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(country.flag, fontSize = 48.sp)
                        Spacer(Modifier.width(20.dp))
                        Column {
                            Text(country.label, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${QuestionBank.questions(country, Category.SIGNS).size} signs · " +
                                    "${QuestionBank.questions(country, Category.SCENARIOS).size} scenarios",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(country.drivesOn, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            UpdateSection(updates)
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Country menu
// ---------------------------------------------------------------------------

@Composable
private fun MenuScreen(
    country: Country,
    store: ProgressStore,
    onBack: () -> Unit,
    onStart: (Category, quick: Boolean) -> Unit,
    onReview: () -> Unit,
    onBrowse: () -> Unit,
    onGuide: () -> Unit,
) {
    var refresh by remember { mutableIntStateOf(0) }
    var confirmReset by remember { mutableStateOf(false) }
    val missed = remember(refresh) { store.missedIds(country).size }

    AppScaffold("${country.flag}  ${country.label}", onBack) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Category.entries.forEach { category ->
                val total = QuestionBank.questions(country, category).size
                val best = remember(refresh) { store.bestPercent(country, category) }
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(category.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Text(
                            "$total questions · " + (best?.let { "best full run: $it%" } ?: "not attempted yet"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onStart(category, false) }, modifier = Modifier.weight(1f)) {
                                Text("All $total")
                            }
                            FilledTonalButton(onClick = { onStart(category, true) }, modifier = Modifier.weight(1f)) {
                                Text("Quick $QUICK_ROUND")
                            }
                        }
                    }
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Review mistakes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(
                        if (missed == 0) "Nothing to review – questions you get wrong will show up here."
                        else "$missed question${if (missed == 1) "" else "s"} to practise. Answer correctly to clear them.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onReview, enabled = missed > 0, modifier = Modifier.fillMaxWidth()) {
                        Text("Practise mistakes")
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onGuide, modifier = Modifier.weight(1f)) {
                    Text("How to behave")
                }
                OutlinedButton(onClick = onBrowse, modifier = Modifier.weight(1f)) {
                    Text("All signs")
                }
            }
            TextButton(onClick = { confirmReset = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Reset progress for ${country.label}")
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset progress?") },
            text = { Text("This clears best scores and the mistakes list for ${country.label}.") },
            confirmButton = {
                TextButton(onClick = {
                    store.resetCountry(country); refresh++; confirmReset = false
                }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel") } },
        )
    }
}

// ---------------------------------------------------------------------------
// Quiz
// ---------------------------------------------------------------------------

@Composable
private fun QuizScreen(
    screen: Screen.Quiz,
    onAnswer: (Question, Boolean) -> Unit,
    onQuit: () -> Unit,
    onFinished: (List<Answer>) -> Unit,
) {
    var index by remember { mutableIntStateOf(0) }
    val answers = remember { mutableStateListOf<Answer>() }
    var chosen by remember { mutableStateOf<String?>(null) }
    var confirmQuit by remember { mutableStateOf(false) }

    val question = screen.questions[index]
    val options = remember(index) { (question.wrong + question.correct).shuffled() }
    val score = answers.count { it.isCorrect }

    BackHandler { confirmQuit = true }

    AppScaffold(screen.title, onBack = { confirmQuit = true }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Question ${index + 1} of ${screen.questions.size}",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f),
                )
                Text("Score: $score", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (index + if (chosen != null) 1 else 0).toFloat() / screen.questions.size },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))

            question.sign?.let { sign ->
                SignPlate(
                    sign,
                    Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(Modifier.height(16.dp))
            }
            question.scene?.let { scene ->
                // Before answering it shows the situation; afterwards the correct behaviour.
                SceneImage(
                    scene,
                    reveal = chosen != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                )
                Spacer(Modifier.height(16.dp))
            }

            Text(question.prompt, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            options.forEach { option ->
                OptionButton(
                    text = option,
                    state = when {
                        chosen == null -> OptionState.Idle
                        option == question.correct -> OptionState.Correct
                        option == chosen -> OptionState.Wrong
                        else -> OptionState.Dimmed
                    },
                    onClick = {
                        if (chosen == null) {
                            chosen = option
                            val ok = option == question.correct
                            answers.add(Answer(question, option))
                            onAnswer(question, ok)
                        }
                    },
                )
                Spacer(Modifier.height(8.dp))
            }

            if (chosen != null) {
                val ok = chosen == question.correct
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = (if (ok) CorrectGreen else WrongRed).copy(alpha = 0.12f),
                    ),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            if (ok) "Correct!" else "Not quite – the answer is: ${question.correct}",
                            fontWeight = FontWeight.Bold,
                            color = if (ok) CorrectGreen else WrongRed,
                        )
                        question.localName?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(it, style = MaterialTheme.typography.labelLarge)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(question.explanation, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(12.dp))
                val last = index == screen.questions.lastIndex
                Button(
                    onClick = {
                        if (last) onFinished(answers.toList()) else {
                            index++; chosen = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(if (last) "See results" else "Next question") }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (confirmQuit) {
        AlertDialog(
            onDismissRequest = { confirmQuit = false },
            title = { Text("Leave this quiz?") },
            text = { Text("Your answers so far still count towards the mistakes list.") },
            confirmButton = { TextButton(onClick = { confirmQuit = false; onQuit() }) { Text("Leave") } },
            dismissButton = { TextButton(onClick = { confirmQuit = false }) { Text("Keep going") } },
        )
    }
}

private enum class OptionState { Idle, Correct, Wrong, Dimmed }

@Composable
private fun OptionButton(text: String, state: OptionState, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val (bg, border, fg) = when (state) {
        OptionState.Idle -> Triple(scheme.surfaceVariant, scheme.outlineVariant, scheme.onSurfaceVariant)
        OptionState.Correct -> Triple(CorrectGreen.copy(alpha = 0.18f), CorrectGreen, scheme.onSurface)
        OptionState.Wrong -> Triple(WrongRed.copy(alpha = 0.18f), WrongRed, scheme.onSurface)
        OptionState.Dimmed -> Triple(scheme.surfaceVariant.copy(alpha = 0.5f), scheme.outlineVariant, scheme.onSurfaceVariant.copy(alpha = 0.6f))
    }
    val shape = RoundedCornerShape(12.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .background(bg, shape)
            .border(if (state == OptionState.Correct || state == OptionState.Wrong) 2.dp else 1.dp, border, shape)
            .clickable(enabled = state == OptionState.Idle, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            when (state) {
                OptionState.Correct -> "✓  $text"
                OptionState.Wrong -> "✗  $text"
                else -> text
            },
            color = fg,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

// ---------------------------------------------------------------------------
// Results
// ---------------------------------------------------------------------------

@Composable
private fun ResultsScreen(
    screen: Screen.Results,
    onRetryMistakes: () -> Unit,
    onRetryAll: () -> Unit,
    onMenu: () -> Unit,
) {
    val correct = screen.answers.count { it.isCorrect }
    val total = screen.answers.size
    val percent = if (total == 0) 0 else correct * 100 / total
    val wrong = screen.answers.filterNot { it.isCorrect }
    val verdict = when {
        percent == 100 -> "Perfect – you're ready to roll! 🚗"
        percent >= 85 -> "Great job – just a few to polish."
        percent >= 60 -> "Good start – review the ones you missed."
        else -> "Keep practising – it'll come back quickly."
    }

    AppScaffold("Results", onBack = onMenu) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(8.dp))
                    Text(screen.title, style = MaterialTheme.typography.titleMedium)
                    Text("$correct / $total", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                    Text("$percent%", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Text(verdict, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    if (wrong.isNotEmpty()) {
                        Button(onClick = onRetryMistakes, modifier = Modifier.fillMaxWidth()) {
                            Text("Retry the ${wrong.size} I missed")
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    FilledTonalButton(onClick = onRetryAll, modifier = Modifier.fillMaxWidth()) { Text("Do it again") }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onMenu, modifier = Modifier.fillMaxWidth()) { Text("Back to menu") }
                    if (wrong.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "What you missed",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            items(wrong, key = { it.question.id }) { a ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        a.question.sign?.let {
                            SignPlate(it, Modifier.size(64.dp), padding = 4)
                            Spacer(Modifier.width(12.dp))
                        }
                        a.question.scene?.let {
                            SceneImage(it, reveal = true, modifier = Modifier.width(110.dp).clip(RoundedCornerShape(8.dp)))
                            Spacer(Modifier.width(12.dp))
                        }
                        Column {
                            if (a.question.sign == null) {
                                Text(a.question.prompt, style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(4.dp))
                            }
                            Text("✓ ${a.question.correct}", color = CorrectGreen, fontWeight = FontWeight.SemiBold)
                            Text("✗ ${a.chosen}", color = WrongRed, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// ---------------------------------------------------------------------------
// Browse signs
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrowseScreen(country: Country, onBack: () -> Unit) {
    val signs = remember(country) { QuestionBank.questions(country, Category.SIGNS) }
    var selected by remember { mutableStateOf<Question?>(null) }

    AppScaffold("${country.flag}  All signs", onBack) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(signs, key = { it.id }) { q ->
                Card(onClick = { selected = q }) {
                    Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        q.sign?.let { SignPlate(it, Modifier.size(110.dp), padding = 6) }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            q.correct,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            maxLines = 3,
                        )
                    }
                }
            }
        }
    }

    selected?.let { q ->
        AlertDialog(
            onDismissRequest = { selected = null },
            confirmButton = { TextButton(onClick = { selected = null }) { Text("Close") } },
            title = { Text(q.correct) },
            text = {
                Column {
                    q.sign?.let {
                        SignPlate(
                            it,
                            Modifier
                                .size(140.dp)
                                .align(Alignment.CenterHorizontally),
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    q.localName?.let { Text(it, style = MaterialTheme.typography.labelLarge); Spacer(Modifier.height(6.dp)) }
                    Text(q.explanation)
                }
            },
        )
    }
}

// ---------------------------------------------------------------------------
// Behaviour guide (all scenarios, with the answer shown)
// ---------------------------------------------------------------------------

@Composable
private fun GuideScreen(country: Country, onBack: () -> Unit) {
    val scenarios = remember(country) { QuestionBank.questions(country, Category.SCENARIOS) }
    var showSituation by remember { mutableStateOf<String?>(null) }

    AppScaffold("${country.flag}  How to behave", onBack) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "Every situation with the right behaviour. Tap a picture to switch between the situation and the solution.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(scenarios, key = { it.id }) { q ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        q.scene?.let {
                            SceneImage(
                                it,
                                reveal = showSituation != q.id,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showSituation = if (showSituation == q.id) null else q.id },
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                        Text(q.prompt, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(6.dp))
                        Text("→ ${q.correct}", color = CorrectGreen, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(q.explanation, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
