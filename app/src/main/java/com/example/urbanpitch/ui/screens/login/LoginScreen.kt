package com.example.urbanpitch.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.urbanpitch.R
import com.example.urbanpitch.ui.UrbanPitchRoute

@Composable
fun LoginScreen(
    navController: NavController,
    onLogin: (String, String) -> Unit,
    onRegister: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Login",
            style = MaterialTheme.typography.headlineMedium,
            color = colors.onBackground
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = colors.onSurfaceVariant) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface,
                disabledTextColor = colors.onSurfaceVariant,
                cursorColor = colors.onSurface,
                focusedBorderColor = colors.onSurface,
                unfocusedBorderColor = colors.onSurfaceVariant,
                focusedLabelColor = colors.onSurface,
                unfocusedLabelColor = colors.onSurfaceVariant
            )
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = colors.onSurfaceVariant) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface,
                disabledTextColor = colors.onSurfaceVariant,
                cursorColor = colors.onSurface,
                focusedBorderColor = colors.onSurface,
                unfocusedBorderColor = colors.onSurfaceVariant,
                focusedLabelColor = colors.onSurface,
                unfocusedLabelColor = colors.onSurfaceVariant
            )
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            )
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onRegister) {
            Text(
                "Don’t have an account? Register",
                color = colors.primary
            )
        }
    }
}


