package com.honor.toolkit.features.lab

import com.honor.toolkit.core.shizuku.ShellExecutor
import com.honor.toolkit.core.shizuku.ShellResult

object SystemTelemetryLab {
    
    fun getMemInfo(): String {
        return executeShellCommand("cat /proc/meminfo | head -10")
    }

    fun getCpuStats(): String {
        return executeShellCommand("cat /proc/cpuinfo | head -15")
    }

    fun getBatteryInfo(): String {
        return executeShellCommand("dumpsys battery")
    }

    fun getRunningServices(): String {
        return executeShellCommand("dumpsys activity services | head -25")
    }

    fun getNetworkInfo(): String {
        return executeShellCommand("ifconfig 2>/dev/null || ip addr show 2>/dev/null | head -20")
    }

    fun executeShellCommand(command: String): String {
        val result = ShellExecutor.execute(command)
        return if (result.isSuccess) {
            result.output.ifEmpty { "Command executed (no output)" }
        } else {
            "⚠ ${result.error.ifEmpty { "Command failed (exit ${result.exitCode})" }}"
        }
    }

    fun executeWithMeta(command: String): ShellResult {
        return ShellExecutor.execute(command)
    }
}
