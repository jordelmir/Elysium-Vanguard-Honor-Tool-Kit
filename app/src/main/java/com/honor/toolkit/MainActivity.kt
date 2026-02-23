package com.honor.toolkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.honor.toolkit.ui.theme.DeepSpace
import com.honor.toolkit.ui.theme.HonorMagicToolkitProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Shizuku broker for privileged access
        com.honor.toolkit.core.root.RootExploitBridge.initBroker()
        
        enableEdgeToEdge()
        setContent {
            HonorMagicToolkitProTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DeepSpace
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        DashboardScreen()
                    }
                }
            }
        }
    }
}
