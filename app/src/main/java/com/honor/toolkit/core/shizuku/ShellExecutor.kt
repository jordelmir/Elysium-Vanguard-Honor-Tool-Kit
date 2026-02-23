package com.honor.toolkit.core.shizuku

import rikka.shizuku.Shizuku

/**
 * Dual-mode shell executor.
 * Attempts Shizuku first for elevated privileges,
 * falls back to Runtime.exec for standard shell access.
 */
object ShellExecutor {

    fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (_: Exception) {
            false
        }
    }

    fun execute(command: String): ShellResult {
        return if (isShizukuAvailable()) {
            executeViaShizuku(command)
        } else {
            executeViaRuntime(command)
        }
    }

    private fun executeViaShizuku(command: String): ShellResult {
        return try {
            val process = ShizukuReflection.newProcess(arrayOf("sh", "-c", command))
            val output = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            ShellResult(
                output = output.trim(),
                error = error.trim(),
                exitCode = exitCode,
                method = "SHIZUKU"
            )
        } catch (e: Exception) {
            // Shizuku failed, fallback
            executeViaRuntime(command)
        }
    }

    private fun executeViaRuntime(command: String): ShellResult {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val output = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            ShellResult(
                output = output.trim(),
                error = error.trim(),
                exitCode = exitCode,
                method = "RUNTIME"
            )
        } catch (e: Exception) {
            ShellResult(
                output = "",
                error = "EXCEPTION: ${e.message}",
                exitCode = -1,
                method = "FAILED"
            )
        }
    }
}

data class ShellResult(
    val output: String,
    val error: String,
    val exitCode: Int,
    val method: String
) {
    val isSuccess get() = exitCode == 0
    val displayOutput get() = if (output.isNotEmpty()) output else error.ifEmpty { "No output" }
}
