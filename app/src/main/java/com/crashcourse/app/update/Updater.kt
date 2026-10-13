package com.crashcourse.app.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.crashcourse.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** A build published by the GitHub Actions workflow as a release tagged `build-<number>`. */
data class Release(
    val buildNumber: Int,
    val notes: String,
    val apkUrl: String,
    val apkSize: Long,
)

/**
 * Checks the repository's latest GitHub release and installs it.
 *
 * The repo is public, so no token is needed. The APK is handed to Android's
 * own package installer, which asks the user to confirm.
 */
object Updater {
    val currentBuild: Int get() = BuildConfig.VERSION_CODE
    val currentVersionName: String get() = BuildConfig.VERSION_NAME

    private const val TAG_PREFIX = "build-"

    /** Returns the latest release, or null if there is none. Throws on network errors. */
    suspend fun fetchLatest(): Release? = withContext(Dispatchers.IO) {
        val conn = URL("https://api.github.com/repos/${BuildConfig.GITHUB_REPO}/releases/latest")
            .openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            conn.setRequestProperty("Accept", "application/vnd.github+json")
            when (conn.responseCode) {
                HttpURLConnection.HTTP_NOT_FOUND -> return@withContext null
                HttpURLConnection.HTTP_OK -> Unit
                else -> error("GitHub answered ${conn.responseCode}")
            }
            val json = JSONObject(conn.inputStream.bufferedReader().use { it.readText() })
            val tag = json.optString("tag_name")
            val number = tag.removePrefix(TAG_PREFIX).toIntOrNull() ?: return@withContext null
            val assets = json.optJSONArray("assets") ?: return@withContext null
            val apk = (0 until assets.length())
                .map { assets.getJSONObject(it) }
                .firstOrNull { it.optString("name").endsWith(".apk") }
                ?: return@withContext null
            Release(
                buildNumber = number,
                notes = json.optString("body").trim(),
                apkUrl = apk.getString("browser_download_url"),
                apkSize = apk.optLong("size"),
            )
        } finally {
            conn.disconnect()
        }
    }

    fun isNewer(release: Release) = release.buildNumber > currentBuild

    /** Downloads the APK into the cache, reporting progress from 0 to 1. */
    suspend fun download(context: Context, release: Release, onProgress: (Float) -> Unit): File =
        withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, "updates").apply { mkdirs() }
            dir.listFiles()?.forEach { it.delete() }
            val target = File(dir, "CrashCourse-$TAG_PREFIX${release.buildNumber}.apk")

            // GitHub redirects the asset URL to its download host; follow it manually
            // so it works regardless of HttpURLConnection's redirect rules.
            var url = URL(release.apkUrl)
            var conn: HttpURLConnection
            var hops = 0
            while (true) {
                conn = url.openConnection() as HttpURLConnection
                conn.instanceFollowRedirects = false
                conn.connectTimeout = 15_000
                conn.readTimeout = 30_000
                val code = conn.responseCode
                if (code in 300..399 && hops < 5) {
                    url = URL(url, conn.getHeaderField("Location"))
                    conn.disconnect()
                    hops++
                    continue
                }
                if (code != HttpURLConnection.HTTP_OK) {
                    conn.disconnect()
                    error("Download failed ($code)")
                }
                break
            }

            try {
                val total = conn.contentLengthLong.takeIf { it > 0 } ?: release.apkSize
                conn.inputStream.use { input ->
                    target.outputStream().use { output ->
                        val buf = ByteArray(64 * 1024)
                        var read = 0L
                        while (true) {
                            val n = input.read(buf)
                            if (n < 0) break
                            output.write(buf, 0, n)
                            read += n
                            if (total > 0) onProgress((read.toFloat() / total).coerceIn(0f, 1f))
                        }
                    }
                }
            } finally {
                conn.disconnect()
            }
            target
        }

    /** Android 8+ requires the user to allow this app to install other apps. */
    fun canInstall(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()

    fun openInstallPermissionSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }

    /** Opens the system installer for the downloaded APK. */
    fun install(context: Context, apk: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.updates", apk)
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, "application/vnd.android.package-archive")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
