package com.gh.ghcompose

import android.content.IntentSender.OnFinished
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * 倒计时
 */
@Composable
fun CountdownTimer(initialTime: Int, onFinished: () -> Unit) {
    var timeLief by remember { mutableStateOf(initialTime) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = isRunning) {
        if (isRunning) {
            while (timeLief > 0) {
                delay(1000)
                timeLief--
            }
            onFinished()
        }
    }

    Column {
        Text("剩余时间:$timeLief")
        Button(onClick = { isRunning = !isRunning }) {
            Text(if (isRunning) "暂停" else "继续")
        }
    }


}