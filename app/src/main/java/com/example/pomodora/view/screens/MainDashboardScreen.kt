package com.example.pomodora.view.screens

import android.graphics.Matrix
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.pomodora.ui.theme.AppBackground
import com.example.pomodora.view.DashboardTab
import com.example.pomodora.viewModel.AuthViewModel
import com.example.pomodora.viewModel.StatsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    navController: NavController,
    viewModel: AuthViewModel, // Pass this if Home/Focus need to navigate elsewhere
    statsViewModel: StatsViewModel
) {
    val pagerState = rememberPagerState(pageCount = { DashboardTab.entries.size }, initialPage = 1)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val bottomInset = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    // This allows us to hide the toolbar when the keyboard is open or specific conditions are met
    val isToolbarVisible = true

    Scaffold(
        containerColor = AppBackground,
        // 1. MOVE TOOLBAR HERE
        bottomBar = {
            // We use a Box inside bottomBar to control the "Floating" position
            // consistently, rather than filling the whole bottom area.
            Box(
                modifier = Modifier
                    .fillMaxWidth() ,// The slot fills width, but we align content inside
                contentAlignment = Alignment.BottomCenter // Center the floating pill
            ) {
                AnimatedVisibility(
                    visible = isToolbarVisible,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    HorizontalFloatingToolbar(
                        expanded = true,
                        // FIX: Force symmetric padding (removes the empty space for FAB)
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(
                            toolbarContainerColor = Color.Transparent,
                            toolbarContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .padding(
                                top = ScreenOffset,
                                bottom = bottomInset
                                        + ScreenOffset
                            )
                            .zIndex(1f)
                            .coloredBlurShadow(
                                color = Color(0xFF598852),
                                blurRadius = 15.dp,
                                offsetX = 1.dp,
                                offsetY = 0.dp
                            )
                            .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF021A32),
                                    Color(0xFF15850D),
                                    Color(0xFF021A35)
                                )
                            ),
                            shape = CircleShape
                        ),
                        content = {
                            DashboardTab.entries.forEach { tab ->
                                val isSelected = pagerState.currentPage == tab.index

                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Above
                                    ),
                                    tooltip = { PlainTooltip { Text(tab.title) } },
                                    state = rememberTooltipState()
                                ) {
                                    ToggleButton(
                                        checked = isSelected,
                                        onCheckedChange = {
                                            if (!isSelected) {
                                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                                scope.launch { pagerState.animateScrollToPage(tab.index) }
                                            }
                                        },
                                        modifier = Modifier
                                            .widthIn(min = 64.dp)
                                            .height(56.dp)
                                            .background(
                                                brush = if (isSelected) ellipticalBrush else SolidColor(Color.Transparent),
                                                shape = CircleShape
                                            ),
                                        shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                                        colors = ToggleButtonDefaults.toggleButtonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            checkedContainerColor = Color.Transparent,
                                            checkedContentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        ) {
                                            Crossfade(targetState = isSelected, label = "icon") { selected ->
                                                Icon(
                                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = tab.title,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            AnimatedVisibility(
                                                visible = isSelected,
                                                enter = expandHorizontally(expandFrom = Alignment.Start) + fadeIn(),
                                                exit = shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut()
                                            ) {
                                                Text(
                                                    text = tab.title,
                                                    modifier = Modifier.padding(start = 8.dp),
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    fontSize = 16.sp,
                                                    lineHeight = 24.sp,
                                                    overflow = TextOverflow.Clip
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // 2. MAIN CONTENT
        // Note: HorizontalPager draws BEHIND the floating bar because we don't
        // apply 'innerPadding.calculateBottomPadding()' to it.
        // This is usually desired for a "Floating" look.
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()), // Respect top bar if you have one
            contentPadding = PaddingValues(0.dp) // Reset padding
        ) { pageIndex ->
            when (DashboardTab.getByIndex(pageIndex)) {
                DashboardTab.Stats -> StatisticScreen(navController,statsViewModel)
                DashboardTab.Focus -> FocusScreen()
                DashboardTab.Profile -> ProfileScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}


fun Modifier.coloredBlurShadow(
    color: Color,
    blurRadius: Dp,
    shapeRadius: Dp = 100.dp, // High value ensures a perfect pill shape
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp
) = this.drawBehind {
    val paint = Paint().apply {
        val frameworkPaint = asFrameworkPaint()
        frameworkPaint.color = android.graphics.Color.WHITE
        frameworkPaint.setShadowLayer(
            blurRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(), // Y offset
            color.toArgb()
        )
    }
    drawIntoCanvas { canvas ->
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = shapeRadius.toPx(),
            radiusY = shapeRadius.toPx(),
            paint = paint
        )
    }
}

val colorCenter = Color(0x8FFAFAFA).toArgb()
val colorEdge = Color(0xFF358435).toArgb()

val ellipticalBrush = object : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // 1. Set the radius to half the width so it reaches the far left and right edges
        val radius = (size.width / 2f).coerceAtLeast(1f)

        val shader = RadialGradient(
            centerX,
            centerY,
            radius,
            intArrayOf(colorCenter, colorEdge),
            // 2. Adjust these stops: 0.75f means the inner 75% is solid FAFAFA,
            // fading smoothly into the green edge for the last 25%
            floatArrayOf(0.20f, 1.0f),
            Shader.TileMode.CLAMP
        )

        // 3. The Magic: Scale the gradient vertically so it matches the pill's aspect ratio
        val matrix = Matrix()
        if (size.width > 0f) {
            val scaleY = size.height / size.width
            matrix.setScale(1f, scaleY, centerX, centerY)
            shader.setLocalMatrix(matrix)
        }

        return shader
    }


}
