package me.snipz.api

import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

fun CommandSender.sendError(component: Component) {
    this.sendMessage(Component.text("ㅅ").appendSpace().append(component))
}

fun CommandSender.sendInfo(component: Component) {
    this.sendMessage(Component.text("ㄹ").appendSpace().append(component))
}
