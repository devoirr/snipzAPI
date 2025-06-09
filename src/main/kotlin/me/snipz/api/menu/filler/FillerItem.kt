package me.snipz.api.menu.filler

import org.bukkit.entity.Player

interface FillerItem {

    fun getPlaceholders(player: Player): Map<String, String>
    fun isLocked(player: Player): Boolean

}