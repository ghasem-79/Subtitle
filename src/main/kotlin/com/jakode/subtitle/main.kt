package com.jakode.subtitle

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.input.key.Key
import com.github.tkuenneth.nativeparameterstoreaccess.Dconf
import com.github.tkuenneth.nativeparameterstoreaccess.Dconf.HAS_DCONF
import com.github.tkuenneth.nativeparameterstoreaccess.MacOSDefaults
import com.github.tkuenneth.nativeparameterstoreaccess.NativeParameterStoreAccess.IS_MACOS
import com.github.tkuenneth.nativeparameterstoreaccess.NativeParameterStoreAccess.IS_WINDOWS
import com.github.tkuenneth.nativeparameterstoreaccess.WindowsRegistry
import com.jakode.subtitle.view.funSelectAll
import com.jakode.subtitle.view.isInDarkMode
import com.jakode.subtitle.view.subtitleContent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import javax.swing.SwingUtilities.invokeLater

val RESOURCE_BUNDLE: ResourceBundle = ResourceBundle.getBundle("values/strings")

@ExperimentalFoundationApi
fun main() {
    // Live change theme OS
    GlobalScope.launch {
        while (isActive) {
            val newMode = isSystemInDarkTheme()
            if (isInDarkMode != newMode) {
                isInDarkMode = newMode
            }
            delay(1000)
        }
    }
    invokeLater {
        AppWindow(title = RESOURCE_BUNDLE.getString("subtitle")).also {
            it.keyboard.setShortcut(Key.Escape) { it.close() }
            it.keyboard.setShortcut(Key.A) { funSelectAll() }
        }.show {
            subtitleContent()
        }
    }
}

/**
 * Is System in dark theme
 * @return boolean
 */
fun isSystemInDarkTheme(): Boolean = when {
    IS_WINDOWS -> {
        val result = WindowsRegistry.getWindowsRegistryEntry(
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
            "AppsUseLightTheme"
        )
        result == 0x0
    }
    IS_MACOS -> {
        val result = MacOSDefaults.getDefaultsEntry("AppleInterfaceStyle")
        result == "Dark"
    }
    HAS_DCONF -> {
        val result = Dconf.getDconfEntry("/org/gnome/desktop/interface/gtk-theme")
        result.toLowerCase().contains("dark")
    }
    else -> false
}