/*
 * Copyright (c) 2025-2026 Akbet Ereke
 *
 * This file is part of Tomato - a minimalist pomodoro timer for Android.
 *
 * Tomato is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tomato is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tomato.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.focusify.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import org.koin.compose.koinInject
import com.example.focusify.di.FlavorUI
import com.example.focusify.service.TimerService
import com.example.focusify.ui.onboarding.CreateNewPasswordScreen
import com.example.focusify.ui.onboarding.EnterOTPScreen
import com.example.focusify.ui.onboarding.ForgotPasswordScreen
import com.example.focusify.ui.onboarding.LoginScreen
import com.example.focusify.ui.onboarding.OnboardingScreen
import com.example.focusify.ui.onboarding.ResetSuccessScreen
import com.example.focusify.ui.onboarding.SignUpScreen
import com.example.focusify.ui.onboarding.SplashScreen
import com.example.focusify.ui.onboarding.WelcomeScreen
import com.example.focusify.ui.calendar.CalendarScreen
import com.example.focusify.ui.settingsScreen.SettingsScreenRoot
import com.example.focusify.ui.settingsScreen.viewModel.SettingsAction
import com.example.focusify.ui.settingsScreen.viewModel.SettingsViewModel
import com.example.focusify.ui.statsScreen.StatsScreenRoot
import com.example.focusify.ui.timerScreen.AlarmDialog
import com.example.focusify.ui.timerScreen.TimerScreen
import com.example.focusify.ui.timerScreen.viewModel.TimerMode
import com.example.focusify.ui.timerScreen.viewModel.TimerViewModel
import com.example.focusify.utils.onBack

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppScreen(
    isAODEnabled: Boolean,
    isPlus: Boolean,
    setTimerFrequency: (Float) -> Unit,
    modifier: Modifier = Modifier,
    flavorUI: FlavorUI = koinInject(),
    timerViewModel: TimerViewModel = koinInject(),
    settingsViewModel: SettingsViewModel = koinInject()
) {
    val context = LocalContext.current

    val uiState by timerViewModel.timerState.collectAsStateWithLifecycle()
    val tasks by timerViewModel.tasks.collectAsStateWithLifecycle()
    val projects by timerViewModel.projects.collectAsStateWithLifecycle()
    val tags by timerViewModel.tags.collectAsStateWithLifecycle()
    val settingsState by settingsViewModel.settingsState.collectAsStateWithLifecycle()
    val progress by timerViewModel.progress.collectAsStateWithLifecycle()

    val layoutDirection = LocalLayoutDirection.current
    val motionScheme = motionScheme
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val systemBarsInsets = WindowInsets.systemBars.asPaddingValues()
    val cutoutInsets = WindowInsets.displayCutout.asPaddingValues()

    val backStack = rememberNavBackStack(Screen.Splash)

    if (uiState.alarmRinging)
        AlarmDialog {
            Intent(context, TimerService::class.java).also {
                it.action = TimerService.Actions.STOP_ALARM.toString()
                context.startService(it)
            }
        }

    var showPaywall by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            val showBottomBar = backStack.last() !is Screen.AOD &&
                    backStack.last() !is Screen.Splash &&
                    backStack.last() !is Screen.Onboarding &&
                    backStack.last() !is Screen.Welcome &&
                    backStack.last() !is Screen.SignUp &&
                    backStack.last() !is Screen.Login &&
                    backStack.last() !is Screen.ForgotPassword &&
                    backStack.last() !is Screen.EnterOTP &&
                    backStack.last() !is Screen.CreateNewPassword &&
                    backStack.last() !is Screen.ResetSuccess

            AnimatedVisibility(
                showBottomBar,
                enter = slideInVertically(motionScheme.slowSpatialSpec()) { it },
                exit = slideOutVertically(motionScheme.slowSpatialSpec()) { it }
            ) {
                val wide = remember {
                    windowSizeClass.isWidthAtLeastBreakpoint(
                        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
                    )
                }

                val primary by animateColorAsState(
                    if (uiState.timerMode == TimerMode.FOCUS) colorScheme.primary else colorScheme.tertiary
                )
                val onPrimary by animateColorAsState(
                    if (uiState.timerMode == TimerMode.FOCUS) colorScheme.onPrimary else colorScheme.onTertiary
                )
                val primaryContainer by animateColorAsState(
                    if (uiState.timerMode == TimerMode.FOCUS) colorScheme.primaryContainer else colorScheme.tertiaryContainer
                )
                val onPrimaryContainer by animateColorAsState(
                    if (uiState.timerMode == TimerMode.FOCUS) colorScheme.onPrimaryContainer else colorScheme.onTertiaryContainer
                )

                NavigationBar(
                    containerColor = primaryContainer,
                    contentColor = onPrimaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f)
                ) {
                    mainScreens.fastForEach { item ->
                        val selected by remember { derivedStateOf { backStack.lastOrNull() == item.route } }
                        NavigationBarItem(
                            selected = selected,
                            onClick = if (item.route != Screen.Timer) {
                                {
                                    if (backStack.size < 2) backStack.add(item.route)
                                    else backStack[1] = item.route
                                }
                            } else {
                                { if (backStack.size > 1) backStack.removeAt(1) }
                            },
                            icon = {
                                Crossfade(selected, label = "icon") {
                                    if (it) Icon(
                                        painterResource(item.selectedIcon),
                                        stringResource(item.label)
                                    )
                                    else Icon(
                                        painterResource(item.unselectedIcon),
                                        stringResource(item.label)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = stringResource(item.label),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = onPrimary,
                                selectedTextColor = onPrimaryContainer,
                                unselectedIconColor = onPrimaryContainer,
                                unselectedTextColor = onPrimaryContainer,
                                indicatorColor = primary
                            )
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { contentPadding ->
        SharedTransitionLayout {
            NavDisplay(
                backStack = backStack,
                onBack = backStack::onBack,
                transitionSpec = {
                    fadeIn(motionScheme.defaultEffectsSpec())
                        .togetherWith(fadeOut(motionScheme.defaultEffectsSpec()))
                },
                popTransitionSpec = {
                    fadeIn(motionScheme.defaultEffectsSpec())
                        .togetherWith(fadeOut(motionScheme.defaultEffectsSpec()))
                },
                predictivePopTransitionSpec = {
                    fadeIn(motionScheme.defaultEffectsSpec())
                        .togetherWith(fadeOut(motionScheme.defaultEffectsSpec()))
                },
                entryProvider = entryProvider {
                    entry<Screen.Splash> {
                        SplashScreen(
                            contentPadding = contentPadding,
                            onNext = {
                                if (settingsState.onboardingCompleted) {
                                    backStack[0] = Screen.Timer
                                } else {
                                    backStack[0] = Screen.Onboarding
                                }
                            }
                        )
                    }

                    entry<Screen.Onboarding> {
                        OnboardingScreen(
                            contentPadding = contentPadding,
                            onFinish = {
                                backStack[0] = Screen.Welcome
                            }
                        )
                    }

                    entry<Screen.Welcome> {
                        WelcomeScreen(
                            contentPadding = contentPadding,
                            onSignUp = {
                                backStack.add(Screen.SignUp)
                            },
                            onLogin = {
                                backStack.add(Screen.Login)
                            }
                        )
                    }

                    entry<Screen.SignUp> {
                        SignUpScreen(
                            contentPadding = contentPadding,
                            onBack = backStack::onBack,
                            onLoginRedirect = {
                                backStack.removeAt(backStack.lastIndex)
                                backStack.add(Screen.Login)
                            },
                            onSuccess = {
                                settingsViewModel.onAction(SettingsAction.SaveOnboardingCompleted(true))
                                backStack.clear()
                                backStack.add(Screen.Timer)
                            }
                        )
                    }

                    entry<Screen.Login> {
                        LoginScreen(
                            contentPadding = contentPadding,
                            onBack = backStack::onBack,
                            onSignUpRedirect = {
                                backStack.removeAt(backStack.lastIndex)
                                backStack.add(Screen.SignUp)
                            },
                            onForgotPassword = {
                                backStack.add(Screen.ForgotPassword)
                            },
                            onSuccess = {
                                settingsViewModel.onAction(SettingsAction.SaveOnboardingCompleted(true))
                                backStack.clear()
                                backStack.add(Screen.Timer)
                            }
                        )
                    }

                    entry<Screen.ForgotPassword> {
                        ForgotPasswordScreen(
                            contentPadding = contentPadding,
                            onBack = backStack::onBack,
                            onSuccess = {
                                backStack.add(Screen.ResetSuccess)
                            }
                        )
                    }

                    entry<Screen.EnterOTP> {
                        EnterOTPScreen(
                            contentPadding = contentPadding,
                            onBack = backStack::onBack,
                            onVerify = {
                                backStack.add(Screen.CreateNewPassword)
                            }
                        )
                    }

                    entry<Screen.CreateNewPassword> {
                        CreateNewPasswordScreen(
                            contentPadding = contentPadding,
                            onBack = backStack::onBack,
                            onSave = {
                                backStack.add(Screen.ResetSuccess)
                            }
                        )
                    }

                    entry<Screen.ResetSuccess> {
                        ResetSuccessScreen(
                            contentPadding = contentPadding,
                            onContinue = {
                                backStack.clear()
                                backStack.add(Screen.Login)
                            }
                        )
                    }

                    entry<Screen.Timer> {
                        TimerScreen(
                            viewModel = timerViewModel,
                            timerState = uiState,
                            settingsState = settingsState,
                            tasks = tasks,
                            projects = projects,
                            tags = tags,
                            isPlus = isPlus,
                            contentPadding = contentPadding,
                            progress = { progress },
                            onAction = timerViewModel::onAction,
                            modifier = if (isAODEnabled) Modifier.clickable {
                                if (backStack.size < 2) backStack.add(Screen.AOD)
                            } else Modifier
                        )
                    }

                    entry<Screen.AOD> {
                        AlwaysOnDisplay(
                            timerState = uiState,
                            secureAod = settingsState.secureAod,
                            progress = { progress },
                            setTimerFrequency = setTimerFrequency,
                            modifier = if (isAODEnabled) Modifier.clickable {
                                if (backStack.size > 1) backStack.removeLastOrNull()
                            } else Modifier
                        )
                    }

                    entry<Screen.Calendar> {
                        CalendarScreen(
                            timerState = uiState,
                            tasks = tasks,
                            projects = projects,
                            tags = tags,
                            onAction = timerViewModel::onAction,
                            onNavigateToTimer = {
                                backStack.clear()
                                backStack.add(Screen.Timer)
                            },
                            contentPadding = contentPadding
                        )
                    }

                    entry<Screen.Settings.Main> {
                        SettingsScreenRoot(
                            setShowPaywall = { showPaywall = it },
                            contentPadding = contentPadding
                        )
                    }

                    entry<Screen.Settings.Appearance> {
                        com.example.focusify.ui.settingsScreen.screens.AppearanceSettings(
                            settingsState = settingsState,
                            contentPadding = contentPadding,
                            isPlus = isPlus,
                            onAction = settingsViewModel::onAction,
                            setShowPaywall = { showPaywall = it },
                            onBack = backStack::onBack
                        )
                    }

                    entry<Screen.Stats.Main> {
                        StatsScreenRoot(
                            contentPadding = contentPadding,
                            focusGoal = settingsState.focusGoal
                        )
                    }
                }
            )
        }
    }

    AnimatedVisibility(
        showPaywall,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        flavorUI.tomatoPlusPaywallDialog(isPlus) { showPaywall = false }
    }
}