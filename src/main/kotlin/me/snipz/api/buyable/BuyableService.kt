package me.snipz.api.buyable

import me.snipz.api.buyable.objects.BuyableCategory
import me.snipz.api.buyable.objects.BuyableItem

object BuyableService {

    private val buyableItems =
        mutableMapOf<BuyableCategory, MutableList<BuyableItem>>()

    fun registerBuyableItem(category: BuyableCategory, item: BuyableItem) {
        buyableItems.computeIfAbsent(category) { mutableListOf() }.add(item)
    }

    fun getBuyableItems(category: BuyableCategory): List<BuyableItem> {
        return buyableItems[category] ?: emptyList()
    }

    fun clearCategory(category: BuyableCategory) {
        buyableItems[category]?.clear()
    }

}