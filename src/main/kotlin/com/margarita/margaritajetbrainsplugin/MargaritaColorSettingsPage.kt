package com.margarita.margaritajetbrainsplugin

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class MargaritaColorSettingsPage : ColorSettingsPage {
    private val ATTRS = arrayOf(
        // Keywords and control flow
        AttributesDescriptor("Keyword (if, for, else)", MargaritaSyntaxHighlighter.KEYWORD),
        AttributesDescriptor("Comment", MargaritaSyntaxHighlighter.COMMENT),
        AttributesDescriptor("Number", MargaritaSyntaxHighlighter.NUMBER),
        AttributesDescriptor("Delimiter (colon)", MargaritaSyntaxHighlighter.DELIMITER),

        // Markdown block
        AttributesDescriptor("Markdown Block Delimiter (<< >>)", MargaritaSyntaxHighlighter.MARKDOWN_DELIMITER),
        AttributesDescriptor("Markdown Header", MargaritaSyntaxHighlighter.MD_HEADER),
        AttributesDescriptor("Markdown Bold", MargaritaSyntaxHighlighter.MD_BOLD),
        AttributesDescriptor("Markdown Italic", MargaritaSyntaxHighlighter.MD_ITALIC),
        AttributesDescriptor("Markdown Code", MargaritaSyntaxHighlighter.MD_CODE),
        AttributesDescriptor("Markdown Link", MargaritaSyntaxHighlighter.MD_LINK),
        AttributesDescriptor("Markdown List", MargaritaSyntaxHighlighter.MD_LIST),
        AttributesDescriptor("Markdown Content", MargaritaSyntaxHighlighter.MD_CONTENT),

        // Include syntax
        AttributesDescriptor("Include Delimiter ([[ ]])", MargaritaSyntaxHighlighter.INCLUDE_DELIMITER),
        AttributesDescriptor("Include Filename", MargaritaSyntaxHighlighter.INCLUDE_FILENAME),

        // Metadata
        AttributesDescriptor("Metadata Delimiter (---)", MargaritaSyntaxHighlighter.METADATA_DELIMITER),
        AttributesDescriptor("Metadata Key", MargaritaSyntaxHighlighter.METADATA_KEY),
        AttributesDescriptor("Metadata Value", MargaritaSyntaxHighlighter.METADATA_VALUE),

        // Variables
        AttributesDescriptor("Variable Delimiter (\${name})", MargaritaSyntaxHighlighter.VAR_DELIMITER),
        AttributesDescriptor("Variable Name (name)", MargaritaSyntaxHighlighter.VAR_NAME)
    )

    override fun getDisplayName(): String = "Margarita"

    override fun getIcon(): Icon? = MargaritaIcons.FILE

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTRS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDemoText(): String = """
// Margarita Language Demo - Markdown Superset

---
task: Build documentation
priority: high
---

// This is a comment

if condition:
    // Conditional block
else:
    // Else block

for item in list:
    // Loop block

[[ common/header.mg ]]

<<
# Welcome to Margarita

This is a **markdown block** with *formatting*.

- List item one
- List item two

Here's some `inline code` and a [link](https://example.com).

## Features

The markdown block supports standard formatting:
- **Bold text**
- *Italic text* 
- `Code snippets`

You can use variables like ${'$'}{name} inside text.

>>

[[ footer.mg ]]
""".trimIndent()

    override fun getHighlighter(): com.intellij.openapi.fileTypes.SyntaxHighlighter = MargaritaSyntaxHighlighter()

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null
}
