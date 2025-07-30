package vergisst.minecraftmod.weaponthrow.packets;

import net.minecraft.util.Identifier;
import vergisst.minecraftmod.weaponthrow.WeaponThrow;

public class PacketIdentifiers {
    public static final Identifier SPAWN_PACKET = new Identifier(WeaponThrow.MOD_ID, "spawn_packet");

    public static final Identifier CPACKET_THROW = new Identifier(WeaponThrow.MOD_ID, "cpacket_throw");

    public static final Identifier SPACKET_THROW = new Identifier(WeaponThrow.MOD_ID, "spacket_throw");
}
