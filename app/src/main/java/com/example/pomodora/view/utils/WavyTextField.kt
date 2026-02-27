package com.example.pomodora.view.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pomodora.ui.theme.FieldBorder
import com.example.pomodora.ui.theme.GlassBlack
import com.example.pomodora.ui.theme.MintAccent

@Composable
fun WavyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: () -> Unit = {}
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val borderBrush = remember {
        Brush.verticalGradient(listOf(FieldBorder, Color.Transparent))
    }

    // OPTIMIZATION: Cache the VisualTransformation
    val visualTransformation = remember(isPassword, isPasswordVisible) {
        if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(GlassBlack, RoundedCornerShape(30.dp)) // Dark semi-transparent bg
            .border(
                width = 1.dp,
                // A subtle gradient border that looks like a reflection
                brush = borderBrush,
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            // 2. Transparent Colors
            // We make the TextField itself transparent so the Box design shows through
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = MintAccent,
                focusedIndicatorColor = Color.Transparent, // Remove underline
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(alpha = 0.8f)
            ),
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = MintAccent) },

            // 3. The Eye Icon Logic (Same as before, just styled)
            trailingIcon = if (isPassword) {
                {
                    val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = MintAccent)
                    }
                }
            } else null,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(onDone = { onAction() }, onNext = { onAction() }),
            singleLine = true
        )
    }
}