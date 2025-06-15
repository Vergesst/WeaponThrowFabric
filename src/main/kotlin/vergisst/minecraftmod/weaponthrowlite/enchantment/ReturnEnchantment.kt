package vergisst.minecraftmod.weaponthrowlite.enchantment

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import vergisst.minecraftmod.weaponthrowlite.handlers.ConfigRegistry

class ReturnEnchantment(weight: Rarity?, vararg slotTypes: EquipmentSlot) :
    Enchantment(weight, EnchantmentTarget.WEAPON ,slotTypes) {

    /**
     * get the power of return WEAPON
     * @param enchantLevel Int, the enchantLevel of RETURN
     * @param strengthLevel Int, the level of POWER
     *
     * and use PotionInstance.getAmplifier() to fetch the
     * @param strengthLevel
     */
    fun getMinPower(enchantLevel: Int, strengthLevel: Int): Int {
        return 5 + enchantLevel*7 + strengthLevel*3
    }

    fun getMaxPower(): Int {
        return 70
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        val enchantAll = ConfigRegistry.COMMON.config.enchantments.enchantAllWeapons
        val isAxe = stack.item is AxeItem
        val canApply = super.isAcceptableItem(stack)

        return if(isAxe || canApply || enchantAll)
            ConfigRegistry.COMMON.config.enchantments.enableReturn
        else
            false
    }
}