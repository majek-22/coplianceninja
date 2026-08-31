package com.example.data

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManager {
    fun setLocale(languageTag: String) {
        val tags = if (languageTag == "in" || languageTag == "id") "in,id" else "en"
        val appLocale = LocaleListCompat.forLanguageTags(tags)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun getCurrentLocaleTag(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (!locales.isEmpty) {
            val primary = locales.get(0)?.language ?: "in"
            if (primary == "en") "en" else "in"
        } else {
            "in"
        }
    }
}
