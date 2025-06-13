package me.snipz.api.utils

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers
import io.papermc.paper.datacomponent.item.ItemEnchantments
import io.papermc.paper.datacomponent.item.ItemLore
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

fun itemStackOf(section: ConfigurationSection, vararg placeholders: TagResolver): ItemStack {
    if (!section.getKeys(false).contains("material")) {
        throw IllegalStateException("Material not set!")
    }

    val materialName = section.getString("material")!!.lowercase()
    val material = Registry.MATERIAL.get(NamespacedKey.minecraft(materialName)) ?: Material.STONE

    val itemStack = ItemStack.of(material)

    itemStack.amount = section.getInt("amount", 1).coerceAtLeast(1)

    if (section.getKeys(false).contains("model-data")) {
        itemStack.setData(
            DataComponentTypes.CUSTOM_MODEL_DATA,
            CustomModelData.customModelData().addFloat(section.getInt("model-data").toFloat())
        )
    }

    section.getString("name")?.let { nameString ->
        val component = MiniMessage.miniMessage().deserialize(nameString, *placeholders)
            .fixItalic()
        itemStack.setData(DataComponentTypes.ITEM_NAME, component)
    }

    section.getStringList("lore").let { loreLines ->
        val lore = loreLines.map { line -> MiniMessage.miniMessage().deserialize(line, *placeholders) }
            .map { it.fixItalic() }
            .toList()

        itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(lore))
    }

    section.getString("enchantments")?.let { enchantmentsString ->
        val pairs = enchantmentsString.lowercase().split(", ")

        var enchantments = ItemEnchantments.itemEnchantments()
        pairs.forEach { pair ->
            val args = pair.split(" ")

            val enchantmentName: String = args[0].lowercase()
            val level: Int = if (args.size == 1) {
                1
            } else {
                args[1].toIntOrNull() ?: 1
            }

            val enc = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(NamespacedKey.minecraft(enchantmentName)) ?: return@forEach

            enchantments = enchantments.add(enc, level)
        }

        itemStack.setData(DataComponentTypes.ENCHANTMENTS, enchantments)
    }

    section.getString("attributes")?.let { atrString ->
        val pairs = atrString.lowercase().split(", ")

        var attributes = ItemAttributeModifiers.itemAttributes()
        pairs.forEach { pair ->
            val args = pair.split(" ")
            if (args.count() < 2)
                return@forEach

            val attributeName = args[0].lowercase()

            val attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(attributeName)) ?: return@forEach
            val boost = args[1].toDoubleOrNull() ?: return@forEach

            val slot = if (args.count() > 2) {
                EquipmentSlotGroup.getByName(args[2].lowercase())
            } else null

            if (slot != null) {
                attributes = attributes.addModifier(
                    attribute,
                    AttributeModifier(attribute.key, boost, AttributeModifier.Operation.ADD_NUMBER, slot)
                )
            } else {
                attributes = attributes.addModifier(
                    attribute,
                    AttributeModifier(attribute.key, boost, AttributeModifier.Operation.ADD_SCALAR)
                )
            }
        }

        itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributes)
    }

    // ФЛАГИ (устанавливать в конце)
    section.getString("flags")?.split(", ")?.map { it.uppercase() }
        ?.mapNotNull { ItemFlag.entries.firstOrNull { flag -> flag.name == it } }?.let { flags ->
            itemStack.editMeta { meta ->
                meta.addItemFlags(*flags.toTypedArray())
            }
        }

    val flags = section.getString("flags")?.split(", ")?.map { it.uppercase() }
    if (flags != null) {
        for (flagName in flags) {
            val flag = ItemFlag.entries.firstOrNull { it.name == flagName }
            if (flag == null) {
                println("[API] Флаг $flagName не распознан!")
                continue
            }

            itemStack.editMeta { meta ->
                meta.addItemFlags(flag)
            }
        }
    }


    return itemStack
}

private fun Component.fixItalic() = this.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)