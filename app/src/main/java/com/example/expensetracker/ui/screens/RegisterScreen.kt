package com.example.expensetracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R
import com.example.expensetracker.ui.theme.*

@Composable
fun RegisterScreen(onRegisterSuccess: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var pinVisible by remember { mutableStateOf(false) }

    val isFormValid = name.isNotEmpty() && email.contains("@") && pin.length == 4 && pin == confirmPin

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Image area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.clover_logo),
                contentDescription = "Clover Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.7f to Color.Transparent,
                            1f to Color.White
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color.White)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to CloverSave",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextGray,
                letterSpacing = 1.sp
            )
            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryRed,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                placeholder = { Text("Enter your name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryRed) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DeepBlack,
                    unfocusedTextColor = DeepBlack,
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = LightGray,
                    focusedLabelColor = PrimaryRed,
                    cursorColor = PrimaryRed
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                placeholder = { Text("example@mail.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryRed) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DeepBlack,
                    unfocusedTextColor = DeepBlack,
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = LightGray,
                    focusedLabelColor = PrimaryRed,
                    cursorColor = PrimaryRed
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PIN Field
            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) pin = it },
                label = { Text("Set 4-Digit PIN") },
                placeholder = { Text("0000") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryRed) },
                trailingIcon = {
                    val image = if (pinVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { pinVisible = !pinVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = TextGray)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DeepBlack,
                    unfocusedTextColor = DeepBlack,
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = LightGray,
                    focusedLabelColor = PrimaryRed,
                    cursorColor = PrimaryRed
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm PIN Field
            OutlinedTextField(
                value = confirmPin,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) confirmPin = it },
                label = { Text("Confirm PIN") },
                placeholder = { Text("Repeat PIN") },
                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (pin == confirmPin && pin.isNotEmpty()) SuccessGreen else PrimaryRed) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = pin != confirmPin && confirmPin.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DeepBlack,
                    unfocusedTextColor = DeepBlack,
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = LightGray,
                    focusedLabelColor = PrimaryRed,
                    cursorColor = PrimaryRed
                )
            )

            if (pin != confirmPin && confirmPin.isNotEmpty()) {
                Text(
                    text = "PINs do not match",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { 
                    if (isFormValid) {
                        onRegisterSuccess(name, email, pin)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryRed,
                    disabledContainerColor = PrimaryRed.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = isFormValid,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "CREATE ACCOUNT",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
