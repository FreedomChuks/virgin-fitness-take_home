package com.virginactive.features.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.virginactive.core.designsystem.*
import com.virginactive.features.login.components.*

@Composable
fun LoginScreen(state: LoginState, onAction: (LoginAction) -> Unit) {
    val focusManager = LocalFocusManager.current
    val submit = {
        focusManager.clearFocus()
        onAction(LoginAction.Submit)
    }
    BoxWithConstraints(Modifier.fillMaxSize().imePadding()) {
        Column(
            Modifier.fillMaxWidth()
                .heightIn(min = maxHeight)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ActiveSpace.screen, vertical = ActiveSpace.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
                Column(
                    Modifier.widthIn(max = 440.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(ActiveSpace.medium)) {
                        LoginHeader()
                        ActiveTextField(
                            value = state.username,
                            onValueChange = { onAction(LoginAction.UsernameChanged(it)) },
                            label = "Email address",
                            modifier =
                                Modifier.fillMaxWidth().semantics {
                                    contentType = ContentType.EmailAddress + ContentType.Username
                                },
                            leadingIcon = ActiveIcons.Email,
                            enabled = !state.busy,
                            isError = state.error != null,
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    autoCorrectEnabled = false,
                                    imeAction = ImeAction.Next),
                            keyboardActions =
                                KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }))
                        ActiveTextField(
                            value = state.password,
                            onValueChange = { onAction(LoginAction.PasswordChanged(it)) },
                            label = "Password",
                            modifier =
                                Modifier.fillMaxWidth().semantics {
                                    contentType = ContentType.Password
                                },
                            leadingIcon = ActiveIcons.Lock,
                            enabled = !state.busy,
                            isError = state.error != null,
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    autoCorrectEnabled = false,
                                    imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { submit() }),
                            visualTransformation =
                                if (state.passwordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                            trailingContent = {
                                IconButton(
                                    onClick = { onAction(LoginAction.TogglePasswordVisibility) },
                                    enabled = !state.busy) {
                                        Icon(
                                            if (state.passwordVisible) ActiveIcons.VisibilityOff
                                            else ActiveIcons.Visibility,
                                            contentDescription =
                                                if (state.passwordVisible) "Hide password"
                                                else "Show password")
                                    }
                            })
                        state.error?.let {
                            ActiveNotice(
                                it,
                                Modifier.fillMaxWidth(),
                                tone = ActiveStatusTone.Error,
                                title = "Couldn't sign you in")
                        }
                        ActiveButton(
                            label = if (state.busy) "Signing in…" else "Sign in",
                            onClick = submit,
                            modifier = Modifier.fillMaxWidth().padding(top = ActiveSpace.xs),
                            busy = state.busy)
                    }
            }
    }
}
