package com.gh.ghcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ggggg", "加载页面-----")
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
//                            LoginScreen(navController = navController)
//                            GHLoginScreen(navController = navController)
//                            LoginScreenWithSideEffects()
                            CountdownTimer(10) { Log.d("ggggg", "完成-----") }
                        }
                        composable("home") {
                            HomeScreen()
                        }
                        composable("register") {
                            // 注册页面
                            Text("注册页面")
                        }
                        composable("forgot_password") {
                            // 忘记密码页面
                            Text("忘记密码页面")
                        }
                    }
                }
            }
        }
    }
}