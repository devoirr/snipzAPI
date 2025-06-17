@file:Suppress("unused")

package me.snipz.api.commands

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

typealias PlayerExecutor = (Player, List<String>) -> Unit
typealias GenericExecutor = (CommandSender, List<String>) -> Unit
typealias TabCompleteProvider = (CommandSender, List<String>) -> List<String>

fun command(name: String, permission: String, init: CommandBuilder.() -> Unit): BukkitCommand {
    val builder = CommandBuilder(name)
    builder.permission = permission
    builder.init()

    return builder.build()
}

open class CommandException(message: String) : Exception(message)

class UsageException(usage: String) : CommandException("&fㅅ Использование: &c$usage")
class PlayerNotFoundException(name: String) : CommandException("&fㅅ Игрок &c$name &fне найден!")

class CommandBuilder(
    val name: String,
    var permission: String = "",
    val aliases: MutableList<String> = mutableListOf(),
    private var playerExecutor: PlayerExecutor? = null,
    private var genericExecutor: GenericExecutor? = null,
    private var tabCompleter: TabCompleteProvider? = null,
    private val subcommands: MutableMap<String, CommandBuilder> = mutableMapOf()
) {
    fun playerExecutor(block: PlayerExecutor) {
        playerExecutor = block
    }

    fun executor(block: GenericExecutor) {
        genericExecutor = block
    }

    fun tabCompleter(block: TabCompleteProvider) {
        tabCompleter = block
    }

    fun subcommand(name: String, block: CommandBuilder.() -> Unit) {
        val sub = CommandBuilder(name)
        sub.apply(block)
        subcommands[name] = sub
    }

    fun build(): BukkitCommand {
        val cmd = object : BukkitCommand(name) {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                if (args.isNotEmpty() && subcommands.containsKey(args[0])) {
                    val sub = subcommands[args[0]]!!
                    sub.execute(sender, args.drop(1))
                    return true
                }
                execute(sender, args.toList())
                return true
            }

            override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String> {
                if (args.isNotEmpty() && subcommands.containsKey(args[0])) {
                    val sub = subcommands[args[0]]!!
                    return sub.tabComplete(sender, alias, args.drop(1))
                }
                return tabCompleter?.invoke(sender, args.toList()) ?: emptyList()
            }
        }

        cmd.permission = permission
        cmd.aliases = aliases

        return cmd
    }

    private fun execute(sender: CommandSender, args: List<String>) {
        try {
            when {
                sender is Player && playerExecutor != null -> {
                    playerExecutor?.invoke(sender, args)
                }

                genericExecutor != null -> {
                    genericExecutor?.invoke(sender, args)
                }

                else -> {
                    sender.sendMessage("Эта команда доступна только игрокам.")
                }
            }
        } catch (e: CommandException) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(e.message!!))
        }
    }

    private fun tabComplete(sender: CommandSender, alias: String, args: List<String>): List<String> {
        if (args.isNotEmpty() && subcommands.containsKey(args[0])) {
            val sub = subcommands[args[0]]!!
            return sub.tabComplete(sender, alias, args.drop(1))
        }
        return tabCompleter?.invoke(sender, args) ?: emptyList()
    }
}