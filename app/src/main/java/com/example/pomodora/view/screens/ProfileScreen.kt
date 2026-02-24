package com.example.pomodora.view.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.pomodora.model.ResultState
import com.example.pomodora.model.UserProfile
import com.example.pomodora.ui.theme.AppBackground
import com.example.pomodora.ui.theme.CardBorder
import com.example.pomodora.ui.theme.CardSurface
import com.example.pomodora.ui.theme.GlowGreen
import com.example.pomodora.ui.theme.TextPrimary
import com.example.pomodora.ui.theme.TextSecondary
import com.example.pomodora.view.NavigationItem
import com.example.pomodora.viewModel.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    // Load profile on first composition
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    // Navigate away after logout

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppBackground)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurface)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Filled.Person, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    "Profile",
                    modifier = Modifier.align(Alignment.Center),
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            when (val state = profileState) {
                is ResultState.Loading, ResultState.Idle -> ProfileShimmer()
                is ResultState.Error -> ErrorCard(message = state.message)
                is ResultState.Success -> {
                    ProfileContent(
                        profile = state.data,
                        onSignOut = { viewModel.logout()
                            navController.navigate(NavigationItem.LoginScreen) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(profile: UserProfile, onSignOut: () -> Unit) {

    // Staggered entrance animations
    val avatarAnim   = remember { Animatable(0f) }
    val card1Anim    = remember { Animatable(0f) }
    val card2Anim    = remember { Animatable(0f) }
    val buttonAnim   = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        avatarAnim.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        card1Anim.animateTo(1f,  tween(400, easing = FastOutSlowInEasing))
        card2Anim.animateTo(1f,  tween(400, easing = FastOutSlowInEasing))
        buttonAnim.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }

    // ── Avatar ──
    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha       = avatarAnim.value
                scaleX      = 0.7f + 0.3f * avatarAnim.value
                scaleY      = 0.7f + 0.3f * avatarAnim.value
            }
            .size(100.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(listOf(GlowGreen, GlowGreen.copy(alpha = 0.4f)))
            ),
        contentAlignment = Alignment.Center
    ) {
        // Initials from email
        val initial = profile.email.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        Text(
            text = initial,
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = AppBackground
        )
    }

    // ── Email card ──
    ProfileInfoCard(
        label = "Email Address",
        value = profile.email,
        icon  = Icons.Default.Email,
        animProgress = card1Anim.value,
        offsetStart  = 60f
    )

    // ── User ID card ──
    ProfileInfoCard(
        label = "User ID",
        value = profile.userId,
        icon  = Icons.Default.Badge,
        animProgress = card2Anim.value,
        offsetStart  = 80f
    )

    Spacer(Modifier.height(12.dp))

    // ── Sign Out button ──
    var signOutPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (signOutPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "signOutScale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha       = buttonAnim.value
                translationY = 30f * (1f - buttonAnim.value)
            }
            .fillMaxWidth()
            .graphicsLayer { scaleX = buttonScale; scaleY = buttonScale }
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A1A1A))
            .border(1.dp, Color(0xFFFF4C4C).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress  = { signOutPressed = true; tryAwaitRelease(); signOutPressed = false },
                    onTap    = { onSignOut() }
                )
            }
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFFF4C4C), modifier = Modifier.size(20.dp))
            Text(
                "Sign Out",
                color     = Color(0xFFFF4C4C),
                fontWeight = FontWeight.Bold,
                fontSize  = 16.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ── Reusable info card ────────────────────────────────────────────────────────
@Composable
private fun ProfileInfoCard(
    label: String,
    value: String,
    icon: ImageVector,
    animProgress: Float,
    offsetStart: Float
) {
    Row(
        modifier = Modifier
            .graphicsLayer {
                alpha        = animProgress
                translationX = offsetStart * (1f - animProgress)
            }
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardSurface)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GlowGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = GlowGreen, modifier = Modifier.size(22.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
            Text(
                value,
                color      = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }
    }
}

// ── Shimmer placeholder ───────────────────────────────────────────────────────
@Composable
private fun ProfileShimmer() {
    val shimmerAlpha by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "shimmerAlpha"
    )
    val shimmerColor = GlowGreen.copy(alpha = shimmerAlpha)

    // Avatar placeholder
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(shimmerColor)
    )
    // Two card placeholders
    repeat(2) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(shimmerColor)
        )
    }
}

// ── Error card ────────────────────────────────────────────────────────────────
@Composable
private fun ErrorCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A1A1A))
            .border(1.dp, Color(0xFFFF4C4C).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = Color(0xFFFF4C4C), fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}