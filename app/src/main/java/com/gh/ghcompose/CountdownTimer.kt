package com.gh.ghcompose

import android.util.Log
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

@Composable
fun CountDownTimerParent() {
    var repeat by remember { mutableStateOf(10) }

    Column {
        CountdownTimer(repeat) {
            Log.d("ggggg", "完成-----")
        }
//        Button(onClick = {
//            repeat++
//        }) {
//            Text("重新开始")
//        }
    }

}

/**
 * 倒计时
 */
@Composable
fun CountdownTimer(initialTime: Int, onFinished: () -> Unit) {
    var timeLief by remember(initialTime) { mutableStateOf(initialTime) }
    var isRunning by remember { mutableStateOf(true) }
    var shouldRestart by remember { mutableStateOf(false) }

    LaunchedEffect(shouldRestart) {
        if (shouldRestart && isRunning) {
            timeLief = initialTime
            shouldRestart = false
        }
    }

    LaunchedEffect(key1 = isRunning, key2 = shouldRestart) {
        if (isRunning && !shouldRestart) {
            while (timeLief > 0 && isRunning) {
                delay(1000)
                timeLief--
            }
            if (timeLief <= 0)
                onFinished()
        }
    }

    Column {
        Text("剩余时间:$timeLief")
        Button(onClick = { isRunning = !isRunning }) {
            Text(if (isRunning) "暂停" else "继续")
        }
        Button(onClick = {
            shouldRestart = true
            timeLief = initialTime
            isRunning = true
        }) {
            Text("重置")
        }
    }


}