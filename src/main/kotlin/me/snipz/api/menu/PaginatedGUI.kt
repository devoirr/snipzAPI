package me.snipz.api.menu

import me.snipz.api.menu.item.FillerButton
import me.snipz.api.menu.item.GUIButton
import me.snipz.api.utils.Pagination
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

data class FillersData(val items: List<FillerButton>, val slots: List<Int>)

abstract class PaginatedGUI(player: Player, size: Int, title: Component) : GUI(player, size, title) {

    protected var page = 0

    private var fillers = mutableListOf<FillerButton>()
    private var fillerSlots = mutableListOf<Int>()
    private var pagination: Pagination<FillerButton>? = null

    abstract fun prepareFillers(): FillersData

    override fun open() {
        this.applyFillers()
        this.redrawFillers()

        this.redraw()

        this.drawn = true

        player.openInventory(this.inventory)
    }

    private fun applyFillers() {
        fillers.clear()
        fillerSlots.clear()

        val data = this.prepareFillers()

        fillers.addAll(data.items)
        fillerSlots.addAll(data.slots)

        pagination = Pagination(data.items, fillerSlots.size)
    }

    private fun redrawFillers() {

        for (fillerSlot in fillerSlots) {
            removeButton(fillerSlot)
        }

        val pagination = this.pagination ?: return

        if (page >= pagination.count()) {
            page = pagination.count() - 1
        }

        var slotIndex = 0
        val buttons = pagination.getPage(page)
        if (buttons.isNotEmpty()) {
            for (button in buttons) {
                if (slotIndex < fillerSlots.size) {
                    addButton(GUIButton().apply {
                        this.itemStack = button.itemStack
                        this.action = button.action
                        this.slots = listOf(fillerSlots[slotIndex])
                    })
                    slotIndex++
                }
            }
        }

    }

    fun nextPage() {
        val pagination = this.pagination ?: return
        if (pagination.count() > page + 1) {
            page++
            redrawFillers()
            redraw()
        }
    }

    fun previousPage() {
        if (page <= 0) {
            return
        }

        page--
        redrawFillers()
        redraw()
    }

    fun addFiller(filler: FillerButton) {
        fillers.add(filler)
    }

    fun isLastPage() = (pagination?.count() ?: 0) <= page + 1
    fun isFirstPage() = page == 0

}