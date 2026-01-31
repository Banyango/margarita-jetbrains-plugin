package com.margarita.margaritajetbrainsplugin

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.intellij.lang.Language

private fun effectiveLanguage(): Language = Language.findLanguageByID("MargaritaScript") ?: MARGARITA_LANGUAGE

class MargaritaFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, effectiveLanguage()) {
    override fun getFileType() = MARGARITA_FILE_TYPE
}
