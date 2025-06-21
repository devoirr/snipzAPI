package me.snipz.api.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

fun Long.formatDuration(): String {
    val days = this / (24 * 3600)
    val hours = (this % (24 * 3600)) / 3600
    val minutes = (this % 3600) / 60
    val secs = this % 60

    val parts = mutableListOf<String>()
    if (days > 0) parts.add("${days}д.")
    if (hours > 0) parts.add("${hours}ч.")
    if (minutes > 0) parts.add("${minutes}м.")
    if (secs > 0 || parts.isEmpty()) parts.add("${secs}с.") // всегда показываем секунды, если всё остальное 0

    return parts.joinToString(" ")
}

fun List<String>.toMultiLineComponent(
    preparation: (String) -> String = { it },
    vararg tagResolvers: TagResolver
): Component {
    var component: Component = Component.empty()

    this.forEachIndexed { index, string ->
        if (index != 0)
            component = component.appendNewline()

        component = component.append(
            MiniMessage.miniMessage().deserialize(preparation(string), *tagResolvers)
        )
    }

    return component
}