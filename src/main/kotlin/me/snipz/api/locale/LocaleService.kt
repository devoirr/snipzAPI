package me.snipz.api.locale

import me.snipz.api.locale.objects.LocaleConfig
import me.snipz.api.locale.objects.LocaleEnum
import org.bukkit.plugin.Plugin
import java.io.File

object LocaleService {
    inline fun <reified E> buildLocaleConfig(plugin: Plugin): LocaleConfig<E> where E : Enum<E>, E : LocaleEnum {
        val file = File(plugin.dataFolder, "locale.yml")
        if (!file.exists()) {
            if (plugin.getResource("locale.yml") != null) {
                plugin.saveResource("locale.yml", true)
            } else {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
        }

        val config = LocaleConfig(file, E::class.java)
        return config
    }
}