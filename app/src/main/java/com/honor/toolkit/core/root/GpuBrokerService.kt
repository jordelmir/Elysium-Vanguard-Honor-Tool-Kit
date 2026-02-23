package com.honor.toolkit.core.root

import android.content.Context
import android.os.IBinder
import com.honor.toolkit.core.root.IGpuBroker
import kotlin.system.exitProcess

/**
 * GpuBrokerService: The entry point for Shizuku.
 * This class is started by Shizuku in a separate process.
 */
class GpuBrokerService : IGpuBroker.Stub() {

    private val impl = GpuBrokerImpl()

    override fun executeIoctl(cmd: Int, payload: ByteArray?): Int {
        return impl.executeIoctl(cmd, payload)
    }

    override fun scanLeak(propertyId: Int): LongArray {
        return impl.scanLeak(propertyId)
    }

    override fun getContextInfo(): String {
        return impl.getContextInfo()
    }

    override fun getBrokerLog(): String {
        return impl.getBrokerLog()
    }

    override fun verifyRoot(): Boolean {
        return impl.verifyRoot()
    }

    override fun runEsm(): Boolean {
        return impl.runEsm()
    }

    override fun getEsmStage(): Int {
        return impl.getEsmStage()
    }

    override fun getEsmLeak(): Long {
        return impl.getEsmLeak()
    }

    override fun requestSuSession(cmd: String?): String {
        return impl.requestSuSession(cmd ?: "")
    }

    fun destroy() {
        exitProcess(0)
    }
}
