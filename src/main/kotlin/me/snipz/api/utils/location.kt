package me.snipz.api.utils

import org.bukkit.Bukkit
import org.bukkit.Location

fun Location.toString(block: Boolean = false): String {
    val stringBuilder = StringBuilder()
        .append(this.world!!.name).append(";")
        .append(if (block) this.blockX else this.x)
        .append(";")
        .append(if (block) this.blockY else this.y)
        .append(";")
        .append(if (block) this.blockZ else this.z)

    if (!block) {
        stringBuilder.append(";")
            .append(this.yaw)
            .append(";")
            .append(this.pitch)
    }

    return stringBuilder.toString()
}

fun String.toLocation(): Location {
    val args = this.split(";")
    val world = Bukkit.getWorld(args[0])

    val x = args[1].toDouble()
    val y = args[2].toDouble()
    val z = args[3].toDouble()

    val yaw = if (args.size > 4) args[4].toFloat() else 0f
    val pitch = if (args.size > 5) args[5].toFloat() else 0f

    return Location(world, x, y, z, yaw, pitch)
}