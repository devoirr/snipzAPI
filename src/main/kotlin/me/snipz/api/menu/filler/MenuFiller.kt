package me.snipz.api.menu.filler

import org.bukkit.entity.Player

data class MenuFiller(val items: (Player) -> List<FillerItem>)