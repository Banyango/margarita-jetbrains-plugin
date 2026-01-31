package com.margarita.margaritajetbrainsplugin

import com.intellij.lang.Language

class MargaritaScriptLanguage : Language("MargaritaScript") {
    companion object {
        @JvmField
        val INSTANCE: MargaritaScriptLanguage = MargaritaScriptLanguage()
    }
}

// Top-level reference for other code to use; prefer platform-registered language by ID at runtime if available
@JvmField
val MARGARITA_LANGUAGE = MargaritaScriptLanguage.INSTANCE
