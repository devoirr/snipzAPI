package me.snipz.api.menu

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

@Suppress("unused")
class GUIListener(private val plugin: Plugin) : Listener {

    companion object {
        val allowedActions =
            mutableSetOf(
                InventoryAction.PICKUP_ALL,
                InventoryAction.PICKUP_ONE,
                InventoryAction.PICKUP_HALF,
                InventoryAction.PICKUP_SOME,
                InventoryAction.DROP_ONE_SLOT
//                InventoryAction.PLACE_ALL,
            )
    }

    @EventHandler(ignoreCancelled = true)
    fun onClick(event: InventoryClickEvent) {

        val player = event.whoClicked as? Player ?: return
        val topInventory = player.openInventory.topInventory
        val holder = topInventory.getHolder(false)

        if (holder !is GUI)
            return

        if (player.isSleeping) {
            player.closeInventory()
            event.isCancelled = true
            return
        }

        if (event.action == InventoryAction.HOTBAR_SWAP) {
            event.isCancelled = true

            val itemInOffHand = player.inventory.itemInOffHand
            player.inventory.setItemInOffHand(null)

            try {
                object : BukkitRunnable() {
                    override fun run() {
                        player.inventory.setItemInOffHand(itemInOffHand)
                        player.updateInventory()
                    }
                }.runTask(plugin)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (event.click == ClickType.DOUBLE_CLICK && event.hotbarButton != -1) {
            event.isCancelled = true
            return
        }

        val clickedInventory = event.clickedInventory ?: return

        if (event.isShiftClick && holder.inventory != clickedInventory) {
            event.isCancelled = true
        }

        if (clickedInventory != player.openInventory.topInventory) {
            return
        }

        event.isCancelled = true

        if (event.action !in allowedActions)
            return

        holder.click(event)
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.inventory.getHolder(false) !is GUI)
            return

        (event.inventory.getHolder(false) as GUI).close(event)
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {
        val holder = event.inventory.getHolder(false) ?: return

        if (holder !is GUI)
            return

        event.isCancelled = true
    }

}