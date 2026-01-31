package com.margarita.margaritajetbrainsplugin

import com.intellij.openapi.fileTypes.LanguageFileType

class MargaritaScriptLanguageType : LanguageFileType(MargaritaScriptLanguage.INSTANCE) {
    override fun getName() = "Margarita File"
    override fun getDescription() = "Margarita language file"
    override fun getDefaultExtension() = "mg"
    override fun getIcon() = MargaritaIcons.FILE

    companion object {
        @JvmField
        val INSTANCE: MargaritaScriptLanguageType = MargaritaScriptLanguageType()
    }
}