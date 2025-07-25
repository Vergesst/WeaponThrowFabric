package vergisst.minecraftmod.weaponthrowlite.enchantment

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import vergisst.minecraftmod.weaponthrowlite.handlers.ConfigRegistry

class ConccusionEnchantment(rarity: Rarity, vararg slot: EquipmentSlot): Enchantment(rarity, EnchantmentTarget.WEARABLE, slot){

    // shitlike HARD_CODED functions
    override fun getMinPower(_a: Int) = 30
    override fun getMaxPower(_a: Int) = 60
    override fun getMaxLevel() = 2

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        val enchantAll = ConfigRegistry.COMMON.config.enchantments.enchantAllWeapons
        val isAxe = stack.item is AxeItem
        val canApply = super.isAcceptableItem(stack)

        return (isAxe || enchantAll || canApply) && ConfigRegistry.COMMON.config.enchantments.enableConccusion
    }
}