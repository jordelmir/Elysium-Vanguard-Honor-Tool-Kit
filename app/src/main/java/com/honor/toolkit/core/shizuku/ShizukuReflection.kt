package com.honor.toolkit.core.shizuku

import java.lang.Process

object ShizukuReflection {
    fun newProcess(cmd: Array<String>): Process {
        return try {
            val method = rikka.shizuku.Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            method.isAccessible = true
            method.invoke(null, cmd, null, "/") as Process
        } catch (e: Exception) {
            throw RuntimeException("Failed to invoke Shizuku.newProcess", e)
        }
    }
}
