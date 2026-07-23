package com.definedengineering.makerfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.definedengineering.makerfit.data.ManufacturingMethod
import com.definedengineering.makerfit.ui.calculator.ToleranceCalculatorScreen
import com.definedengineering.makerfit.ui.calculator.ToleranceCalculatorState
import com.definedengineering.makerfit.ui.home.HomeSelectionScreen
import com.definedengineering.makerfit.ui.navigation.Calculator
import com.definedengineering.makerfit.ui.navigation.Home
import com.definedengineering.makerfit.ui.theme.MakersToleranceCalculatorTheme
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.MobileAds

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this) {}
        setContent {
            MakersToleranceCalculatorTheme {
                val billingManager = remember { com.definedengineering.makerfit.billing.BillingManager(this).apply { startConnection() } }
                val isPremiumUser by billingManager.isPremiumUser.collectAsState()
                val premiumPurchaseDate by billingManager.premiumPurchaseDate.collectAsState()

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Home,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable<Home> {
                        HomeSelectionScreen(
                            billingManager = billingManager,
                            isPremiumUser = isPremiumUser,
                            onMethodSelected = { method ->
                                navController.navigate(Calculator(method = method.name))
                            }
                        )
                    }
                    composable<Calculator> { backStackEntry ->
                        val route = backStackEntry.toRoute<Calculator>()
                        val initialMethod = remember(route.method) {
                            try {
                                ManufacturingMethod.valueOf(route.method)
                            } catch (e: Exception) {
                                ManufacturingMethod.FDM_3D_PRINTING
                            }
                        }
                        
                        val state = remember(initialMethod) {
                            ToleranceCalculatorState(initialMethod = initialMethod)
                        }

                        ToleranceCalculatorScreen(
                            billingManager = billingManager,
                            isPremiumUser = isPremiumUser,
                            premiumPurchaseDate = premiumPurchaseDate,
                            state = state,
                            onNavigateToHome = {
                                navController.navigate(Home) {
                                    popUpTo(Home) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}