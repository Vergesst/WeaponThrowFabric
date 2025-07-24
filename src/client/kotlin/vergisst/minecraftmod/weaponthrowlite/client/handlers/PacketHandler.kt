package vergisst.minecraftmod.weaponthrowlite.client.handlers

import vergisst.minecraftmod.weaponthrowlite.client.packets.EntitySpawnPacket
import vergisst.minecraftmod.weaponthrowlite.client.packets.SPacketThrow

object PacketHandler {
    fun registerClientListeners() {
        EntitySpawnPacket.register()
        SPacketThrow.Companion.register()
    }
}