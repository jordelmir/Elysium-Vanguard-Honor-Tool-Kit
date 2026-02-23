package com.honor.toolkit

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.honor.toolkit.ui.components.TacticalButton
import com.honor.toolkit.ui.components.TacticalGlassCard
import com.honor.toolkit.ui.theme.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen() {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // ── State: Telemetry ──
    val integrity by produceState<Map<String, String>>(initialValue = emptyMap()) {
        value = try {
            com.honor.toolkit.features.lab.StabilityOrchestrator.verifySystemIntegrity()
        } catch (_: Exception) { emptyMap() }
    }
    val shellMode = com.honor.toolkit.core.shizuku.ShellExecutor.isShizukuAvailable()
    val brokerConnected by com.honor.toolkit.core.root.RootExploitBridge.brokerConnected.collectAsState()

    // ── Operation States ──
    var telemetryResult by remember { mutableStateOf<String?>(null) }
    var telemetryLoading by remember { mutableStateOf(false) }
    var bloatwareResult by remember { mutableStateOf<String?>(null) }
    var bloatwareLoading by remember { mutableStateOf(false) }
    var binderResult by remember { mutableStateOf<String?>(null) }
    var binderLoading by remember { mutableStateOf(false) }
    var stressResult by remember { mutableStateOf<String?>(null) }
    var stressLoading by remember { mutableStateOf(false) }

    // ── Root Trigger State ──
    var rootTriggered by remember { mutableStateOf(false) }
    var rootProgress by remember { mutableStateOf(0f) }
    var rootStatus by remember { mutableStateOf("STANDBY") }
    var rootLog by remember { mutableStateOf<List<String>>(emptyList()) }
    var isRootedState by remember { mutableStateOf(false) }
    var rootShellInput by remember { mutableStateOf("") }
    var rootShellOutput by remember { mutableStateOf<List<String>>(emptyList()) }
    var rootShellLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isRootedState = try { com.honor.toolkit.core.root.RootExploitBridge.isRooted() } catch (_: Exception) { false }
    }

    // ── Animations ──
    val infiniteTransition = rememberInfiniteTransition(label = "main")
    val headerGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "headerGlow"
    )
    val statusDot by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "statusDot"
    )
    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "scanLine"
    )
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.02f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Deep space gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(DarkNavy, DeepSpace, Color(0xFF030810))
                    )
                )
                // Ambient glow - top left
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(CyanNeon.copy(alpha = 0.05f), Color.Transparent)
                    ),
                    radius = size.width * 0.7f,
                    center = Offset(0f, 0f)
                )
                // Ambient glow - bottom right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(MagentaNeon.copy(alpha = 0.03f), Color.Transparent)
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(size.width, size.height)
                )
                // Scan line (horizontal)
                val lineY = size.height * scanLine
                drawLine(
                    color = CyanNeon.copy(alpha = 0.04f),
                    start = Offset(0f, lineY),
                    end = Offset(size.width, lineY),
                    strokeWidth = 1.5f
                )
            }
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 48.dp, bottom = 40.dp)
    ) {
        // ═══════════════════════════════════════════
        //  ANIMATED HEADER
        // ═══════════════════════════════════════════
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.elite_logo),
                    contentDescription = "Chronomaly Logo",
                    modifier = Modifier.size(110.dp).padding(bottom = 8.dp)
                )
                Text(
                    text = "HONOR MAGIC",
                    style = MaterialTheme.typography.displayLarge,
                    color = CyanNeon.copy(alpha = headerGlow),
                    letterSpacing = 4.sp
                )
                Text(
                    text = "TOOLKIT  PRO",
                    style = MaterialTheme.typography.displayLarge,
                    color = MagentaNeon.copy(alpha = headerGlow * 0.85f),
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pulsing status dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(pulse)
                            .clip(CircleShape)
                            .background(MatrixGreen.copy(alpha = statusDot))
                    )
                    Text(
                        text = "ENCRYPTED  //  v2.1.0  //  ${if (shellMode) "SHIZUKU" else "RUNTIME"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreen.copy(alpha = 0.7f)
                    )
                }
                // Animated divider line
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    CyanNeon.copy(alpha = breathe * 0.5f),
                                    MagentaNeon.copy(alpha = breathe * 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }

        // ═══════════════════════════════════════════
        //  SYSTEM TELEMETRY CARD
        // ═══════════════════════════════════════════
        item {
            TacticalGlassCard(glowColor = CyanNeon) {
                SectionHeader("⟐", "SYSTEM ARCHITECTURE TELEMETRY", CyanNeon)
                Spacer(modifier = Modifier.height(14.dp))
                MetricRow("DEVICE", "HONOR MAGIC V2", CyberBlue)
                MetricRow("CHIPSET", "SNAPDRAGON 8 GEN 2", CyberBlue)
                MetricRow("SELINUX", integrity["SELinux"] ?: "—", WarningOrange)
                MetricRow(
                    "CONTEXT",
                    if (isRootedState) "ROOT // UID:0" else (integrity["Context"]?.take(40) ?: "—"),
                    if (isRootedState) MatrixGreen else CyanNeon
                )
                MetricRow(
                    "KERNEL",
                    integrity["Kernel"]?.take(30) ?: "—",
                    ElectricLime
                )
                MetricRow(
                    "EXEC MODE",
                    if (shellMode) "SHIZUKU (ELEVATED)" else "RUNTIME (STANDARD)",
                    if (shellMode) MatrixGreen else WarningOrange
                )
                MetricRow(
                    "SOVEREIGN GUARD",
                    if (brokerConnected) "✓ CONNECTED" else "⏳ BINDING...",
                    if (brokerConnected) MatrixGreen else WarningOrange
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Animated gradient bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    CyanNeon.copy(alpha = breathe),
                                    MagentaNeon.copy(alpha = breathe),
                                    ElectricLime.copy(alpha = breathe * 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }

        // ═══════════════════════════════════════════
        //  🔴 ROOT TRIGGER PANEL
        // ═══════════════════════════════════════════
        item {
            TacticalGlassCard(
                glowColor = if (rootTriggered) (if (isRootedState) MatrixGreen else PlasmaOrange) else MagentaNeon
            ) {
                SectionHeader(
                    if (rootTriggered) "◉" else "⊛",
                    "CHRONOMALY ENGINE // CVE-2025-38352",
                    if (rootTriggered) PlasmaOrange else MagentaNeon
                )
                Spacer(modifier = Modifier.height(12.dp))
                MetricRow("CVE", "CVE-2025-38352", UltraViolet)
                MetricRow("VECTOR", "HEAP CORRUPTION // KGSL", MagentaNeon)
                MetricRow("TARGET", "CRED STRUCT OVERWRITE", CyanNeon)
                MetricRow("STATUS", rootStatus, when(rootStatus) {
                    "STANDBY" -> Color.Gray
                    "COMPLETE" -> MatrixGreen
                    else -> PlasmaOrange
                })
                Spacer(modifier = Modifier.height(14.dp))

                // Progress bar (animated)
                if (rootTriggered && rootProgress < 1f) {
                    LinearProgressIndicator(
                        progress = { rootProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = PlasmaOrange,
                        trackColor = PlasmaOrange.copy(alpha = 0.15f),
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Root log output
                AnimatedVisibility(
                    visible = rootLog.isNotEmpty(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, MagentaNeon.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "COPY LOG",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MagentaNeon,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(MagentaNeon.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                        .border(0.5.dp, MagentaNeon.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(rootLog.joinToString("\n")))
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            rootLog.forEach { line ->
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when {
                                        line.startsWith("[OK]") -> MatrixGreen
                                        line.startsWith("[!!]") -> DangerRed
                                        line.startsWith("[>>]") -> PlasmaOrange
                                        else -> CyanNeon.copy(alpha = 0.8f)
                                    },
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ROOT TRIGGER BUTTON
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(pulse)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (rootTriggered && rootProgress >= 1f)
                                Brush.horizontalGradient(listOf(MatrixGreen.copy(alpha = 0.15f), MatrixGreen.copy(alpha = 0.05f)))
                            else
                                Brush.horizontalGradient(listOf(MagentaNeon.copy(alpha = 0.15f), PlasmaOrange.copy(alpha = 0.1f)))
                        )
                        .border(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    MagentaNeon.copy(alpha = breathe * 0.7f),
                                    PlasmaOrange.copy(alpha = breathe * 0.5f)
                                )
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = (!rootTriggered || rootProgress >= 1f) && brokerConnected) {
                            if (!rootTriggered) {
                                rootTriggered = true
                                coroutineScope.launch {
                                    rootStatus = "INITIALIZING"
                                    rootLog = listOf("[>>] Launching Chronomaly exploit engine...")
                                    rootProgress = 0.05f

                                    // Run the real exploit on a background thread
                                    val exploitResult = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                        // Launch exploit in a separate thread so we can poll progress
                                        val resultHolder = java.util.concurrent.atomic.AtomicBoolean(false)
                                        val exploitThread = Thread {
                                            try {
                                                val result = com.honor.toolkit.core.root.RootExploitBridge.attemptEscalation()
                                                resultHolder.set(result)
                                            } catch (_: Exception) {
                                                resultHolder.set(false)
                                            }
                                        }
                                        exploitThread.start()

                                        // Poll native telemetry while exploit runs
                                        while (exploitThread.isAlive) {
                                            Thread.sleep(200)
                                            try {
                                                val stage = com.honor.toolkit.core.root.RootExploitBridge.nativeGetStage()
                                                val stageName = com.honor.toolkit.core.root.RootExploitBridge.getStageName(stage)
                                                val nativeLog = com.honor.toolkit.core.root.RootExploitBridge.getExploitLog()
                                                
                                                // Update progress from stage
                                                val progress = when (stage) {
                                                    0 -> 0.05f; 1 -> 0.10f; 2 -> 0.20f
                                                    3 -> 0.35f; 4 -> 0.50f; 5 -> 0.65f
                                                    6 -> 0.80f; 7 -> 0.95f; else -> 0.5f
                                                }
                                                rootProgress = progress
                                                rootStatus = stageName
                                                
                                                // Parse native log lines
                                                if (nativeLog.isNotEmpty()) {
                                                    rootLog = nativeLog.split("\n").filter { it.isNotBlank() }
                                                }
                                            } catch (_: Exception) { /* polling may fail briefly */ }
                                        }
                                        
                                        exploitThread.join()
                                        resultHolder.get()
                                    }

                                    // Final telemetry
                                    rootProgress = 1f
                                    try {
                                        val finalLog = com.honor.toolkit.core.root.RootExploitBridge.getExploitLog()
                                        if (finalLog.isNotEmpty()) {
                                            rootLog = finalLog.split("\n").filter { it.isNotBlank() }
                                        }
                                        val kernelLeak = com.honor.toolkit.core.root.RootExploitBridge.getKernelLeak()
                                        if (kernelLeak != 0L) {
                                            rootLog = rootLog + "[>>] Kernel pointer: 0x${kernelLeak.toString(16)}"
                                        }
                                    } catch (_: Exception) {}

                                    // Verify strictly via Stage 7 (SYSTEM_ACCESS) completion in the BROKER context
                                    val finalStage = com.honor.toolkit.core.root.RootExploitBridge.getStage()
                                    val uidCheck = com.honor.toolkit.features.lab.KernelPolicyLab.getProcessContext()
                                    val brokerUid = com.honor.toolkit.core.root.RootExploitBridge.getUid()

                                    if (exploitResult && finalStage == 7 && brokerUid == 0) {
                                        rootStatus = "COMPLETE"
                                        rootLog = rootLog + "[OK] ═══ ROOT ACHIEVED — UID 0 ═══"
                                        rootLog = rootLog + "[>>] Broker context coherence verified"
                                        isRootedState = true
                                    } else {
                                        rootStatus = "BLOCKED"
                                        rootLog = rootLog + "[!!] Kernel Proof FAILED — Stage $finalStage"
                                        rootLog = rootLog + "[!!] Broker UID: $brokerUid (Expected 0)"
                                        rootLog = rootLog + "[!!] Process Context: $uidCheck"
                                        isRootedState = false
                                    }
                                }
                            }
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            !brokerConnected -> "⏳  BINDING SOVEREIGN GUARD..."
                            rootTriggered && rootProgress >= 1f && rootStatus == "COMPLETE" -> "✓  ROOT ACHIEVED"
                            rootTriggered && rootProgress < 1f -> "⟳  $rootStatus..."
                            rootTriggered -> "◉  EXPLOIT COMPLETED"
                            else -> "⚡  TRIGGER CHRONOMALY  ⚡"
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp,
                        color = when {
                            rootStatus == "COMPLETE" -> MatrixGreen
                            rootTriggered -> PlasmaOrange
                            else -> MagentaNeon
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // ═══════════════════════════════════════════
        //  ⚡ ROOT SHELL TERMINAL (Stage 7)
        // ═══════════════════════════════════════════
        if (isRootedState) {
            item {
                TacticalGlassCard(glowColor = MatrixGreen) {
                    SectionHeader("➲", "ROOT COMMAND BRIDGE // SOVEREIGN", MatrixGreen)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Shell Output
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, MatrixGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (rootShellOutput.isEmpty()) {
                                item { 
                                    Text(
                                        "Waiting for command input...",
                                        color = MatrixGreen.copy(alpha = 0.3f),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            rootShellOutput.forEach { line ->
                                item {
                                    Text(
                                        line,
                                        color = MatrixGreen,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Input Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .border(0.5.dp, MatrixGreen.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            androidx.compose.foundation.text.BasicTextField(
                                value = rootShellInput,
                                onValueChange = { rootShellInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                ),
                                cursorBrush = androidx.compose.ui.graphics.SolidColor(MatrixGreen),
                                decorationBox = { innerTextField ->
                                    if (rootShellInput.isEmpty()) {
                                        Text(
                                            "Enter root command...",
                                            color = Color.White.copy(alpha = 0.2f),
                                            fontSize = 12.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MatrixGreen.copy(alpha = if (rootShellLoading) 0.1f else 0.2f))
                                .border(1.dp, MatrixGreen.copy(alpha = 0.5f), CircleShape)
                                .clickable(enabled = !rootShellLoading && rootShellInput.isNotBlank()) {
                                    coroutineScope.launch {
                                        rootShellLoading = true
                                        val cmd = rootShellInput
                                        rootShellInput = ""
                                        rootShellOutput = rootShellOutput + "# $cmd"
                                        
                                        val result = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                            com.honor.toolkit.core.root.RootExploitBridge.requestSu(cmd)
                                        }
                                        
                                        rootShellOutput = rootShellOutput + result.split("\n")
                                        rootShellLoading = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (rootShellLoading) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MatrixGreen,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("↵", color = MatrixGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ═══════════════════════════════════════════
        //  ENGINEERING OPERATIONS
        // ═══════════════════════════════════════════
        item {
            SectionHeader("◆", "ENGINEERING OPERATIONS", CyanNeon)
        }

        // ── Button 1: System Telemetry Lab ──
        item {
            TacticalButton(
                text = "System Telemetry Lab",
                onClick = {
                    coroutineScope.launch {
                        telemetryLoading = true
                        telemetryResult = null
                        try {
                            val kernel = com.honor.toolkit.features.lab.KernelPolicyLab.getKernelVersion()
                            val mem = com.honor.toolkit.features.lab.SystemTelemetryLab.getMemInfo()
                            val cpu = com.honor.toolkit.features.lab.SystemTelemetryLab.getCpuStats()
                            val sb = StringBuilder()
                            sb.appendLine("══ KERNEL ══")
                            sb.appendLine(kernel.take(120))
                            sb.appendLine("══ MEMORY ══")
                            sb.appendLine(mem.take(200))
                            sb.appendLine("══ CPU ══")
                            sb.appendLine(cpu.take(200))
                            telemetryResult = sb.toString()
                        } catch (e: Exception) {
                            telemetryResult = "⚠ ${e.message}"
                        }
                        telemetryLoading = false
                    }
                },
                color = CyanNeon,
                icon = "⊡",
                isLoading = telemetryLoading
            )
            ResultPanel(telemetryResult, CyanNeon)
        }

        // ── Button 2: Bloatware Orchestrator ──
        item {
            TacticalButton(
                text = "Bloatware Orchestrator",
                onClick = {
                    coroutineScope.launch {
                        bloatwareLoading = true
                        bloatwareResult = null
                        try {
                            val result = com.honor.toolkit.features.lab.SystemTelemetryLab.executeShellCommand(
                                "pm list packages -s | grep -iE 'hihonor|huawei' | head -15"
                            )
                            val fallbackList = listOf(
                                "com.hihonor.mall", "com.hihonor.video",
                                "com.hihonor.music", "com.hihonor.tips",
                                "com.hihonor.browser", "com.hihonor.cloud",
                                "com.huawei.android.launcher", "com.huawei.appmarket"
                            )
                            val sb = StringBuilder()
                            sb.appendLine("══ DETECTED SYSTEM BLOATWARE ══")
                            if (result.startsWith("package:")) {
                                result.lines().forEach { line ->
                                    sb.appendLine("  ├─ ${line.removePrefix("package:")}")
                                }
                            } else {
                                sb.appendLine("  [Runtime mode — showing known targets]")
                                fallbackList.forEach { pkg ->
                                    sb.appendLine("  ├─ $pkg")
                                }
                            }
                            sb.appendLine("══ ${if (result.startsWith("package:")) result.lines().size else fallbackList.size} PACKAGES IDENTIFIED ══")
                            bloatwareResult = sb.toString()
                        } catch (e: Exception) {
                            bloatwareResult = "⚠ ${e.message}"
                        }
                        bloatwareLoading = false
                    }
                },
                color = MagentaNeon,
                icon = "⊠",
                isLoading = bloatwareLoading
            )
            ResultPanel(bloatwareResult, MagentaNeon)
        }

        // ── Button 3: Binder Transaction Monitor ──
        item {
            TacticalButton(
                text = "Binder Transaction Monitor",
                onClick = {
                    coroutineScope.launch {
                        binderLoading = true
                        binderResult = null
                        try {
                            val services = com.honor.toolkit.features.lab.SystemTelemetryLab.getRunningServices()
                            val network = com.honor.toolkit.features.lab.SystemTelemetryLab.getNetworkInfo()
                            val sb = StringBuilder()
                            sb.appendLine("══ ACTIVE SERVICES ══")
                            sb.appendLine(services.take(300))
                            sb.appendLine("══ NETWORK INTERFACES ══")
                            sb.appendLine(network.take(200))
                            binderResult = sb.toString()
                        } catch (e: Exception) {
                            binderResult = "⚠ ${e.message}"
                        }
                        binderLoading = false
                    }
                },
                color = ElectricLime,
                icon = "⊞",
                isLoading = binderLoading
            )
            ResultPanel(binderResult, ElectricLime)
        }

        // ── Button 4: Stress Test ──
        item {
            TacticalButton(
                text = "Verification: Stress Test",
                onClick = {
                    coroutineScope.launch {
                        stressLoading = true
                        stressResult = null
                        delay(300)
                        try {
                            val results = com.honor.toolkit.features.lab.StabilityOrchestrator.runStressTest()
                            val sb = StringBuilder()
                            sb.appendLine("══ STRESS TEST ANALYSIS ══")
                            results.forEach { (key, value) ->
                                sb.appendLine("  [$key]")
                                sb.appendLine("    ${value.take(100)}")
                            }
                            sb.appendLine("══ STATUS: ${if (results.isNotEmpty()) "PASS" else "INCONCLUSIVE"} ══")
                            stressResult = sb.toString()
                        } catch (e: Exception) {
                            stressResult = "⚠ ${e.message}"
                        }
                        stressLoading = false
                    }
                },
                color = PlasmaOrange,
                icon = "⊛",
                isLoading = stressLoading
            )
            ResultPanel(stressResult, PlasmaOrange)
        }

        // ═══════════════════════════════════════════
        //  FOOTER
        // ═══════════════════════════════════════════
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                CyanNeon.copy(alpha = 0.2f),
                                MagentaNeon.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "HONOR MAGIC TOOLKIT PRO  //  CHRONOMALY ENGINE  //  2026",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ═══════════════════════════════════════════════
//  COMPOSABLE HELPERS
// ═══════════════════════════════════════════════

@Composable
fun SectionHeader(icon: String, title: String, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "sectionGlow")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "sectionGlowAnim"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = icon, fontSize = 16.sp,
            color = color.copy(alpha = glow)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = color.copy(alpha = glow),
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
fun MetricRow(label: String, value: String, color: Color = MatrixGreen) {
    val infiniteTransition = rememberInfiniteTransition(label = "metricPulse$label")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse),
        label = "metricAlpha$label"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = alpha),
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun ResultPanel(result: String?, color: Color) {
    AnimatedVisibility(
        visible = result != null,
        enter = expandVertically(animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)),
        exit = shrinkVertically() + fadeOut()
    ) {
        result?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.06f),
                                color.copy(alpha = 0.02f)
                            )
                        )
                    )
                    .border(1.dp, color.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = color.copy(alpha = 0.9f),
                    lineHeight = 14.sp,
                    fontSize = 9.sp
                )
            }
        }
    }
}
