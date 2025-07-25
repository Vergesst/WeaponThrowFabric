package vergisst.minecraftmod.weaponthrowlite.enchantment

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import vergisst.minecraftmod.weaponthrowlite.handlers.ConfigRegistry

class ThrowEnchantment(rarity: Rarity, vararg slots: EquipmentSlot): Enchantment(rarity, EnchantmentTarget.WEAPON, slots) {

    override fun getMinPower(_a: Int) = 10
    override fun getMaxPower(_a: Int) = 50

    override fun getMaxLevel() = 3

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        val enchantAll: Boolean = ConfigRegistry.COMMON.getConfig().enchantments.enchantAllWeapons
        val isAxe = stack.item is AxeItem
        val canApply = super.isAcceptableItem(stack)
        return (isAxe || canApply || enchantAll) && ConfigRegistry.COMMON.getConfig().enchantments.enableThrow
    }
}