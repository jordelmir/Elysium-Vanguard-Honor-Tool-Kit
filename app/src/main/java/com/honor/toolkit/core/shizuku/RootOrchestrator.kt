package com.honor.toolkit.core.shizuku

import rikka.shizuku.Shizuku

object RootOrchestrator {
    fun executeRootCommand(command: String): String {
        if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) return "ERR_PERM"
        return try {
            val process = com.honor.toolkit.core.shizuku.ShizukuReflection.newProcess(arrayOf("sh", "-c", command))
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            output
        } catch (e: Exception) {
            "ERR: ${e.message}"
        }
    }
}
