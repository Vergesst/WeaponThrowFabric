package vergisst.minecraftmod.weaponthrowlite.handlers

import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import vergisst.minecraftmod.weaponthrowlite.Weaponthrowlite
import vergisst.minecraftmod.weaponthrowlite.enchantment.GravityEnchantment
import vergisst.minecraftmod.weaponthrowlite.enchantment.ReturnEnchantment

object EnchantmentHandler {
    val RETURN = Registry.register(
        Registries.ENCHANTMENT,
        Identifier.of(Weaponthrowlite.MODID, "return"),
        ReturnEnchantment(
        Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND
        )
    )

    val GRAVITY = Registry.register(
        Registries.ENCHANTMENT,
        Identifier.of(Weaponthrowlite.MODID, "gravity"),
        GravityEnchantment(
            Enchantment.Rarity.VERY_RARE,
            EquipmentSlot.MAINHAND
        )
    )

    fun registerEnchantments() {}
}