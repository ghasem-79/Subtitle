package com.jakode.subtitle.view

import androidx.compose.desktop.AppManager
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DesktopDialogProperties
import androidx.compose.ui.window.Dialog
import com.jakode.subtitle.RESOURCE_BUNDLE
import com.jakode.subtitle.utils.Assets
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.event.MouseInputAdapter

@Composable
fun ConfirmDialog(showDialog: MutableState<Boolean>, selectedFiles: MutableList<File>) {
    if (showDialog.value) {
        val videoFormat = remember { mutableStateOf(TextFieldValue("")) }
        val subtitleFormat = remember { mutableStateOf(TextFieldValue("")) }
        val videoSubstringUnique = remember { mutableStateOf(TextFieldValue("")) }
        val subtitleSubstringUnique = remember { mutableStateOf(TextFieldValue("")) }
        val newName = remember { mutableStateOf(TextFieldValue("")) }

        val focusRequester1 = remember { FocusRequester() }
        val focusRequester2 = remember { FocusRequester() }
        val focusRequester3 = remember { FocusRequester() }
        val focusRequester4 = remember { FocusRequester() }

        val funRename = {
            fileRename.apply {
                if (videoFormat.value.text.isNotEmpty())
                    this.videoFormat = videoFormat.value.text
                if (subtitleFormat.value.text.isNotEmpty())
                    this.subtitleFormat = subtitleFormat.value.text
                if (videoSubstringUnique.value.text.isNotEmpty())
                    this.videoSubstringUnique = videoSubstringUnique.value.text
                if (subtitleSubstringUnique.value.text.isNotEmpty())
                    this.subtitleSubstringUnique = subtitleSubstringUnique.value.text
                if (newName.value.text.isNotEmpty())
                    this.newName = newName.value.text
            }
            fileRename.rename(selectedFiles)
        }

        val confirm = {
            println("Confirm")
            funRename()
            funImport()
            showDialog.value = false
        }

        val cancel = {
            println("cancel")
            showDialog.value = false
        }

        Dialog(
            onDismissRequest = {},
            properties = DesktopDialogProperties(
                size = IntSize(400, 390),
                undecorated = true
            ),
        ) {
            Surface(
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.secondary
                        )
                        .padding(24.dp)
                ) {
                    TextField(
                        value = videoFormat.value,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Next),
                        onImeActionPerformed = { _, _ -> focusRequester1.requestFocus() },
                        placeholder = {
                            Text(RESOURCE_BUNDLE.getString("video.format"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, MaterialTheme.colors.secondary, RoundedCornerShape(16.dp)),
                        onValueChange = {
                            videoFormat.value = it
                        }
                    )
                    spacer(4.dp)
                    TextField(
                        value = subtitleFormat.value,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Next),
                        onImeActionPerformed = { _, _ -> focusRequester2.requestFocus() },
                        placeholder = {
                            Text(RESOURCE_BUNDLE.getString("subtitle.format"))
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester1)
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, MaterialTheme.colors.secondary, RoundedCornerShape(16.dp)),
                        onValueChange = {
                            subtitleFormat.value = it
                        }
                    )
                    spacer(4.dp)
                    TextField(
                        value = videoSubstringUnique.value,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Next),
                        onImeActionPerformed = { _, _ -> focusRequester3.requestFocus() },
                        placeholder = {
                            Text(RESOURCE_BUNDLE.getString("video.substring.unique"))
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester2)
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, MaterialTheme.colors.secondary, RoundedCornerShape(16.dp)),
                        onValueChange = {
                            videoSubstringUnique.value = it
                        }
                    )
                    spacer(4.dp)
                    TextField(
                        value = subtitleSubstringUnique.value,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Next),
                        onImeActionPerformed = { _, _ -> focusRequester4.requestFocus() },
                        placeholder = {
                            Text(RESOURCE_BUNDLE.getString("subtitle.substring.unique"))
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester3)
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, MaterialTheme.colors.secondary, RoundedCornerShape(16.dp)),
                        onValueChange = {
                            subtitleSubstringUnique.value = it
                        }
                    )
                    spacer(4.dp)
                    TextField(
                        value = newName.value,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Done),
                        onImeActionPerformed = { _, _ -> confirm() },
                        placeholder = {
                            Text(RESOURCE_BUNDLE.getString("new.name"))
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester4)
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, MaterialTheme.colors.secondary, RoundedCornerShape(16.dp)),
                        onValueChange = {
                            newName.value = it
                        }
                    )
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
                                text = RESOURCE_BUNDLE.getString("confirm"),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
        windowSettings({}, cancel)
    }
}

fun windowSettings(confirm: () -> Unit, cancel: () -> Unit) {
    val window = AppManager.windows.last().window
    window.iconImage = Assets.WindowIcon
    val component = window.contentPane.getComponent(0)
    val adapter = object : MouseInputAdapter() {
        private lateinit var initialClick: Point

        override fun mousePressed(e: MouseEvent) {
            initialClick = e.point
        }

        override fun mouseDragged(e: MouseEvent) {
            val dx = e.x - initialClick.x
            val dy = e.y - initialClick.y
            window.setLocation(window.location.x + dx, window.location.y + dy)
        }
    }
    component.addKeyListener(object : KeyListener {
        override fun keyPressed(e: KeyEvent?) {
            e?.let {
                when (e.keyCode) {
                    KeyEvent.VK_ESCAPE -> cancel()
                    KeyEvent.VK_ENTER -> confirm()
                }
            }
        }

        override fun keyTyped(e: KeyEvent?) {}
        override fun keyReleased(e: KeyEvent?) {}
    })
    component.addMouseMotionListener(adapter)
    component.addMouseListener(adapter)
}