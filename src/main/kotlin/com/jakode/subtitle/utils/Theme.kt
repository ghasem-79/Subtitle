package com.jakode.subtitle.utils

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.fontFamily
import androidx.compose.ui.text.platform.font
import androidx.compose.ui.unit.dp
import com.jakode.subtitle.utils.Colors.Background
import com.jakode.subtitle.utils.Colors.DarkBackground
import com.jakode.subtitle.utils.Colors.DarkError
import com.jakode.subtitle.utils.Colors.DarkOnBackground
import com.jakode.subtitle.utils.Colors.DarkOnError
import com.jakode.subtitle.utils.Colors.DarkOnPrimary
import com.jakode.subtitle.utils.Colors.DarkOnSecondary
import com.jakode.subtitle.utils.Colors.DarkOnSurface
import com.jakode.subtitle.utils.Colors.DarkPrimary
import com.jakode.subtitle.utils.Colors.DarkPrimaryVariant
import com.jakode.subtitle.utils.Colors.DarkSecondary
import com.jakode.subtitle.utils.Colors.DarkSurface
import com.jakode.subtitle.utils.Colors.Error
import com.jakode.subtitle.utils.Colors.OnBackground
import com.jakode.subtitle.utils.Colors.OnError
import com.jakode.subtitle.utils.Colors.OnPrimary
import com.jakode.subtitle.utils.Colors.OnSecondary
import com.jakode.subtitle.utils.Colors.OnSurface
import com.jakode.subtitle.utils.Colors.Primary
import com.jakode.subtitle.utils.Colors.PrimaryVariant
import com.jakode.subtitle.utils.Colors.Secondary
import com.jakode.subtitle.utils.Colors.SecondaryVariant
import com.jakode.subtitle.utils.Colors.Surface
import com.jakode.subtitle.view.isInDarkMode

// Paddings
val SUBTITLE_PADDING = 4.dp

// Theme colors
val lightThemeColors = lightColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary,
    secondaryVariant = SecondaryVariant,
    background = Background,
    surface = Surface,
    error = Error,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = OnBackground,
    onSurface = OnSurface,
    onError = OnError
)

val darkThemeColors = darkColors(
    primary = DarkPrimary,
    primaryVariant = DarkPrimaryVariant,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    error = DarkError,
    onPrimary = DarkOnPrimary,
    onSecondary = DarkOnSecondary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onError = DarkOnError
)

fun colors(): Colors = if (isInDarkMode) darkThemeColors else lightThemeColors

// Fonts
val balooFontFamily = fontFamily(
    listOf(font("baloo_regular", "font/baloo_regular.ttf"))
)
val productSansFontFamily = fontFamily(
    listOf(
        font("product_sans_regular", "font/product_sans_regular.ttf", FontWeight(400), FontStyle.Normal),
        font("product_sans_italic", "font/product_sans_italic.ttf", FontWeight(400), FontStyle.Italic),
        font("product_sans_bold", "font/product_sans_bold.ttf", FontWeight(700), FontStyle.Normal),
        font("product_sans_bold_italic", "font/product_sans_bold_italic.ttf", FontWeight(700), FontStyle.Italic)
    )
)