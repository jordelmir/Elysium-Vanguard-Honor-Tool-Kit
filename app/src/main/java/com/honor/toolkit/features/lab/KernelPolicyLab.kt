package com.honor.toolkit.features.lab

import com.honor.toolkit.core.shizuku.ShellExecutor

object KernelPolicyLab {
    
    fun getKernelVersion(): String {
        return ShellExecutor.execute("uname -a").displayOutput
    }

    fun getPropData(propName: String): String {
        return ShellExecutor.execute("getprop $propName").displayOutput
    }

    fun getSelinuxStatus(): String {
        return ShellExecutor.execute("getenforce").displayOutput
    }

    fun getProcessContext(): String {
        return ShellExecutor.execute("id").displayOutput
    }

    fun getMountInfo(): String {
        return ShellExecutor.execute("mount | grep -E '/system|/vendor' | head -5").displayOutput
    }
}
