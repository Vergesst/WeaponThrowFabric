package vergisst.minecraftmod.weaponthrow.handler;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import vergisst.minecraftmod.weaponthrow.WeaponThrow;
import vergisst.minecraftmod.weaponthrow.enchantments.*;

public class EnchantmentHandler {

    public static final Enchantment THROW = Registry.register(Registries.ENCHANTMENT, new Identifier(WeaponThrow.MOD_ID, "throw"), new ThrowEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment GROUNDEDEDGE = Registry.register(Registries.ENCHANTMENT, new Identifier(WeaponThrow.MOD_ID, "groundededge"), new GroundedEdgeEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final Enchantment CONCCUSION = Registry.register(Registries.ENCHANTMENT, new Identifier(WeaponThrow.MOD_ID, "conccusion"), new ConccusionEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment GRAVITY = Registry.register(Registries.ENCHANTMENT, new Identifier(WeaponThrow.MOD_ID, "gravity"), new GravityEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
    public static final Enchantment RETURN = Registry.register(Registries.ENCHANTMENT, new Identifier(WeaponThrow.MOD_ID, "return"), new ReturnEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));

    public static void registerEnchantments() {}
}