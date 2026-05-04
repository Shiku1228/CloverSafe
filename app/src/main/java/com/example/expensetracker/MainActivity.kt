package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.screens.HomeScreen
import com.example.expensetracker.ui.screens.RegisterScreen
import com.example.expensetracker.ui.screens.PinLoginScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                val navController = rememberNavController()
                val userProfile by viewModel.userProfile.observeAsState()
                
                // Determine start destination based on whether user is registered
                val startDestination = if (userProfile == null) "register" else "pin_login"
                
                // Use a key to re-evaluate NavHost when userProfile state transitions from null to non-null
                // but usually NavHost startDestination is set once. 
                // However, userProfile might take a moment to load from DB.
                
                if (userProfile != null || !isFinishing) {
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = { name, email, pin ->
                                    viewModel.registerUser(name, email, pin)
                                    navController.navigate("home") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("pin_login") {
                            userProfile?.pin?.let { savedPin ->
                                PinLoginScreen(
                                    savedPin = savedPin,
                                    onLoginSuccess = {
                                        navController.navigate("home") {
                                            popUpTo("pin_login") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                        composable("home") {
                            HomeScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
