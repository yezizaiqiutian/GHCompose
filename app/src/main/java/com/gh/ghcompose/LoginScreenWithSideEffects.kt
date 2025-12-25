package com.gh.ghcompose

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 登录界面 - 完整副作用示例
 * 展示了各种副作用API的用法
 */
@Composable
fun LoginScreenWithSideEffects(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {}
) {
    // ===================== 状态定义 =====================
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // 错误状态
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // 消息状态
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // 密码强度
    val passwordStrength by remember(password) {
        derivedStateOf {
            when {
                password.isEmpty() -> PasswordStrength.EMPTY
                password.length < 6 -> PasswordStrength.WEAK
                password.length < 10 -> PasswordStrength.MEDIUM
                else -> PasswordStrength.STRONG
            }
        }
    }

    // ===================== 副作用相关 =====================
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val passwordFocusRequester = remember { FocusRequester() }


    println("LoginScreen ------不用副作用")

    // 副作用1: LaunchedEffect - 自动登录检查
    LaunchedEffect(rememberMe) {
        println("LoginScreen rememberMe=$rememberMe")

        if (rememberMe) {
            // 模拟从本地存储读取保存的登录信息
            delay(500) // 模拟读取延迟
            // 这里应该是实际读取逻辑
            // val saved = prefs.getSavedCredentials()
            // if (saved != null) {
            //     email = saved.email
            //     password = saved.password
            // }
        }
    }

    // 副作用2: LaunchedEffect - 邮箱实时验证（防抖）
    LaunchedEffect(email) {
        println("LoginScreen email=$email")

        if (email.length > 3) {
            delay(300) // 防抖延迟
            if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailError = "邮箱格式不正确"
            } else {
                emailError = null
            }
        }
    }

    // 副作用3: LaunchedEffect - 自动隐藏消息
    LaunchedEffect(showSuccessMessage) {
        println("LoginScreen showSuccessMessage=$showSuccessMessage")
        if (showSuccessMessage) {
            delay(2000)
            showSuccessMessage = false
        }
    }

    LaunchedEffect(showErrorMessage) {
        println("LoginScreen showErrorMessage=$showErrorMessage")
        if (showErrorMessage) {
            delay(3000)
            showErrorMessage = false
            errorMessage = ""
        }
    }

    // 副作用4: LaunchedEffect - 加载时隐藏键盘
    LaunchedEffect(isLoading) {
        println("LoginScreen isLoading=$isLoading")
        if (isLoading) {
            focusManager.clearFocus()
        }
    }

    // 副作用5: DisposableEffect - 组件生命周期管理
    DisposableEffect(Unit) {
        println("LoginScreen 已创建")

        onDispose {
            println("LoginScreen 已销毁")
            // 清理资源，如取消网络请求等
        }
    }

    // ===================== 登录函数 =====================
    fun performLogin() {
        // 清除焦点
        focusManager.clearFocus()

        // 验证
        val emailValidation = when {
            email.isEmpty() -> "邮箱不能为空"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "请输入有效的邮箱地址"
            else -> null
        }

        val passwordValidation = when {
            password.isEmpty() -> "密码不能为空"
            password.length < 6 -> "密码至少需要6位"
            else -> null
        }

        emailError = emailValidation
        passwordError = passwordValidation

        if (emailValidation != null || passwordValidation != null) {
            return
        }

        // 开始登录
        isLoading = true

        scope.launch {
            try {
                // 模拟网络请求
                delay(2000)

                // 模拟登录逻辑
                val isSuccess = email == "admin@example.com" && password == "123456"

                if (isSuccess) {
                    // 登录成功
                    showSuccessMessage = true

                    if (rememberMe) {
                        // 保存凭证到本地存储
                        // prefs.saveCredentials(email, password)
                    }

                    // 延迟跳转，让用户看到成功消息
                    delay(1000)
                    onLoginSuccess()
                } else {
                    // 登录失败
                    showErrorMessage = true
                    errorMessage = "登录失败，请检查邮箱和密码"
                }
            } catch (e: Exception) {
                // 网络错误
                showErrorMessage = true
                errorMessage = "网络错误: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // ===================== UI布局 =====================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A11CB),
                        Color(0xFF2575FC)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 成功消息 - 使用动画
            AnimatedVisibility(
                visible = showSuccessMessage,
                enter = fadeIn(animationSpec = tween(3000)),
                exit = fadeOut(animationSpec = tween(3000))
            ) {
                SuccessMessageCard()
            }

            // 错误消息 - 使用动画
            AnimatedVisibility(
                visible = showErrorMessage,
                enter = fadeIn(animationSpec = tween(3000)),
                exit = fadeOut(animationSpec = tween(3000))
            ) {
                ErrorMessageCard(message = errorMessage)
            }

            // Logo区域
            LoginLogoCard()

            // 标题
            Text(
                text = "欢迎回来",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Text(
                text = "请登录您的账户",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // 登录表单卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 邮箱输入
                    EmailTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        error = emailError,
                        onNext = { passwordFocusRequester.requestFocus() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 密码输入
                    PasswordTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        isVisible = passwordVisible,
                        onToggleVisibility = { passwordVisible = !passwordVisible },
                        error = passwordError,
                        focusRequester = passwordFocusRequester,
                        strength = passwordStrength,
                        onDone = { performLogin() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 选项行
                    OptionsRow(
                        rememberMe = rememberMe,
                        onRememberMeChange = { rememberMe = it },
                        onForgotPasswordClick = onNavigateToForgotPassword
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 登录按钮
                    LoginButton(
                        isLoading = isLoading,
                        enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading,
                        onClick = { performLogin() }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 注册提示
                    RegisterPrompt(onClick = onNavigateToRegister)
                }
            }
        }
    }
}

// ===================== 子组件 =====================

@Composable
fun SuccessMessageCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "登录成功！正在跳转...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ErrorMessageCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun LoginLogoCard() {
    Card(
        modifier = Modifier
            .size(100.dp)
            .padding(bottom = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Logo",
                tint = Color(0xFF6A11CB),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    onNext: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("邮箱地址") },
        placeholder = { Text("example@email.com") },
        leadingIcon = {
            Icon(Icons.Default.Email, contentDescription = null)
        },
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext() }
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

enum class PasswordStrength {
    EMPTY, WEAK, MEDIUM, STRONG
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String?,
    focusRequester: FocusRequester,
    strength: PasswordStrength,
    onDone: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("密码") },
            placeholder = { Text("请输入密码") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (isVisible) "隐藏密码" else "显示密码"
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true
        )

        // 密码强度指示器
        if (value.isNotEmpty() && strength != PasswordStrength.EMPTY) {
            Spacer(modifier = Modifier.height(8.dp))
            PasswordStrengthIndicator(strength = strength)
        }
    }
}

@Composable
fun PasswordStrengthIndicator(strength: PasswordStrength) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "密码强度: ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = when (strength) {
                PasswordStrength.WEAK -> "弱"
                PasswordStrength.MEDIUM -> "中"
                PasswordStrength.STRONG -> "强"
                else -> ""
            },
            style = MaterialTheme.typography.labelSmall,
            color = when (strength) {
                PasswordStrength.WEAK -> Color.Red
                PasswordStrength.MEDIUM -> Color(0xFFFFA500) // 橙色
                PasswordStrength.STRONG -> Color.Green
                else -> Color.Transparent
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 强度条
        Box(
            modifier = Modifier
                .height(4.dp)
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(2.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth(
                        when (strength) {
                            PasswordStrength.WEAK -> 0.33f
                            PasswordStrength.MEDIUM -> 0.66f
                            PasswordStrength.STRONG -> 1f
                            else -> 0f
                        }
                    )
                    .background(
                        color = when (strength) {
                            PasswordStrength.WEAK -> Color.Red
                            PasswordStrength.MEDIUM -> Color(0xFFFFA500)
                            PasswordStrength.STRONG -> Color.Green
                            else -> Color.Transparent
                        },
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun OptionsRow(
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = onRememberMeChange
            )
            Text(
                text = "记住我",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        TextButton(onClick = onForgotPasswordClick) {
            Text("忘记密码?")
        }
    }
}

@Composable
fun LoginButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6A11CB)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "登录",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun RegisterPrompt(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "还没有账户?--- ",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(
            onClick = onClick,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Text("立即注册")
        }
    }
}

// ===================== 预览 =====================

//@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
//@Composable
//fun LoginScreenWithSideEffectsPreview() {
//    MaterialTheme {
//        LoginScreenWithSideEffects(
//            onLoginSuccess = { println("登录成功") },
//            onNavigateToRegister = { println("跳转到注册") },
//            onNavigateToForgotPassword = { println("跳转到忘记密码") }
//        )
//    }
//}
//
//@Preview(showBackground = true, widthDp = 360, heightDp = 640)
//@Composable
//fun LoginScreenWithSideEffectsPreview_Phone() {
//    MaterialTheme {
//        LoginScreenWithSideEffects()
//    }
//}
//
//@Preview(
//    showBackground = true,
//    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
//)
//@Composable
//fun LoginScreenWithSideEffectsPreview_Dark() {
//    MaterialTheme {
//        LoginScreenWithSideEffects()
//    }
//}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun ErrorMessageCard_Preview() {
    MaterialTheme {
        ErrorMessageCard("啊啊啊啊")
    }
}
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun SuccessMessageCard_Preview() {
    MaterialTheme {
        SuccessMessageCard()
    }
}

