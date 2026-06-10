package com.romit.securebox.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font as GoogleFontProviderFont
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.romit.securebox.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bodyFontFamily = FontFamily(
    GoogleFontProviderFont(
        googleFont = GoogleFont("Roboto"),
        fontProvider = provider,
    )
)

val displayFontFamily = FontFamily(
    GoogleFontProviderFont(
        googleFont = GoogleFont("Roboto"),
        fontProvider = provider,
    )
)

@OptIn(ExperimentalTextApi::class)
val CustomFontFamily = FontFamily(
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.ExtraLight,
        variationSettings = FontVariation.Settings(FontWeight.ExtraLight, FontStyle.Normal)
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(FontWeight.Light, FontStyle.Normal)
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontWeight.Normal, FontStyle.Normal)
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontWeight.Medium, FontStyle.Normal)
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontWeight.SemiBold, FontStyle.Normal)
    ),
    Font(
        resId = R.font.bricolage_grotesque_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontWeight.Bold, FontStyle.Normal)
    )
)

// Default Material 3 typography values
val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = CustomFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = CustomFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)
