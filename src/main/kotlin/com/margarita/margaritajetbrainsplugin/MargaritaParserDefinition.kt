package com.margarita.margaritajetbrainsplugin

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.TokenType
import com.intellij.lang.Language

class MargaritaParserDefinition : ParserDefinition {
    companion object {
        // Resolve language lazily to avoid classloading/registration-order issues
        val FILE: IFileElementType = IFileElementType(Language.findLanguageByID("MargaritaScript") ?: MARGARITA_LANGUAGE)
    }

    override fun createLexer(project: com.intellij.openapi.project.Project) = MargaritaLexerAdapter()

    // Provide a minimal parser that creates a root marker and consumes all tokens.
    // This prevents the "Parser produced no markers" runtime error when no real parser is used yet.
    override fun createParser(project: com.intellij.openapi.project.Project): PsiParser = PsiParser { root, builder ->
        val rootMarker = builder.mark()
        while (builder.tokenType != null) {
            builder.advanceLexer()
        }
        rootMarker.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getWhitespaceTokens(): TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

    override fun getCommentTokens(): TokenSet = TokenSet.create(MargaritaTokenTypes.COMMENT)

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement = MargaritaElementFactory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = MargaritaFile(viewProvider)
}
