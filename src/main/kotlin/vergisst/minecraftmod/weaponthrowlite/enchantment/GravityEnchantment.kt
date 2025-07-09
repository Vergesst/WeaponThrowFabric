package vergisst.minecraftmod.weaponthrowlite.enchantment

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import vergisst.minecraftmod.weaponthrowlite.handlers.ConfigRegistry

class GravityEnchantment(rarityIn: Enchantment.Rarity, vararg slots: EquipmentSlot):
    Enchantment(rarityIn, EnchantmentTarget.WEAPON, slots) {

    override fun getMinPower(enchantLevel: Int): Int {
        return 25
    }

    override fun getMaxPower(level: Int): Int {
        return 75
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    override fun isTreasure(): Boolean {
        return true
    }

    override fun isCursed(): Boolean {
        return true
    }

    override fun isAcceptableItem(stack: ItemStack?): Boolean {
        val enchantAll = ConfigRegistry.COMMON.config.enchantments.enchantAllWeapons
        val isAxe = stack?.item is AxeItem
        val canApply = isAcceptableItem(stack)

        return if (enchantAll || isAxe || canApply)
            ConfigRegistry.COMMON.config.enchantments.enableGravity
        else
            false
    }
}