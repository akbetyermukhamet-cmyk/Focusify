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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Screen : NavKey {
    @Serializable
    object Splash : Screen()

    @Serializable
    object Onboarding : Screen()

    @Serializable
    object Welcome : Screen()

    @Serializable
    object SignUp : Screen()

    @Serializable
    object Login : Screen()

    @Serializable
    object ForgotPassword : Screen()

    @Serializable
    object EnterOTP : Screen()

    @Serializable
    object CreateNewPassword : Screen()

    @Serializable
    object ResetSuccess : Screen()

    @Serializable
    object Timer : Screen()

    @Serializable
    object Calendar : Screen()

    @Serializable
    object AOD : Screen()

    @Serializable
    sealed class Settings : Screen() {
        @Serializable
        object Main : Settings()

        @Serializable
        object About : Settings()

        @Serializable
        object Alarm : Settings()

        @Serializable
        object Appearance : Settings()

        @Serializable
        object Backup : Settings()

        @Serializable
        object Timer : Settings()
    }

    @Serializable
    sealed class Stats : Screen() {
        @Serializable
        object Main : Stats()

        @Serializable
        object LastWeek : Stats()

        @Serializable
        object LastMonth : Stats()

        @Serializable
        object LastYear : Stats()

        @Serializable
        object TagsList : Stats()

        @Serializable
        object ProjectsList : Stats()
    }
}

data class NavItem(
    val route: Screen,
    @param:DrawableRes val unselectedIcon: Int,
    @param:DrawableRes val selectedIcon: Int,
    @param:StringRes val label: Int
)

data class SettingsNavItem(
    val route: Screen.Settings,
    @param:DrawableRes val icon: Int,
    @param:StringRes val label: Int,
    val innerSettings: List<Int>
)
