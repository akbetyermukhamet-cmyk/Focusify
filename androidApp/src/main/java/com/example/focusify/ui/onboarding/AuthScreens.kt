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

package com.example.focusify.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.focusify.R

import androidx.compose.runtime.collectAsState
import org.koin.compose.koinInject
import com.example.focusify.ui.onboarding.OnboardingOrange

@Composable
fun SignUpScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onLoginRedirect: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AuthViewModel = koinInject()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
            viewModel.resetSuccess()
        }
    }

    if (uiState.isLoading) {
        LoadingDialog(stringResource(R.string.signing_up_label))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Icon(painterResource(R.drawable.arrow_back), contentDescription = null, tint = Color.Black)
        }

        Text(
            text = stringResource(R.string.join_focusify_today),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = stringResource(R.string.unlock_productivity),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = stringResource(R.string.email), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.email)) },
            leadingIcon = { Icon(painterResource(R.drawable.email), null, modifier = Modifier.size(20.dp)) },
            isError = uiState.emailError != null,
            supportingText = uiState.emailError?.let { { Text(it) } },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OnboardingOrange,
                unfocusedBorderColor = Color.LightGray,
                focusedLeadingIconColor = OnboardingOrange,
                unfocusedLeadingIconColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.password), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.password)) },
            leadingIcon = { Icon(painterResource(R.drawable.mobile_lock_portrait), null, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painterResource(if (passwordVisible) R.drawable.mobile_lock_portrait else R.drawable.mobile_lock_portrait), null, modifier = Modifier.size(20.dp))
                }
            },
            isError = uiState.passwordError != null,
            supportingText = uiState.passwordError?.let { { Text(it) } },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OnboardingOrange,
                unfocusedBorderColor = Color.LightGray,
                focusedLeadingIconColor = OnboardingOrange,
                unfocusedLeadingIconColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.confirm_password), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; viewModel.clearError() },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.confirm_password)) },
            leadingIcon = { Icon(painterResource(R.drawable.mobile_lock_portrait), null, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(painterResource(if (confirmPasswordVisible) R.drawable.mobile_lock_portrait else R.drawable.mobile_lock_portrait), null, modifier = Modifier.size(20.dp))
                }
            },
            isError = uiState.confirmPasswordError != null,
            supportingText = uiState.confirmPasswordError?.let { { Text(it) } },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OnboardingOrange,
                unfocusedBorderColor = Color.LightGray,
                focusedLeadingIconColor = OnboardingOrange,
                unfocusedLeadingIconColor = Color.Gray
            )
        )

        uiState.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.already_have_account_label), color = Color.Gray)
            Text(
                " " + stringResource(R.string.log_in),
                color = OnboardingOrange,
                modifier = Modifier.clickable { onLoginRedirect() },
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.signUp(email, password, confirmPassword) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OnboardingOrange),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(stringResource(R.string.sign_up_label), fontSize = 16.sp)
        }
    }
}

@Composable
fun LoginScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onSignUpRedirect: () -> Unit,
    onForgotPassword: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AuthViewModel = koinInject()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
            viewModel.resetSuccess()
        }
    }

    if (uiState.isLoading) {
        LoadingDialog(stringResource(R.string.logging_in_label))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Icon(painterResource(R.drawable.arrow_back), contentDescription = null, tint = Color.Black)
        }

        Text(
            text = stringResource(R.string.welcome_back_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = stringResource(R.string.get_back_to_productivity),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = stringResource(R.string.email), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.email)) },
            leadingIcon = { Icon(painterResource(R.drawable.email), null, modifier = Modifier.size(20.dp)) },
            isError = uiState.emailError != null || (uiState.error != null && uiState.passwordError == null),
            supportingText = {
                Column {
                    uiState.emailError?.let { Text(it) }
                    if (uiState.emailError == null) {
                        uiState.error?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OnboardingOrange,
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red,
                focusedLeadingIconColor = OnboardingOrange,
                unfocusedLeadingIconColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.password), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.password)) },
            leadingIcon = { Icon(painterResource(R.drawable.mobile_lock_portrait), null, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painterResource(if (passwordVisible) R.drawable.mobile_lock_portrait else R.drawable.mobile_lock_portrait), null, modifier = Modifier.size(20.dp))
                }
            },
            isError = uiState.passwordError != null || (uiState.error != null && uiState.emailError == null),
            supportingText = {
                Column {
                    uiState.passwordError?.let { Text(it) }
                    if (uiState.passwordError == null && uiState.emailError == null) {
                        uiState.error?.let {
                            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OnboardingOrange,
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red,
                focusedLeadingIconColor = OnboardingOrange,
                unfocusedLeadingIconColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = OnboardingOrange)
                )
                Text(stringResource(R.string.remember_me), color = Color.Black)
            }
            Text(
                stringResource(R.string.forgot_password_label),
                color = OnboardingOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onForgotPassword() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.dont_have_account_label), color = Color.Gray)
            Text(
                " " + stringResource(R.string.sign_up),
                color = OnboardingOrange,
                modifier = Modifier.clickable { onSignUpRedirect() },
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OnboardingOrange),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(stringResource(R.string.login_label), fontSize = 16.sp)
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AuthViewModel = koinInject()
) {
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isResetEmailSent) {
        if (uiState.isResetEmailSent) {
            onSuccess()
            viewModel.resetSuccess()
        }
    }

    if (uiState.isLoading) {
        LoadingDialog(stringResource(R.string.sending_email_label))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Icon(painterResource(R.drawable.arrow_back), null, tint = Color.Black)
        }

        Text(
            text = stringResource(R.string.forgot_password_title_label),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = stringResource(R.string.forgot_password_desc_label),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = stringResource(R.string.your_registered_email), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.email)) },
            leadingIcon = { Icon(painterResource(R.drawable.email), null, modifier = Modifier.size(20.dp)) },
            isError = uiState.emailError != null,
            supportingText = uiState.emailError?.let { { Text(it) } },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OnboardingOrange,
                unfocusedBorderColor = Color.LightGray
            )
        )

        uiState.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.resetPassword(email) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OnboardingOrange),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(stringResource(R.string.send_reset_email), fontSize = 16.sp)
        }
    }
}

@Composable
fun EnterOTPScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onVerify: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(56) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Icon(painterResource(R.drawable.arrow_back), null, tint = Color.Black)
        }

        Text(
            text = stringResource(R.string.enter_otp_code_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = stringResource(R.string.enter_otp_code_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) { index ->
                val char = otp.getOrNull(index)?.toString() ?: ""
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                        .clickable { /* Handle focus */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = char, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.resend_code_timer, timeLeft),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        if (timeLeft == 0) {
            Text(
                text = stringResource(R.string.resend_code),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { timeLeft = 60 },
                textAlign = TextAlign.Center,
                color = OnboardingOrange,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Simulated Number Pad for UI
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("*", "0", "DEL")).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { key ->
                        TextButton(onClick = {
                            if (key == "DEL") { if (otp.isNotEmpty()) otp = otp.dropLast(1) }
                            else if (otp.length < 4 && key != "*") otp += key

                            if (otp.length == 4) onVerify()
                        }) {
                            Text(text = key, fontSize = 24.sp, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateNewPasswordScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Icon(painterResource(R.drawable.arrow_back), null, tint = Color.Black)
        }

        Text(
            text = stringResource(R.string.secure_your_account_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = stringResource(R.string.secure_your_account_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = stringResource(R.string.new_password), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OnboardingOrange)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.confirm_new_password), fontWeight = FontWeight.Bold, color = Color.Black)
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OnboardingOrange)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OnboardingOrange),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(stringResource(R.string.save_new_password), fontSize = 16.sp)
        }
    }
}

@Composable
fun ResetSuccessScreen(
    contentPadding: PaddingValues,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(OnboardingOrange.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.mobile_lock_portrait),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = OnboardingOrange
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.you_are_all_set),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.password_reset_success),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OnboardingOrange),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(stringResource(R.string.go_to_homepage), fontSize = 16.sp)
        }
    }
}

@Composable
fun LoadingDialog(text: String) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = OnboardingOrange)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = text, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}
