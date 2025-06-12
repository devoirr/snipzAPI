package me.snipz.api.locale.objects

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

data class Message(private var lines: List<Component>) {

    companion object {
        // Фабричный метод для создания экземпляра Message из списка строк.
        // Использует MiniMessage для десериализации каждой строки в Component.
        fun of(lines: List<String>): Message {
            return Message(lines.map { MiniMessage.miniMessage().deserialize(it) })
        }

        // Фабричный метод для создания экземпляра Message из одной строки.
        fun of(line: String): Message {
            return Message(listOf(MiniMessage.miniMessage().deserialize(line)))
        }
    }

    private val singleLine = lines.joinToString(" ") { MiniMessage.miniMessage().serialize(it) }

    private var isCloned = false

    fun send(sender: CommandSender) {
        lines.forEach { line -> sender.sendMessage(line) }
    }

    fun placeholder(key: String, replacement: String): Message {
        return placeholder(key, Component.text(replacement))
    }

    fun placeholder(key: String, replacement: Component): Message {
        if (!this.isCloned) {
            val message = this.copy()
            message.isCloned = true

            return message.placeholder(key, replacement)
        } else {

            this.lines = lines.map { component ->
                component.replaceText(
                    TextReplacementConfig.builder()
                        .matchLiteral(key)
                        .replacement(replacement)
                        .build()
                )
            }

            return this
        }
    }

    fun sendActionBar(player: Player) {
        player.sendActionBar(lines.first())
    }

    fun isSingleLine() = lines.size == 1

    fun write(config: FileConfiguration, key: String) {
        if (isSingleLine()) {
            config.set(key, MiniMessage.miniMessage().serialize(lines.first()))
        } else {
            config.set(key, lines.map { MiniMessage.miniMessage().serialize(it) })
        }
    }

    fun getAsSingleLine() = singleLine

}