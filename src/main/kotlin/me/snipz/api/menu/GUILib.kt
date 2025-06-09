package me.snipz.api.menu

import org.bukkit.plugin.Plugin

internal object GUILib {

    private var initialized = false

    fun init(plugin: Plugin) {
        if (initialized)
            return

        val listener = GUIListener(plugin)
        plugin.server.pluginManager.registerEvents(listener, plugin)

        initialized = true
    }

}