package vergisst.minecraftmod.weaponthrowlite.handlers

import net.minecraft.util.Identifier
import vergisst.minecraftmod.weaponthrowlite.Weaponthrowlite

object PacketsIdentifier {
    val SPAWN_PACKET = Identifier(Weaponthrowlite.MODID, "spawn_packet")

    val CPACKET_THROW = Identifier(Weaponthrowlite.MODID, "cpacket_throw")

    val SPACKET_THROW = Identifier(Weaponthrowlite.MODID, "spacket_throw")
}