package me.snipz.api.locale.objects

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

data class Message(private var lines: List<Component>) {

    constructor(lines: List<String>) : this(lines.map { MiniMessage.miniMessage().deserialize(it) })

    private val singleLine = lines.joinToString(" ") { MiniMessage.miniMessage().serialize(it) }

    private var isCloned = false

    fun send(sender: CommandSender) {
        lines.forEach { line -> sender.sendMessage(line) }
    }

    fun placeholder(key: String, replacement: String): Message {
        if (!this.isCloned) {
            val message = this.copy()
            message.isCloned = true

            return message.placeholder(key, replacement)
        } else {

            this.lines = lines.map { component ->
                component.replaceText {
                    TextReplacementConfig.builder().matchLiteral(key)
                        .replacement(replacement)
                        .build()
                }
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
            config.set(key, lines.first())
        } else {
            config.set(key, lines)
        }
    }

    fun getAsSingleLine() = singleLine

}