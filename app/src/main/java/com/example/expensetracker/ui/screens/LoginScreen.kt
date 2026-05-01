package com.example.expensetracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.expensetracker.R
import com.example.expensetracker.ui.theme.*

@Composable
fun LoginScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Image area with maximum impact
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f), // Reduced weight to pull the form up
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.clover_logo),
                contentDescription = "Clover Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Refined Gradient Overlay: Clear at the top for "Aura", clean fade at the bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.6f to Color.Transparent, // Adjusted gradient start
                            1f to Color.White           // Clean blend only at the bottom
                        )
                    )
            )
        }

        // Login Form area - Centered and elegant positioning
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.1f) // Increased weight
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color.White)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Reduced from 24.dp to move content up
            
            // Premium Welcome Header
            Text(
                text = "Welcome to",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextGray,
                letterSpacing = 1.sp
            )
            Text(
                text = "CloverSave",
                fontSize = 46.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryRed,
                letterSpacing = (-1.5).sp,
                lineHeight = 52.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Manage your expense with a class.",
                fontSize = 15.sp,
                color = DeepBlack.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp)) // Slightly reduced spacer

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email_address)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryRed,
                    unfocusedTextColor = PrimaryRed,
                    unfocusedBorderColor = Taupe.copy(alpha = 0.3f),
                    focusedBorderColor = PrimaryRed,
                    cursorColor = PrimaryRed,
                    focusedLabelColor = PrimaryRed
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = TextGray)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryRed,
                    unfocusedTextColor = PrimaryRed,
                    unfocusedBorderColor = Taupe.copy(alpha = 0.3f),
                    focusedBorderColor = PrimaryRed,
                    cursorColor = PrimaryRed,
                    focusedLabelColor = PrimaryRed
                )
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { /* TODO */ }) {
                    Text(
                        text = stringResource(R.string.forgot_password),
                        color = PrimaryRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.login),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.not_a_member), color = TextGray, fontSize = 14.sp)
                TextButton(onClick = onRegisterClick, contentPadding = PaddingValues(horizontal = 4.dp)) {
                    Text(
                        text = stringResource(R.string.register_now),
                        color = PrimaryRed,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
