package com.honor.toolkit

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.honor.toolkit.features.toolkit.BloatwareManager
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BinderStressTest {

    @Test
    fun stressTest_BinderTransactions_RapidFire() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val manager = BloatwareManager(appContext)
        val targetPackage = "com.hihonor.mall"

        println("STRESS TEST START: Rapid Binder transactions to $targetPackage")
        
        // Monitoring via logcat should reveal if /dev/binder is saturated
        repeat(500) { i ->
            val success = manager.disablePackage(targetPackage)
            if (i % 50 == 0) {
                println("ITERATION $i: Success=$success")
            }
        }
        
        println("STRESS TEST COMPLETE. Verify dumpsys binder_calls_stats for anomalies.")
    }
}
