package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GamingLogo
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingDarkBg
import com.example.ui.theme.GamingDeepPurple
import com.example.ui.theme.GamingNeonBlue
import com.example.ui.theme.GamingPrimaryPurple
import com.example.ui.theme.GamingSecondaryBg
import com.example.ui.theme.GamingTextMuted
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2200)
        onTimeout()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SplashGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingDarkBg)
    ) {
        // Abstract Cyberpunk Grid & Light Trails Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Radial ambient glow behind the logo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GamingPrimaryPurple.copy(alpha = glowAlpha * 0.45f),
                        GamingNeonBlue.copy(alpha = glowAlpha * 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.42f),
                    radius = w * 0.75f
                )
            )

            // Cybernetic angled circuit lines
            drawLine(
                color = GamingBorder.copy(alpha = 0.3f),
                start = Offset(0f, h * 0.2f),
                end = Offset(w * 0.4f, h * 0.25f),
                strokeWidth = 1.5f
            )
            drawLine(
                color = GamingCyan.copy(alpha = 0.25f),
                start = Offset(w * 0.4f, h * 0.25f),
                end = Offset(w * 0.65f, h * 0.15f),
                strokeWidth = 1.5f
            )
            drawLine(
                color = GamingDeepPurple.copy(alpha = 0.3f),
                start = Offset(w, h * 0.75f),
                end = Offset(w * 0.3f, h * 0.85f),
                strokeWidth = 1.5f
            )
        }

        // Main Splash Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Center Logo & Brand Name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                GamingLogo(
                    iconSize = 130.dp,
                    showTagline = true
                )
            }

            // Minimalist Loading Bar at the bottom
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GamingSecondaryBg)
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = GamingCyan,
                        trackColor = GamingSecondaryBg
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "INITIALIZING SQUAD VOICE...",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = GamingTextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
