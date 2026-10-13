package com.crashcourse.app.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crashcourse.app.update.Release
import com.crashcourse.app.update.Updater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

sealed interface UpdateState {
    data object Idle : UpdateState
    data object Checking : UpdateState
    data object UpToDate : UpdateState
    data class Available(val release: Release) : UpdateState
    data class Downloading(val release: Release, val progress: Float) : UpdateState
    data class Ready(val release: Release, val apk: File) : UpdateState
    data class Failed(val message: String) : UpdateState
}

/** Lives above the navigation so the check runs once per app start, not on every visit. */
class UpdateController(private val scope: CoroutineScope) {
    var state by mutableStateOf<UpdateState>(UpdateState.Idle)
        private set

    /** [silent] checks (on app start) stay quiet unless an update is found. */
    fun check(silent: Boolean) {
        if (state is UpdateState.Checking || state is UpdateState.Downloading) return
        state = UpdateState.Checking
        scope.launch {
            state = try {
                val latest = Updater.fetchLatest()
                when {
                    latest != null && Updater.isNewer(latest) -> UpdateState.Available(latest)
                    silent -> UpdateState.Idle
                    else -> UpdateState.UpToDate
                }
            } catch (e: Exception) {
                if (silent) UpdateState.Idle else UpdateState.Failed("Couldn't reach GitHub. Check your internet connection.")
            }
        }
    }

    fun download(context: Context, release: Release) {
        val appContext = context.applicationContext
        state = UpdateState.Downloading(release, 0f)
        scope.launch {
            state = try {
                val apk = Updater.download(appContext, release) { p ->
                    state = UpdateState.Downloading(release, p)
                }
                UpdateState.Ready(release, apk)
            } catch (e: Exception) {
                UpdateState.Failed("Download failed. Please try again.")
            }
            (state as? UpdateState.Ready)?.let { install(appContext, it) }
        }
    }

    fun install(context: Context, ready: UpdateState.Ready) {
        if (!Updater.canInstall(context)) {
            Updater.openInstallPermissionSettings(context)
            return
        }
        Updater.install(context, ready.apk)
    }
}

@Composable
fun UpdateSection(controller: UpdateController) {
    val context = LocalContext.current
    val state = controller.state

    when (state) {
        is UpdateState.Available, is UpdateState.Downloading, is UpdateState.Ready -> {
            val release = when (state) {
                is UpdateState.Available -> state.release
                is UpdateState.Downloading -> state.release
                is UpdateState.Ready -> state.release
                else -> error("unreachable")
            }
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Update available", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Build ${release.buildNumber} (you have build ${Updater.currentBuild})",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (release.notes.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(release.notes.lineSequence().take(6).joinToString("\n"), style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(12.dp))
                    when (state) {
                        is UpdateState.Available -> Button(
                            onClick = { controller.download(context, release) },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Update now") }

                        is UpdateState.Downloading -> {
                            LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(4.dp))
                            Text("Downloading… ${(state.progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                        }

                        is UpdateState.Ready -> {
                            if (!Updater.canInstall(context)) {
                                Text(
                                    "Android needs your permission once: allow CrashCourse to install apps, then come back and tap Install.",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            Button(
                                onClick = { controller.install(context, state) },
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text("Install") }
                        }

                        else -> Unit
                    }
                }
            }
        }

        else -> Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Version ${Updater.currentVersionName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (state is UpdateState.Checking) {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                }
            }
            when (state) {
                UpdateState.UpToDate -> Text("You're on the latest version ✓", style = MaterialTheme.typography.bodySmall, color = CorrectGreen)
                is UpdateState.Failed -> Text(state.message, style = MaterialTheme.typography.bodySmall, color = WrongRed)
                else -> Unit
            }
            if (state is UpdateState.Idle || state is UpdateState.UpToDate || state is UpdateState.Failed) {
                OutlinedButton(onClick = { controller.check(silent = false) }) { Text("Check for updates") }
            } else {
                TextButton(onClick = {}, enabled = false) { Text("Checking…") }
            }
        }
    }
}
