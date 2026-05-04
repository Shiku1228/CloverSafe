package com.example.expensetracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R
import com.example.expensetracker.ui.theme.*

@Composable
fun PinLoginScreen(savedPin: String, onLoginSuccess: () -> Unit) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Image area with Fade effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f), // Reduced height for top image to give more room for keypad
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.clover_logo),
                contentDescription = "Clover Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.5f to Color.Transparent,
                            1f to Color.White
                        )
                    )
            )
        }

        // Pin Input Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.7f) // Increased weight to give more breathing room
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color.White)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
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

            Spacer(modifier = Modifier.height(40.dp))

            // PIN Display (Using Vector Clover Icon)
            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(36.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (index < enteredPin.length) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_clover_pin),
                                    contentDescription = "Pin Entered",
                                    tint = PrimaryRed,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(if (index < enteredPin.length) PrimaryRed else LightGray.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = PrimaryRed,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Number Pad (More spacious layout)
            val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "DEL")
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp) // More vertical gap between rows
            ) {
                for (i in 0 until 4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (j in 0 until 3) {
                            val text = numbers[i * 3 + j]
                            if (text.isEmpty()) {
                                Spacer(modifier = Modifier.size(72.dp))
                            } else {
                                PinNumberButton(
                                    text = text,
                                    onClick = {
                                        if (text == "DEL") {
                                            if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                            errorMessage = ""
                                        } else if (enteredPin.length < 4) {
                                            enteredPin += text
                                            errorMessage = ""
                                            if (enteredPin.length == 4) {
                                                if (enteredPin == savedPin) {
                                                    onLoginSuccess()
                                                } else {
                                                    errorMessage = "Incorrect PIN. Try again."
                                                    enteredPin = ""
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinNumberButton(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent,
        modifier = Modifier.size(72.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (text == "DEL") {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Delete",
                    tint = PrimaryRed,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = text,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack
                )
            }
        }
    }
}
