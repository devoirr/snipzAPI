package me.snipz.api.buyable.objects

import org.bukkit.entity.Player

interface BuyableItem {
    val id: String

    suspend fun purchase(player: Player)
    fun isAvailable(player: Player): Boolean
}