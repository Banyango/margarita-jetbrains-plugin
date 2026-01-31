package com.margarita.margaritajetbrainsplugin

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class MargaritaSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        // Margarita keywords
        val KEYWORD: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val NUMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.NUMBER", DefaultLanguageHighlighterColors.NUMBER)

        // Delimiters
        val DELIMITER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.DELIMITER", DefaultLanguageHighlighterColors.OPERATION_SIGN)

        // Markdown block delimiters
        val MARKDOWN_DELIMITER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.MD_DELIMITER", DefaultLanguageHighlighterColors.BRACES)

        // Markdown content highlighting
        val MD_HEADER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.MD_HEADER", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG)
        val MD_BOLD: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.MD_BOLD", DefaultLanguageHighlighterColors.KEYWORD)
        val MD_ITALIC: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.MD_ITALIC", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE)
        val MD_CODE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.MD_CODE", DefaultLanguageHighlighterColors.STRING)
        val MD_LINK: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.MD_LINK", DefaultLanguageHighlighterColors.IDENTIFIER)
        val MD_LIST: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.MD_LIST", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        val MD_CONTENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.MD_CONTENT", DefaultLanguageHighlighterColors.DOC_COMMENT)

        // Include syntax
        val INCLUDE_DELIMITER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.INCLUDE_DELIMITER", DefaultLanguageHighlighterColors.BRACKETS)
        val INCLUDE_FILENAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.INCLUDE_FILENAME", DefaultLanguageHighlighterColors.STRING)

        // Metadata
        val METADATA_DELIMITER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.METADATA_DELIMITER", DefaultLanguageHighlighterColors.BRACES)
        val METADATA_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.METADATA_KEY", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        val METADATA_VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.METADATA_VALUE", DefaultLanguageHighlighterColors.STRING)

        // Variable interpolation
        val VAR_DELIMITER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.VAR_DELIM", DefaultLanguageHighlighterColors.BRACES)
        val VAR_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MARGARITA.VAR_NAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD)

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val DELIMITER_KEYS = arrayOf(DELIMITER)
        private val MARKDOWN_DELIMITER_KEYS = arrayOf(MARKDOWN_DELIMITER)
        private val MD_HEADER_KEYS = arrayOf(MD_HEADER)
        private val MD_BOLD_KEYS = arrayOf(MD_BOLD)
        private val MD_ITALIC_KEYS = arrayOf(MD_ITALIC)
        private val MD_CODE_KEYS = arrayOf(MD_CODE)
        private val MD_LINK_KEYS = arrayOf(MD_LINK)
        private val MD_LIST_KEYS = arrayOf(MD_LIST)
        private val MD_CONTENT_KEYS = arrayOf(MD_CONTENT)
        private val INCLUDE_DELIMITER_KEYS = arrayOf(INCLUDE_DELIMITER)
        private val INCLUDE_FILENAME_KEYS = arrayOf(INCLUDE_FILENAME)
        private val METADATA_DELIMITER_KEYS = arrayOf(METADATA_DELIMITER)
        private val METADATA_KEY_KEYS = arrayOf(METADATA_KEY)
        private val METADATA_VALUE_KEYS = arrayOf(METADATA_VALUE)
        private val VAR_DELIM_KEYS = arrayOf(VAR_DELIMITER)
        private val VAR_NAME_KEYS = arrayOf(VAR_NAME)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = MargaritaLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            MargaritaTokenTypes.KEYWORD_IF, MargaritaTokenTypes.KEYWORD_FOR -> KEYWORD_KEYS
            MargaritaTokenTypes.COMMENT -> COMMENT_KEYS
            MargaritaTokenTypes.NUMBER -> NUMBER_KEYS
            MargaritaTokenTypes.COLON -> DELIMITER_KEYS
            MargaritaTokenTypes.MARKDOWN_BLOCK_START, MargaritaTokenTypes.MARKDOWN_BLOCK_END -> MARKDOWN_DELIMITER_KEYS
            MargaritaTokenTypes.MD_HEADER -> MD_HEADER_KEYS
            MargaritaTokenTypes.MD_BOLD -> MD_BOLD_KEYS
            MargaritaTokenTypes.MD_ITALIC -> MD_ITALIC_KEYS
            MargaritaTokenTypes.MD_CODE -> MD_CODE_KEYS
            MargaritaTokenTypes.MD_LINK -> MD_LINK_KEYS
            MargaritaTokenTypes.MD_LIST -> MD_LIST_KEYS
            MargaritaTokenTypes.MARKDOWN_CONTENT -> MD_CONTENT_KEYS
            MargaritaTokenTypes.INCLUDE_START, MargaritaTokenTypes.INCLUDE_END -> INCLUDE_DELIMITER_KEYS
            MargaritaTokenTypes.INCLUDE_FILENAME -> INCLUDE_FILENAME_KEYS
            MargaritaTokenTypes.METADATA_DELIMITER -> METADATA_DELIMITER_KEYS
            MargaritaTokenTypes.METADATA_KEY -> METADATA_KEY_KEYS
            MargaritaTokenTypes.METADATA_VALUE -> METADATA_VALUE_KEYS
            MargaritaTokenTypes.VARIABLE_START, MargaritaTokenTypes.VARIABLE_END -> VAR_DELIM_KEYS
            MargaritaTokenTypes.VARIABLE_NAME -> VAR_NAME_KEYS
            else -> EMPTY_KEYS
        }
    }
}
