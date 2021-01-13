package com.jakode.subtitle.view

import androidx.compose.desktop.AppManager
import androidx.compose.desktop.DesktopTheme
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jakode.subtitle.RESOURCE_BUNDLE
import com.jakode.subtitle.model.FileRename
import com.jakode.subtitle.utils.Assets
import com.jakode.subtitle.utils.colors
import kotlinx.coroutines.*
import java.awt.Desktop
import java.awt.Dimension
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import javax.swing.JFileChooser
import kotlin.properties.Delegates.observable

// Window size
const val DEFAULT_WIDTH = 800
const val DEFAULT_HEIGHT = 600

// Theme observe
var isInDarkMode by observable(false) { _, oldValue, newValue ->
    onIsInDarkModeChanged?.let { it(oldValue, newValue) }
}
private var onIsInDarkModeChanged: ((Boolean, Boolean) -> Unit)? = null

// File selected to process
private val selectedFiles = mutableListOf<File>()

// Coroutine job
private lateinit var job: Job

// Lambda function
lateinit var funImport: () -> Unit
lateinit var funSelectAll: () -> Unit

// Model class
lateinit var fileRename: FileRename

@ExperimentalFoundationApi
@Composable
fun subtitleContent() {
    // Color observe
    val colors = remember { mutableStateOf(colors()) }
    onIsInDarkModeChanged = { _, _ ->
        colors.value = colors()
    }

    val path = remember { mutableStateOf(TextFieldValue(System.getProperty("user.home"))) }
    val checksums = remember { mutableStateOf<List<String>>(emptyList()) }
    val selected = remember { mutableStateMapOf<Int, Boolean>() }
    val scanning = remember { mutableStateOf(false) }
    val folderIsEmpty = remember { mutableStateOf(false) }

    MaterialTheme(colors = colors.value, shapes = Shapes(medium = RoundedCornerShape(16.dp))) {
        DesktopTheme {
            Surface(Modifier.fillMaxSize()) {
                Box {
                    searchBox(path, scanning, folderIsEmpty, selected, checksums)

                    if (scanning.value) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        if (folderIsEmpty.value) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        bitmap = imageFromResource("drawable/ic_folder.png"),
                                        modifier = Modifier.size(100.dp)
                                    )
                                    Text(text = RESOURCE_BUNDLE.getString("empty.folder"))
                                }
                            }
                        } else {
                            Column {
                                ThirdRow(selected, checksums.value)
                            }
                        }
                    }
                }
            }
        }
    }

    val target = object : DropTarget() {
        @Synchronized
        override fun drop(evt: DropTargetDropEvent) {
            try {
                evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                val droppedFiles = evt.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
                droppedFiles.first()?.let {
                    path.value = TextFieldValue((it as File).absolutePath)
                }
                funImport()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    val window = AppManager.windows.first().window
    window.apply {
        iconImage = Assets.WindowIcon
        minimumSize = Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)
        contentPane.dropTarget = target
    }
}

@Composable
fun searchBox(
    path: MutableState<TextFieldValue>,
    scanning: MutableState<Boolean>,
    folderIsEmpty: MutableState<Boolean>,
    selected: SnapshotStateMap<Int, Boolean>,
    checksums: MutableState<List<String>>,
) {
    val showConfirmDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }

    val enabled = scanning.value || File(path.value.text).isDirectory

    funImport = {
        if (enabled) {
            CoroutineScope(Dispatchers.IO).launch {
                scanning.value = if (!scanning.value) {
                    job = launch {
                        fileRename = FileRename(pathDir = path.value.text)
                        folderIsEmpty.value = fileRename.isEmptyFolder
                        println("Job is done")

                        checksums.value = fileRename.allFiles.map { it.absolutePath }
                        scanning.value = false
                    }

                    selected.clear()
                    selectedFiles.clear()
                    checksums.value = emptyList()
                    true
                } else {
                    println("Cancelling...")
                    if (::job.isInitialized) job.cancelAndJoin()
                    println("Job is cancelled...")

                    false
                }
            }
        }
    }

    val funJFC: () -> Unit = {
        val chooser = JFileChooser()
        chooser.apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            showOpenDialog(null)
            selectedFile?.let {
                path.value = TextFieldValue(it.absolutePath)
                funImport()
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        TextField(
            value = path.value,
            singleLine = true,
            keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Search),
            onImeActionPerformed = { _, _ ->
                funImport()
            },
            placeholder = {
                Text(RESOURCE_BUNDLE.getString("base.directory"))
            },
            modifier = Modifier
                .height(56.dp)
                .alignBy(LastBaseline)
                .weight(1.0f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, MaterialTheme.colors.secondary, RoundedCornerShape(16.dp)),
            onValueChange = {
                path.value = it
            }
        )
        spacer(8.dp)
        Button(
            modifier = Modifier.preferredSize(56.dp),
            onClick = funImport,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp)
        ) {
            Image(
                bitmap = if (scanning.value) imageFromResource("drawable/ic_cancel.png")
                else imageFromResource("drawable/ic_search.png"),
                modifier = Modifier.fillMaxSize()
            )
        }
        spacer(8.dp)
        Button(
            modifier = Modifier.preferredSize(56.dp),
            onClick = funJFC,
            shape = RoundedCornerShape(16.dp)
        ) {
            Image(
                bitmap = imageFromResource("drawable/ic_import.png"),
                modifier = Modifier.fillMaxSize()
            )
        }
        spacer(8.dp)
        Button(
            modifier = Modifier.preferredSize(56.dp),
            onClick = {
                showConfirmDialog.value = true
            },
            shape = RoundedCornerShape(16.dp),
            enabled = selected.values.count { it } != 0,
        ) {
            Image(
                bitmap = imageFromResource("drawable/ic_done.png"),
                modifier = Modifier.fillMaxSize(),
            )
        }
        spacer(8.dp)
        Button(
            modifier = Modifier.preferredSize(56.dp),
            onClick = {
                showDeleteDialog.value = true
            },
            shape = RoundedCornerShape(16.dp),
            enabled = selected.values.count { it } != 0,
        ) {
            Image(
                bitmap = imageFromResource("drawable/ic_trash.png"),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
    ConfirmDialog(showConfirmDialog, selectedFiles)
    DeleteDialog(showDeleteDialog, selectedFiles)
}

@ExperimentalFoundationApi
@Composable
fun ThirdRow(
    selected: SnapshotStateMap<Int, Boolean>,
    checksums: List<String>,
) {
    val items = if (checksums.isNotEmpty()) FileRename.mapToFile(checksums) else listOf()
    selectedFiles.clear()
    selected.entries.forEach { entry ->
        if (entry.value) selectedFiles.add(items[entry.key])
    }

    funSelectAll = {
        if (selectedFiles.size < items.size) {
            selected.clear()
            selectedFiles.clear()

            items.forEachIndexed { index, file ->
                selected[index] = true
                selectedFiles.add(index, file)
            }
        } else {
            selected.clear()
            selectedFiles.clear()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 104.dp, end = 24.dp, bottom = 24.dp)
    ) {
        val state = rememberLazyListState()

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(end = 10.dp),
            state = state
        ) {
            itemsIndexed(items) { index, item ->
                val current = selected[index] ?: false
                ListItem(
                    secondaryText = { Text(item.parent) },
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                selected[index] = !current
                            },
                            onDoubleClick = {
                                Desktop.getDesktop().open(item)
                            }
                        )
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (current) MaterialTheme.colors.primaryVariant else Color.Transparent),
                    text = { Text(item.name) }
                )
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state,
                itemCount = items.size,
                averageItemSize = 72.dp // TextBox height + Spacer height
            )
        )
    }
}

@Composable
fun spacer(dp: Dp) {
    Spacer(modifier = Modifier.width(dp))
}