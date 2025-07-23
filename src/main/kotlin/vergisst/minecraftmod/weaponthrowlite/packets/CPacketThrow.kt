package vergisst.minecraftmod.weaponthrowlite.packets

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import vergisst.minecraftmod.weaponthrowlite.handlers.EventsHandler
import vergisst.minecraftmod.weaponthrowlite.handlers.PacketsIdentifier

class CPacketThrow(val state: PacketState): BasePacket(PacketsIdentifier.CPACKET_THROW) {
    init {
        buf?.writeByte(state.toByte().toInt())
    }

    companion object {
        fun register() {
            ServerPlayNetworking.registerGlobalReceiver(
                PacketsIdentifier.CPACKET_THROW,
                { server, player, handler, buf, responseSender ->
                    val action = PacketState.fromByte(buf.readByte().toInt())

                    server.execute {
                        EventsHandler.onThrowItem(player, action)
                    }
                }
            )
        }
    }
}