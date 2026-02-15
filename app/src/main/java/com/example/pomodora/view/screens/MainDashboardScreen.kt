package com.example.pomodora.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarHorizontalFabPosition
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.pomodora.view.DashboardTab
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    navController: NavController, // Pass this if Home/Focus need to navigate elsewhere
) {
    val pagerState = rememberPagerState(pageCount = { DashboardTab.entries.size }, initialPage = 1)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val bottomInset = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    // This allows us to hide the toolbar when the keyboard is open or specific conditions are met
    val isToolbarVisible = true

    Scaffold(
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
                            toolbarContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            toolbarContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .padding(
                                top = ScreenOffset,
                                bottom = bottomInset
                                        + ScreenOffset
                            )
                            .zIndex(1f),
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
                                            .height(56.dp),
                                        shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                                        colors = ToggleButtonDefaults.toggleButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            checkedContainerColor = MaterialTheme.colorScheme.primary,
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
                DashboardTab.Home -> HomeScreen(navController)
                DashboardTab.Focus -> FocusScreen()
                DashboardTab.Profile -> {}
            }
        }
    }
}
