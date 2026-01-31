package com.margarita.margaritajetbrainsplugin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.extapi.psi.ASTWrapperPsiElement

object MargaritaElementFactory {
    fun createElement(node: ASTNode): PsiElement {
        // Use ASTWrapperPsiElement to create a simple PSI wrapper for the node.
        return ASTWrapperPsiElement(node)
    }
}
