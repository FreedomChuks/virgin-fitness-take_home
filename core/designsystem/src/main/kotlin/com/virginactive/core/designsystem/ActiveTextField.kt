package com.virginactive.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun ActiveTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.heightIn(min = ActiveSize.button),
        label = { Text(label) },
        leadingIcon =
            leadingIcon?.let {
                { Icon(it, contentDescription = null, Modifier.size(ActiveSize.icon)) }
            },
        trailingIcon = trailingContent,
        singleLine = true,
        enabled = enabled,
        isError = isError,
        shape = MaterialTheme.shapes.medium,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            ),
    )
}

@ActivePreviews
@Composable
private fun ActiveTextFieldPreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.padding(ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.smallMedium)) {
                    ActiveTextField(
                        "member@example.com",
                        {},
                        "Email address",
                        Modifier.fillMaxWidth(),
                        leadingIcon = ActiveIcons.Email)
                    ActiveTextField(
                        "",
                        {},
                        "Password",
                        Modifier.fillMaxWidth(),
                        leadingIcon = ActiveIcons.Lock,
                        isError = true)
                    ActiveTextField(
                        "Disabled", {}, "Email address", Modifier.fillMaxWidth(), enabled = false)
                }
        }
    }
}
