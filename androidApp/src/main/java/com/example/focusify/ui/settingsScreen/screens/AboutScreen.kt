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

package com.example.focusify.ui.settingsScreen.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import org.koin.compose.koinInject
import com.example.focusify.R
import com.example.focusify.di.AppInfo
import com.example.focusify.di.FlavorUI
import com.example.focusify.ui.mergePaddingValues
import com.example.focusify.ui.settingsScreen.components.ClickableListItem
import com.example.focusify.ui.settingsScreen.components.LicenseBottomSheet
import com.example.focusify.ui.theme.AppFonts.googleFlex600
import com.example.focusify.ui.theme.AppFonts.robotoFlexTopBar
import com.example.focusify.ui.theme.CustomColors.detailPaneTopBarColors
import com.example.focusify.ui.theme.CustomColors.listItemColors
import com.example.focusify.ui.theme.CustomColors.topBarColors
import com.example.focusify.ui.theme.TomatoShapeDefaults.PANE_MAX_WIDTH
import com.example.focusify.ui.theme.TomatoShapeDefaults.bottomListItemShape
import com.example.focusify.ui.theme.TomatoShapeDefaults.topListItemShape
import com.example.focusify.ui.theme.TomatoTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutScreen(
    contentPadding: PaddingValues,
    isPlus: Boolean,
    onBack: () -> Unit,
    flavorUI: FlavorUI = koinInject(),
    appInfo: AppInfo = koinInject(),
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uriHandler = LocalUriHandler.current

    val widthExpanded = currentWindowAdaptiveInfo()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val socialLinks = remember {
        listOf(
            SocialLink(R.drawable.github, "https://github.com/akbetyermukhamet-cmyk"),
            SocialLink(R.drawable.x, "https://www.instagram.com/yerbuhbet/?hl=de"),
            SocialLink(R.drawable.globe, "https://www.instagram.com/yerbuhbet/?hl=de"),
            SocialLink(R.drawable.email, "mailto:abeke.ereke@gmail.com")
        )
    }

    var showLicense by rememberSaveable { mutableStateOf(false) }

    val barColors = if (widthExpanded) detailPaneTopBarColors
    else topBarColors

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(barColors.containerColor)
    ) {
        Scaffold(
            topBar = {
                LargeFlexibleTopAppBar(
                    title = {
                        Text(stringResource(R.string.about), fontFamily = robotoFlexTopBar)
                    },
                    subtitle = {
                        Text(stringResource(R.string.app_name))
                    },
                    navigationIcon = {
                        if (!widthExpanded)
                            FilledTonalIconButton(
                                onClick = onBack,
                                shapes = IconButtonDefaults.shapes(),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = listItemColors.containerColor
                                )
                            ) {
                                Icon(
                                    painterResource(R.drawable.arrow_back),
                                    stringResource(R.string.back)
                                )
                            }
                    },
                    colors = barColors,
                    scrollBehavior = scrollBehavior
                )
            },
            containerColor = barColors.containerColor,
            modifier = modifier
                .widthIn(max = PANE_MAX_WIDTH)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->
            val insets = mergePaddingValues(innerPadding, contentPadding)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = insets,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Box(Modifier.background(listItemColors.containerColor, topListItemShape)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_launcher_monochrome),
                                tint = colorScheme.onPrimaryContainer,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        colorScheme.primaryContainer,
                                        MaterialShapes.Cookie12Sided.toShape()
                                    )
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    if (!isPlus) stringResource(R.string.app_name)
                                    else stringResource(R.string.app_name_plus),
                                    color = colorScheme.onSurface,
                                    style = typography.titleLarge,
                                    fontFamily = googleFlex600
                                )
                                Text(
                                    text = "${appInfo.versionName} (${appInfo.versionCode})",
                                    style = typography.labelLarge,
                                    color = colorScheme.primary
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                FilledTonalIconButton(
                                    onClick = {
                                        uriHandler.openUri("https://discord.gg/yCmgEpp3")
                                    },
                                    shapes = IconButtonDefaults.shapes()
                                ) {
                                    Icon(
                                        painterResource(R.drawable.discord),
                                        contentDescription = "Discord",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                FilledTonalIconButton(
                                    onClick = { uriHandler.openUri("https://github.com/akbetyermukhamet-cmyk") },
                                    shapes = IconButtonDefaults.shapes()
                                ) {
                                    Icon(
                                        painterResource(R.drawable.github),
                                        contentDescription = "GitHub",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Box(Modifier.background(listItemColors.containerColor, bottomListItemShape)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painterResource(R.drawable.pfp),
                                    tint = colorScheme.onSecondaryContainer,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            colorScheme.secondaryContainer,
                                            MaterialShapes.Square.toShape()
                                        )
                                        .padding(8.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Akbet Ereke",
                                        style = typography.titleLarge,
                                        color = colorScheme.onSurface,
                                        fontFamily = googleFlex600
                                    )
                                    Text(
                                        "Developer",
                                        style = typography.labelLarge,
                                        color = colorScheme.secondary
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                            }
                            Spacer(Modifier.height(8.dp))
                            Row {
                                Spacer(Modifier.width((64 + 16).dp))
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    socialLinks.fastForEach {
                                        FilledTonalIconButton(
                                            onClick = { uriHandler.openUri(it.url) },
                                            shapes = IconButtonDefaults.shapes(),
                                            modifier = Modifier.width(52.dp)
                                        ) {
                                            Icon(
                                                painterResource(it.icon),
                                                null,
                                                modifier = Modifier.size(ButtonDefaults.SmallIconSize)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(12.dp)) }

                item { flavorUI.topButton(Modifier) }
                item { flavorUI.bottomButton(Modifier) }

                item { Spacer(Modifier.height(12.dp)) }
            }
        }
    }

    if (showLicense) {
        LicenseBottomSheet({ showLicense = false })
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    TomatoTheme(dynamicColor = false) {
        AboutScreen(
            contentPadding = PaddingValues(),
            isPlus = true,
            onBack = {}
        )
    }
}

data class SocialLink(
    @param:DrawableRes val icon: Int,
    val url: String
)
