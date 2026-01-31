package com.margarita.margaritajetbrainsplugin

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object MargaritaIcons {
    val FILE: Icon? = try {
        IconLoader.getIcon("/icons/margarita.svg", MargaritaIcons::class.java)
    } catch (_: Exception) {
        null
    }
}
