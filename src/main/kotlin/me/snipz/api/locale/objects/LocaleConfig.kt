package me.snipz.api.locale.objects

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class LocaleConfig<E>(private val file: File, private val enumClass: Class<E>) where E : Enum<E>, E : LocaleEnum {

    private lateinit var handle: FileConfiguration
    private val messages = mutableMapOf<E, Message>()

    init {
        reload()
    }

    fun reload() {
        handle = YamlConfiguration.loadConfiguration(file)

        messages.clear()
        enumClass.enumConstants.forEach { constant ->
            val defaultMessage = constant.getMessage()

            if (handle.getKeys(false).contains(constant.name.lowercase())) {
                if (handle.isList(constant.name.lowercase())) {
                    messages[constant] = Message.of(handle.getStringList(constant.name.lowercase()))
                } else {
                    messages[constant] = Message.of(listOf(handle.getString(constant.name.lowercase())!!))
                }
            } else {
                defaultMessage.write(handle, constant.name.lowercase())
                messages[constant] = defaultMessage
            }
        }

        handle.save(file)
    }

    fun getMessage(constant: E): Message {
        return messages[constant] ?: Message.of(emptyList())
    }

}