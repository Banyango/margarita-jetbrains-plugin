package com.margarita.margaritajetbrainsplugin

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

object MargaritaTokenTypes {
    // Keywords
    val KEYWORD_IF: IElementType = IElementType("MARGARITA_IF", null)
    val KEYWORD_FOR: IElementType = IElementType("MARGARITA_FOR", null)

    // Delimiters
    val COLON: IElementType = IElementType("MARGARITA_COLON", null)
    val COMMENT: IElementType = IElementType("MARGARITA_COMMENT", null)

    // Markdown block delimiters
    val MARKDOWN_BLOCK_START: IElementType = IElementType("MARGARITA_MD_START", null)
    val MARKDOWN_BLOCK_END: IElementType = IElementType("MARGARITA_MD_END", null)
    val MARKDOWN_CONTENT: IElementType = IElementType("MARGARITA_MD_CONTENT", null)

    // Include file syntax [[ filename ]]
    val INCLUDE_START: IElementType = IElementType("MARGARITA_INCLUDE_START", null)
    val INCLUDE_END: IElementType = IElementType("MARGARITA_INCLUDE_END", null)
    val INCLUDE_FILENAME: IElementType = IElementType("MARGARITA_INCLUDE_FILENAME", null)

    // Metadata block --- ... ---
    val METADATA_DELIMITER: IElementType = IElementType("MARGARITA_METADATA_DELIM", null)
    val METADATA_KEY: IElementType = IElementType("MARGARITA_METADATA_KEY", null)
    val METADATA_VALUE: IElementType = IElementType("MARGARITA_METADATA_VALUE", null)

    // Variable interpolation ${name}
    val VARIABLE_START: IElementType = IElementType("MARGARITA_VAR_START", null)
    val VARIABLE_NAME: IElementType = IElementType("MARGARITA_VAR_NAME", null)
    val VARIABLE_END: IElementType = IElementType("MARGARITA_VAR_END", null)

    // Markdown elements (for highlighting inside << >> blocks)
    val MD_HEADER: IElementType = IElementType("MARGARITA_MD_HEADER", null)
    val MD_BOLD: IElementType = IElementType("MARGARITA_MD_BOLD", null)
    val MD_ITALIC: IElementType = IElementType("MARGARITA_MD_ITALIC", null)
    val MD_CODE: IElementType = IElementType("MARGARITA_MD_CODE", null)
    val MD_LINK: IElementType = IElementType("MARGARITA_MD_LINK", null)
    val MD_LIST: IElementType = IElementType("MARGARITA_MD_LIST", null)

    val IDENTIFIER: IElementType = IElementType("MARGARITA_IDENTIFIER", null)
    val NUMBER: IElementType = IElementType("MARGARITA_NUMBER", null)
    val BAD_CHARACTER = TokenType.BAD_CHARACTER
}
