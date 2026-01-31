package com.margarita.margaritajetbrainsplugin

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext

/**
 * Registers references for include filenames like [[ filename ]].
 * The reference resolves relative to the current file's directory first,
 * then falls back to a project-wide filename lookup. It also accepts
 * filenames without the .mg extension and will try filename + ".mg".
 */
class IncludeReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        // Use a permissive pattern which matches any PSI element with our INCLUDE_FILENAME token type.
        val pattern = PlatformPatterns.psiElement().withElementType(MargaritaTokenTypes.INCLUDE_FILENAME)
        registrar.registerReferenceProvider(pattern, object : PsiReferenceProvider() {
            override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                try {
                } catch (_: Throwable) {
                    // Avoid crashing plugin due to logging interpolation issues
                }

                val fullText = element.text ?: ""
                var raw = fullText.trim()
                if (raw.isEmpty()) return PsiReference.EMPTY_ARRAY

                // Strip surrounding quotes if present: [[ "file" ]] or [[ 'file' ]]
                if ((raw.startsWith("\"") && raw.endsWith("\"")) || (raw.startsWith("'") && raw.endsWith("'"))) {
                    raw = raw.substring(1, raw.length - 1)
                }

                // Compute range of the trimmed filename within the element text so Ctrl+click targets correct chars.
                val startInElement = fullText.indexOf(raw).coerceAtLeast(0)
                val range = TextRange(startInElement, startInElement + raw.length)

                val project = element.project
                return arrayOf(object : PsiReferenceBase<PsiElement>(element, range) {
                    override fun resolve(): PsiElement? {
                        val filename = fullText.substring(range.startOffset, range.endOffset)

                        // Try to resolve relative to the current file
                        val containing = element.containingFile ?: run {
                            return null
                        }
                        val vFile = containing.virtualFile ?: run {
                            return null
                        }
                        val parent = vFile.parent ?: run {
                            return null
                        }

                        // First try exactly the provided filename
                        var relativeTarget: VirtualFile? = parent.findFileByRelativePath(filename)
                        if (relativeTarget != null) {
                            return PsiManager.getInstance(project).findFile(relativeTarget)
                        }

                        // If not found and filename has no extension, try adding .mg
                        if (!filename.contains('.')) {
                            relativeTarget = parent.findFileByRelativePath("$filename.mg")
                            if (relativeTarget != null) {
                                return PsiManager.getInstance(project).findFile(relativeTarget)
                            }
                        }

                        // Fallback: search by basename in project
                        val basename = filename.substringAfterLast('/')
                        var virtuals = FilenameIndex.getVirtualFilesByName(project, basename, GlobalSearchScope.projectScope(project))
                        if (virtuals.isNotEmpty()) {
                            return PsiManager.getInstance(project).findFile(virtuals.first())
                        }

                        // If basename without extension didn't find anything, try basename + .mg (virtuals)
                        if (!basename.contains('.')) {
                            virtuals = FilenameIndex.getVirtualFilesByName(project, "$basename.mg", GlobalSearchScope.projectScope(project))
                            if (virtuals.isNotEmpty()) {
                                return PsiManager.getInstance(project).findFile(virtuals.first())
                            }
                        }

                        // Extra fallback: use FilenameIndex.getFilesByName (returns PsiFile[]) which sometimes succeeds where virtuals lookups do not
                        if (!basename.contains('.')) {
                            val psiFiles = FilenameIndex.getFilesByName(project, "$basename.mg", GlobalSearchScope.projectScope(project))
                            if (psiFiles.isNotEmpty()) {
                                return psiFiles[0]
                            }
                        }

                        return null
                    }

                    override fun getVariants(): Array<Any> = emptyArray()
                })
            }
        })
    }
}
