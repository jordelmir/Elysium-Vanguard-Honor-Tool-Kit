package com.honor.toolkit.core.root

import android.os.Process
import android.util.Log
import com.honor.toolkit.core.root.IGpuBroker
import kotlin.system.exitProcess

/**
 * GpuBrokerImpl: The Sovereign Guard Implementation.
 * Runs in the privileged context (shell) via Shizuku.
 */
class GpuBrokerImpl : IGpuBroker.Stub() {

    override fun executeIoctl(cmd: Int, payload: ByteArray?): Int {
        return nativeExecuteIoctl(cmd, payload)
    }

    override fun scanLeak(propertyId: Int): LongArray {
        return LongArray(0)
    }

    override fun getContextInfo(): String {
        return nativeGetContextInfo()
    }

    override fun getBrokerLog(): String {
        return nativeGetBrokerLog()
    }

    override fun verifyRoot(): Boolean {
        return nativeVerifyRoot()
    }

    override fun runEsm(): Boolean {
        return nativeRunEsm()
    }

    override fun getEsmStage(): Int {
        return nativeGetEsmStage()
    }

    override fun getEsmLeak(): Long {
        return nativeGetEsmLeak()
    }

    override fun requestSuSession(cmd: String): String {
        return nativeRequestSuSession(cmd)
    }

    override fun getBrokerUid(): Int {
        return nativeGetUid()
    }

    private external fun nativeGetUid(): Int

    private external fun nativeRequestSuSession(cmd: String): String
    private external fun nativeExecuteIoctl(cmd: Int, payload: ByteArray?): Int
    private external fun nativeGetBrokerLog(): String
    private external fun nativeGetContextInfo(): String
    private external fun nativeVerifyRoot(): Boolean
    private external fun nativeRunEsm(): Boolean
    private external fun nativeGetEsmStage(): Int
    private external fun nativeGetEsmLeak(): Long

    companion object {
        init {
            System.loadLibrary("exploit_bridge")
        }
    }
}
