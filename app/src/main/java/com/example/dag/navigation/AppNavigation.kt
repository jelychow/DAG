package com.example.dag.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dag.dag.FlowManager
import com.example.dag.screens.LoginScreen
import com.example.dag.screens.MainScreen
import com.example.dag.screens.PrivacyPolicyScreen
import com.example.dag.utils.rememberUserPreferences

sealed class Screen(val route: String) {
    object PrivacyPolicy : Screen("privacy_policy")
    object Login : Screen("login")
    object Main : Screen("main")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    onExit: () -> Unit
) {
    val userPreferences = rememberUserPreferences()
    val flowManager = remember { FlowManager(userPreferences) }
    
    // 根据当前流程步骤确定起始目的地
    var startDestination by remember {
        mutableStateOf(
           flowManager.getCurrentStep()
        )
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onAgree = {
                    userPreferences.setPrivacyPolicyAgreed(true)
                    // 检查下一步应该是什么
//                    if (flowManager.canNavigateTo(FlowManager.FlowStep.LOGIN)) {
//                        navController.navigate(Screen.Login.route) {
//                            popUpTo(Screen.PrivacyPolicy.route) { inclusive = true }
//                        }
//                    }
                    flowManager.navToNext(navController)

//                    navController.navigate(Screen.Main.route)
                },
                onDisagree = onExit
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { 
                    userPreferences.setLoggedIn(true)
                    // 检查下一步应该是什么
//                    if (flowManager.canNavigateTo(FlowManager.FlowStep.MAIN)) {
//                        navController.navigate(Screen.Main.route) {
//                            popUpTo(Screen.Login.route) { inclusive = true }
//                        }
//                    }
                    flowManager.navToNext(navController)

                }
            )
        }
        
        composable(Screen.Main.route) {
            MainScreen(
                onLogout = {
                    userPreferences.setLoggedIn(false)
                    // 登出后返回登录界面
//                    navController.navigate(Screen.Login.route) {
//                        popUpTo(Screen.Main.route) { inclusive = true }
//                    }
                    flowManager.navToNext(navController)
                }
            )
        }
    }
}