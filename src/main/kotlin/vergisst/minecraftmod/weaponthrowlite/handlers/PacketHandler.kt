package vergisst.minecraftmod.weaponthrowlite.handlers

import net.minecraft.util.Identifier
import vergisst.minecraftmod.weaponthrowlite.Weaponthrowlite
import vergisst.minecraftmod.weaponthrowlite.packets.CPacketThrow

object PacketHandler {
    fun registerClientListeners() {
        EntitySpawnPacket.register()
        SPacketThrow.register()
    }

    fun registerServerListeners() {
        CPacketThrow.register()
    }
}