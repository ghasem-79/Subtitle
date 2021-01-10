package com.jakode.subtitle

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*

private lateinit var job: Job

fun main() = Window {
    MaterialTheme {
        val buttonState = remember { mutableStateOf(false) }
        val text = remember { mutableStateOf("Start") }
        val progress = remember { mutableStateOf(0F) }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier.size(200.dp, 60.dp),
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        buttonState.value = if (!buttonState.value) {
                            job = launch {
                                repeat(100) { index ->
                                    delay(50)
                                    println("Job is running ${index + 1} ...")
                                    progress.value = ((index + 1) * 1) / 100F
                                }

                                text.value = "Start Again"
                                println("Job is done")
                                buttonState.value = false
                            }

                            text.value = "Stop"
                            true
                        } else {
                            println("Cancelling...")
                            if (::job.isInitialized) job.cancelAndJoin()
                            println("Job is cancelled...")

                            text.value = "Start Again"
                            false
                        }
                    }
                },
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(
                    text = text.value,
                    fontSize = 24.sp
                )
            }

            Card(
                modifier = Modifier.padding(16.dp),
                shape = CircleShape,
                elevation = 0.dp
            ) {
                LinearProgressIndicator(
                    progress = progress.value,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                )
            }
        }
    }
}