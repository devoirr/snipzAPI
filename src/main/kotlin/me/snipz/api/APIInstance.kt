package me.snipz.api

import me.snipz.api.menu.GUILib
import org.bukkit.plugin.java.JavaPlugin

object APIInstance {
    fun init(plugin: JavaPlugin) {
        GUILib.init(plugin)
    }
}