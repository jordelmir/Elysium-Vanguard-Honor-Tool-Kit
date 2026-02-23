package com.honor.toolkit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.honor.toolkit.ui.theme.*

@Composable
fun TacticalGlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color = CyanNeon,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                // Outer glow effect
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = glowAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2, 0f),
                        radius = size.width * 0.8f
                    )
                )
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CardDark,
                        DeepSpace.copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glowColor.copy(alpha = glowAlpha),
                        glowColor.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            content()
        }
    }
}
