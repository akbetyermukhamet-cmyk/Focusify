/*
 * Copyright (c) 2025 Akbet Ereke
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

package com.example.focusify

import org.koin.dsl.module
import com.example.focusify.billing.BillingManager
import com.example.focusify.billing.FossBillingManager
import com.example.focusify.billing.TomatoPlusPaywallDialog
import com.example.focusify.di.FlavorUI
import com.example.focusify.ui.settingsScreen.components.BottomButton
import com.example.focusify.ui.settingsScreen.components.TopButton

val flavorModule = module {
    single<BillingManager> { FossBillingManager() }
}

val flavorUiModule = module {
    single {
        FlavorUI(
            tomatoPlusPaywallDialog = ::TomatoPlusPaywallDialog,
            topButton = ::TopButton,
            bottomButton = ::BottomButton
        )
    }
}