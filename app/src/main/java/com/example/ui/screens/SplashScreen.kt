package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Stage 1: Corporate Tokio Marine Presentation Screen (White background)
    // Stage 2: Bang Patuh Compliance Ninja Flash Screen (Attachment image, Fit to screen)
    var splashStage by remember { mutableStateOf(1) }

    val alphaAnim = remember { Animatable(0.92f) }
    val scaleAnim = remember { Animatable(0.98f) }

    LaunchedEffect(splashStage) {
        alphaAnim.snapTo(0.85f)
        scaleAnim.snapTo(0.97f)
        alphaAnim.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        scaleAnim.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))

        if (splashStage == 1) {
            delay(2000L)
            splashStage = 2
        } else {
            delay(2200L)
            onSplashFinished()
        }
    }

    val isStage1 = splashStage == 1
    val backgroundColor = if (isStage1) Color.White else Color(0xFF1390C4)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (splashStage == 1) {
                    splashStage = 2
                } else {
                    onSplashFinished()
                }
            }
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        if (isStage1) {
            // Screen 1: Corporate Presentation Screen (Tokio Marine Insurance Group)
            Image(
                painter = painterResource(id = R.drawable.corporate_screen),
                contentDescription = stringResource(R.string.splash_corporate_desc),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .graphicsLayer {
                        alpha = alphaAnim.value
                        scaleX = scaleAnim.value
                        scaleY = scaleAnim.value
                    }
                    .testTag("corporate_screen_image")
            )
        } else {
            // Screen 2: Bang Patuh Compliance Ninja Flash Screen (Attachment Image, Fit to all screens)
            Image(
                painter = painterResource(id = R.drawable.splash_bang_patuh),
                contentDescription = stringResource(R.string.splash_content_desc),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = alphaAnim.value
                        scaleX = scaleAnim.value
                        scaleY = scaleAnim.value
                    }
                    .testTag("bang_patuh_splash_image")
            )
        }
    }
}
