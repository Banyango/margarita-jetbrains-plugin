package com.margarita.margaritajetbrainsplugin

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType

class MargaritaLexerAdapter : LexerBase() {
    private var buffer: CharSequence = ""
    private var startOffset: Int = 0
    private var endOffset: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var tokenType: IElementType? = null
    private var inMarkdownBlock = false
    private var inMetadataBlock = false
    private var inVariable = false

    override fun start(buffer: CharSequence, start: Int, end: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = start
        this.endOffset = end
        this.tokenStart = start
        this.tokenEnd = start
        this.tokenType = null
        this.inMarkdownBlock = (initialState and 1) != 0
        this.inMetadataBlock = (initialState and 2) != 0
        this.inVariable = false
        advance()
    }

    override fun getState(): Int {
        var state = 0
        if (inMarkdownBlock) state = state or 1
        if (inMetadataBlock) state = state or 2
        // Note: variable state is transient inside a token sequence; we don't encode it into state
        return state
    }

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        if (tokenEnd >= endOffset) {
            tokenType = null
            return
        }

        var i = tokenEnd
        tokenStart = i
        // If current char(s) are whitespace, return them as a WHITE_SPACE token
        if (tokenStart < endOffset && buffer[tokenStart].isWhitespace()) {
            var j = tokenStart
            while (j < endOffset && buffer[j].isWhitespace()) j++
            tokenEnd = j
            tokenType = TokenType.WHITE_SPACE
            return
        }

        // Variable interpolation ${name}
        // Highest priority so it works inside most contexts unless in Markdown special handling
        if (!inMarkdownBlock && !inMetadataBlock) {
            if (!inVariable && i + 1 < endOffset && buffer[i] == '$' && buffer[i + 1] == '{') {
                tokenEnd = i + 2
                tokenType = MargaritaTokenTypes.VARIABLE_START
                inVariable = true
                return
            }
            if (inVariable) {
                // If we are at the end of variable, emit end token
                if (i < endOffset && buffer[i] == '}') {
                    tokenEnd = i + 1
                    tokenType = MargaritaTokenTypes.VARIABLE_END
                    inVariable = false
                    return
                }
                // Otherwise parse the variable name (identifier)
                if (i < endOffset && buffer[i].isLetter()) {
                    var j = i
                    while (j < endOffset && buffer[j].isLetterOrDigit()) j++
                    tokenEnd = j
                    tokenType = MargaritaTokenTypes.VARIABLE_NAME
                    return
                }
                // If variable contains unexpected chars, consume until } or whitespace
                var j = i
                while (j < endOffset && buffer[j] != '}' && !buffer[j].isWhitespace()) j++
                tokenEnd = j
                tokenType = MargaritaTokenTypes.VARIABLE_NAME
                return
            }
        }

        // Check for markdown block delimiters << >>
        if (!inMetadataBlock && i + 1 < endOffset && buffer[i] == '<' && buffer[i + 1] == '<') {
            tokenEnd = i + 2
            tokenType = MargaritaTokenTypes.MARKDOWN_BLOCK_START
            inMarkdownBlock = true
            return
        }

        if (inMarkdownBlock && i + 1 < endOffset && buffer[i] == '>' && buffer[i + 1] == '>') {
            tokenEnd = i + 2
            tokenType = MargaritaTokenTypes.MARKDOWN_BLOCK_END
            inMarkdownBlock = false
            return
        }

        // Check for metadata delimiters ---
        if (i + 2 < endOffset && buffer[i] == '-' && buffer[i + 1] == '-' && buffer[i + 2] == '-') {
            tokenEnd = i + 3
            tokenType = MargaritaTokenTypes.METADATA_DELIMITER
            inMetadataBlock = !inMetadataBlock
            return
        }

        // Handle metadata block content (key: value)
        if (inMetadataBlock) {
            // Look for key: value pattern
            if (buffer[i].isLetter()) {
                while (i < endOffset && buffer[i].isLetterOrDigit()) i++
                // Check if followed by colon
                var j = i
                while (j < endOffset && buffer[j].isWhitespace()) j++
                if (j < endOffset && buffer[j] == ':') {
                    tokenEnd = i
                    tokenType = MargaritaTokenTypes.METADATA_KEY
                    return
                }
            } else if (buffer[i] == ':') {
                tokenEnd = i + 1
                tokenType = MargaritaTokenTypes.COLON
                // After colon, consume the value
                return
            } else {
                // Everything else is metadata value
                while (i < endOffset && buffer[i] != '\n' && !lookingAt(i, "---")) i++
                tokenEnd = i
                tokenType = MargaritaTokenTypes.METADATA_VALUE
                return
            }
        }

        // Check for include syntax [[ filename ]]
        if (!inMarkdownBlock && !inMetadataBlock && i + 1 < endOffset && buffer[i] == '[' && buffer[i + 1] == '[') {
            tokenEnd = i + 2
            tokenType = MargaritaTokenTypes.INCLUDE_START
            return
        }

        if (!inMarkdownBlock && !inMetadataBlock && i + 1 < endOffset && buffer[i] == ']' && buffer[i + 1] == ']') {
            tokenEnd = i + 2
            tokenType = MargaritaTokenTypes.INCLUDE_END
            return
        }

        // After [[ parse filename
        if (!inMarkdownBlock && !inMetadataBlock && i > 1 &&
            buffer.subSequence(maxOf(0, i - 10), i).contains("[[")) {
            // Check if we're between [[ and ]]
            var backtrack = i - 1
            var foundStart = false
            while (backtrack >= 0 && backtrack >= i - 50) {
                if (backtrack + 1 < buffer.length && buffer[backtrack] == '[' && buffer[backtrack + 1] == '[') {
                    foundStart = true
                    break
                }
                if (backtrack + 1 < buffer.length && buffer[backtrack] == ']' && buffer[backtrack + 1] == ']') {
                    break
                }
                backtrack--
            }
            if (foundStart) {
                while (i < endOffset && !(buffer[i] == ']' && i + 1 < endOffset && buffer[i + 1] == ']') && !buffer[i].isWhitespace() && buffer[i] != '\n') i++
                if (i > tokenStart) {
                    tokenEnd = i
                    tokenType = MargaritaTokenTypes.INCLUDE_FILENAME
                    return
                }
            }
        }

        // Handle markdown content inside << >>
        if (inMarkdownBlock) {
            tokenType = parseMarkdownContent(i)
            return
        }

        // Check for // comment
        if (!inMarkdownBlock && buffer[i] == '/' && i + 1 < endOffset && buffer[i + 1] == '/') {
            i += 2
            while (i < endOffset && buffer[i] != '\n') i++
            tokenEnd = i
            tokenType = MargaritaTokenTypes.COMMENT
            return
        }

        // Check for keywords if, for
        if (!inMarkdownBlock && buffer[i].isLetter()) {
            val wordStart = i
            while (i < endOffset && buffer[i].isLetterOrDigit()) i++
            val word = buffer.subSequence(wordStart, i).toString()
            tokenEnd = i
            tokenType = when (word) {
                "if" -> MargaritaTokenTypes.KEYWORD_IF
                "for" -> MargaritaTokenTypes.KEYWORD_FOR
                else -> MargaritaTokenTypes.IDENTIFIER
            }
            return
        }

        // Check for colon (terminates if/for)
        if (!inMarkdownBlock && buffer[i] == ':') {
            tokenEnd = i + 1
            tokenType = MargaritaTokenTypes.COLON
            return
        }

        // Check for numbers
        if (!inMarkdownBlock && buffer[i].isDigit()) {
            i++
            while (i < endOffset && buffer[i].isDigit()) i++
            tokenEnd = i
            tokenType = MargaritaTokenTypes.NUMBER
            return
        }

        // Single char or bad character
        tokenEnd = i + 1
        tokenType = MargaritaTokenTypes.BAD_CHARACTER
    }

    private fun parseMarkdownContent(start: Int): IElementType {
        var i = start
        val ch = buffer[i]

        // Check for >> end marker
        if (i + 1 < endOffset && ch == '>' && buffer[i + 1] == '>') {
            tokenEnd = i
            return MargaritaTokenTypes.MARKDOWN_CONTENT
        }

        // Markdown header (# ## ###)
        if (ch == '#') {
            while (i < endOffset && buffer[i] == '#') i++
            while (i < endOffset && buffer[i] != '\n' && !lookingAt(i, ">>")) i++
            tokenEnd = i
            return MargaritaTokenTypes.MD_HEADER
        }

        // Markdown bold (**text**)
        if (i + 1 < endOffset && ch == '*' && buffer[i + 1] == '*') {
            i += 2
            while (i + 1 < endOffset && !(buffer[i] == '*' && buffer[i + 1] == '*') && !lookingAt(i, ">>")) i++
            if (i + 1 < endOffset && buffer[i] == '*' && buffer[i + 1] == '*') i += 2
            tokenEnd = i
            return MargaritaTokenTypes.MD_BOLD
        }

        // Markdown italic (*text* or _text_)
        // Allow italics to start immediately at the beginning of the markdown block (i == start)
        if ((ch == '*' || ch == '_') && (i == start || buffer[i - 1].isWhitespace())) {
            val delim = ch
            i++
            while (i < endOffset && buffer[i] != delim && !lookingAt(i, ">>")) i++
            if (i < endOffset && buffer[i] == delim) i++
            tokenEnd = i
            return MargaritaTokenTypes.MD_ITALIC
        }

        // Markdown inline code (`code`)
        if (ch == '`') {
            i++
            while (i < endOffset && buffer[i] != '`' && !lookingAt(i, ">>")) i++
            if (i < endOffset && buffer[i] == '`') i++
            tokenEnd = i
            return MargaritaTokenTypes.MD_CODE
        }

        // Markdown link [text](url)
        if (ch == '[') {
            while (i < endOffset && buffer[i] != ']' && !lookingAt(i, ">>")) i++
            if (i < endOffset && buffer[i] == ']' && i + 1 < endOffset && buffer[i + 1] == '(') {
                i += 2
                while (i < endOffset && buffer[i] != ')' && !lookingAt(i, ">>")) i++
                if (i < endOffset && buffer[i] == ')') i++
            }
            tokenEnd = i
            return MargaritaTokenTypes.MD_LINK
        }

        // Markdown list (- item or * item)
        if ((ch == '-' || ch == '*') && (i + 1 < endOffset && buffer[i + 1].isWhitespace())) {
            i++
            while (i < endOffset && buffer[i] != '\n' && !lookingAt(i, ">>")) i++
            tokenEnd = i
            return MargaritaTokenTypes.MD_LIST
        }

        // Default: regular markdown content until special char or >>
        while (i < endOffset && !isMarkdownSpecialChar(buffer[i]) && !lookingAt(i, ">>")) {
            if (buffer[i] == '\n') {
                i++
                break
            }
            i++
        }
        if (i == start) i++ // Ensure we advance at least one char
        tokenEnd = i
        return MargaritaTokenTypes.MARKDOWN_CONTENT
    }

    private fun isMarkdownSpecialChar(ch: Char): Boolean {
        return ch == '#' || ch == '*' || ch == '_' || ch == '`' || ch == '[' || ch == '-'
    }

    private fun lookingAt(pos: Int, str: String): Boolean {
        if (pos + str.length > endOffset) return false
        for (j in str.indices) {
            if (buffer[pos + j] != str[j]) return false
        }
        return true
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = endOffset
}
