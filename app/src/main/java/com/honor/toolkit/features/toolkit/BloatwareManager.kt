package com.honor.toolkit.features.toolkit

import android.content.Context
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

data class SystemApp(
    val name: String,
    val packageName: String,
    val isBloatware: Boolean
)

class BloatwareManager(private val context: Context) {
    
    fun disablePackage(packageName: String): Boolean {
        if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) return false
        
        return try {
            val cmd = "pm disable-user --user 0 $packageName"
            val process = com.honor.toolkit.core.shizuku.ShizukuReflection.newProcess(
                cmd.split(" ").toTypedArray()
            )
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    fun getKnownBloatware(): List<String> {
        return listOf(
            "com.hihonor.mall", 
            "com.hihonor.video", 
            "com.hihonor.music", 
            "com.huawei.android.launcher"
        )
    }
}
