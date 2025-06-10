package me.snipz.api.locale.objects

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class Message(private val lines: List<String>) {

    private val singleLine = lines.joinToString(" ")

    fun send(sender: CommandSender) {
        lines.map { MiniMessage.miniMessage().deserialize(it) }
            .forEach { line -> sender.sendMessage(line) }
    }

    fun send(sender: CommandSender, replacements: Map<String, String>) {
        lines.map {
            var text = it
            replacements.forEach { replacement ->
                text = text.replace(replacement.key, replacement.value)
            }
            return@map text
        }
            .map { MiniMessage.miniMessage().deserialize(it) }
            .forEach { line -> sender.sendMessage(line) }
    }

    fun send(sender: CommandSender, vararg replacements: Pair<String, String>) {
        send(sender, replacements.toMap())
    }

    fun sendActionBar(player: Player) {
        player.sendActionBar(MiniMessage.miniMessage().deserialize(lines.first()))
    }

    fun sendActionBar(player: Player, vararg replacements: Pair<String, String>) {
        var line = singleLine
        replacements.forEach { replacement ->
            line = line.replace(replacement.first, replacement.second)
        }

        player.sendActionBar {
            MiniMessage.miniMessage().deserialize(line)
        }
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