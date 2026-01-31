package com.margarita.margaritajetbrainsplugin

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.psi.PsiDocumentManager

private val LOG = Logger.getInstance(IncludeGotoDeclarationHandler::class.java)

class IncludeGotoDeclarationHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(sourceElement: PsiElement?, offset: Int, editor: Editor): Array<PsiElement>? {
        if (sourceElement == null) {
            return tryFindTargetFromEditor(editor, offset)
        }

        val element = sourceElement
        try {
            LOG.info("GotoDeclaration invoked on ${sourceElement::class.java.simpleName} text='${sourceElement.text}'")
        } catch (_: Throwable) {}

        val elementType = element.node?.elementType
        if (elementType != MargaritaTokenTypes.INCLUDE_FILENAME) {
            // Try to find a more precise element at the caret
            val alternative = tryFindTargetFromEditor(editor, offset)
            if (alternative != null) return alternative
            return null
        }

        return resolveForElement(element)
    }

    private fun tryFindTargetFromEditor(editor: Editor, offset: Int): Array<PsiElement>? {
        val project = editor.project ?: return null
        val doc = editor.document
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(doc) ?: return null
        val pos = (offset - 1).coerceAtLeast(0)
        val leaf = psiFile.findElementAt(pos) ?: return null
        if (leaf.node?.elementType != MargaritaTokenTypes.INCLUDE_FILENAME) return null
        return resolveForElement(leaf)
    }

    private fun resolveForElement(element: PsiElement): Array<PsiElement>? {
        val raw = element.text.trim()
        if (raw.isEmpty()) return null
        val filename = if ((raw.startsWith("\"") && raw.endsWith("\"")) || (raw.startsWith("'") && raw.endsWith("'"))) raw.substring(1, raw.length - 1) else raw

        val project: Project = element.project

        // Try relative path
        val containing = element.containingFile ?: return null
        val vFile = containing.virtualFile ?: return null
        val parent = vFile.parent ?: return null
        var relativeTarget: VirtualFile? = parent.findFileByRelativePath(filename)
        if (relativeTarget != null) {
            val psi = PsiManager.getInstance(project).findFile(relativeTarget)
            if (psi != null) return arrayOf(psi)
        }
        // If not found and filename has no extension, try adding .mg
        if (!filename.contains('.')) {
            relativeTarget = parent.findFileByRelativePath("$filename.mg")
            if (relativeTarget != null) {
                val psi = PsiManager.getInstance(project).findFile(relativeTarget)
                if (psi != null) return arrayOf(psi)
            }
        }

        // Fallback: search by basename in project
        val basename = filename.substringAfterLast('/')
        val virtuals = FilenameIndex.getVirtualFilesByName(project, basename, GlobalSearchScope.projectScope(project))
        if (virtuals.isNotEmpty()) {
            val psi = PsiManager.getInstance(project).findFile(virtuals.first())
            if (psi != null) return arrayOf(psi)
        }

        // Try basename + .mg when no extension using virtuals
        if (!basename.contains('.')) {
            val virtuals2 = FilenameIndex.getVirtualFilesByName(project, "$basename.mg", GlobalSearchScope.projectScope(project))
            if (virtuals2.isNotEmpty()) {
                val psi = PsiManager.getInstance(project).findFile(virtuals2.first())
                if (psi != null) return arrayOf(psi)
            }
        }

        // Extra fallback: use FilenameIndex.getFilesByName (returns PsiFile[]) which sometimes succeeds where virtuals lookups do not
        if (!basename.contains('.')) {
            val psiFiles = FilenameIndex.getFilesByName(project, "$basename.mg", GlobalSearchScope.projectScope(project))
            if (psiFiles.isNotEmpty()) return arrayOf(psiFiles[0])
        }

        return null
    }

    override fun getActionText(context: DataContext): String? = null
}
