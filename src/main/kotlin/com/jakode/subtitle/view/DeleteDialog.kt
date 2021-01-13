package com.jakode.subtitle.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DesktopDialogProperties
import androidx.compose.ui.window.Dialog
import com.jakode.subtitle.RESOURCE_BUNDLE
import com.jakode.subtitle.model.FileRename
import java.io.File

@Composable
fun DeleteDialog(showDialog: MutableState<Boolean>, selectedFiles: MutableList<File>) {
    if (showDialog.value) {
        val state = rememberScrollState(0f)

        val confirm = {
            println("Confirm")
            selectedFiles.forEach { file ->
                FileRename.deleteFile(file)
            }
            funImport()
            showDialog.value = false
        }

        val cancel = {
            println("cancel delete")
            showDialog.value = false
        }

        Dialog(
            onDismissRequest = {},
            properties = DesktopDialogProperties(
                size = IntSize(400, 300),
                undecorated = true
            ),
        ) {
            Surface(
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colors.secondary
                    ).padding(24.dp)
                ) {
                    Text(
                        text = RESOURCE_BUNDLE.getString("confirm.deletion"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    Box(modifier = Modifier.height(150.dp)) {
                        ScrollableColumn(
                            modifier = Modifier.fillMaxSize().padding(end = 12.dp),
                            scrollState = state
                        ) {
                            val sb = StringBuilder()
                            selectedFiles.forEach { file ->
                                sb.append(file.name).appendLine()
                            }
                            Text(sb.toString())
                        }
                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(state)
                        )
                    }
                    Spacer(modifier = Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(44.dp).padding(start = 4.dp, end = 2.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { cancel() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxHeight().weight(1F)
                        ) {
                            Text(
                                text = RESOURCE_BUNDLE.getString("cancel"),
                                fontSize = 16.sp
                            )
                        }
                        spacer(8.dp)
                        Button(
                            onClick = { confirm() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxHeight().weight(1F)
                        ) {
                            Text(
                                text = RESOURCE_BUNDLE.getString("delete"),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
        windowSettings(confirm, cancel)
    }
}