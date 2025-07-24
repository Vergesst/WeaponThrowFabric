package vergisst.minecraftmod.weaponthrowlite.client.packets

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import vergisst.minecraftmod.weaponthrowlite.client.handlers.EventsHandler
import vergisst.minecraftmod.weaponthrowlite.handlers.PacketsIdentifier
import vergisst.minecraftmod.weaponthrowlite.packets.BasePacket
import java.util.*

//class SPacketThrow: BasePacket(PacketsIdentifier.SPACKET_THROW) {
//
//}

class SPacketThrow(uuid: UUID?, maxChargeTime: Int, isCharging: Boolean) : BasePacket(PacketsIdentifier.SPACKET_THROW) {
    init {
        buf?.writeUuid(uuid)
        buf?.writeVarInt(maxChargeTime)
        buf?.writeBoolean(isCharging)
    }

    companion object {
        fun register() {
            ClientPlayNetworking.registerGlobalReceiver(
                PacketsIdentifier.SPACKET_THROW,
                ClientPlayNetworking.PlayChannelHandler { client: MinecraftClient?, handler: ClientPlayNetworkHandler?, buf: PacketByteBuf?, responseSender: PacketSender? ->
                    val uuid = buf!!.readUuid()
                    val maxChargeTime = buf.readVarInt()
                    val isCharging = buf.readBoolean()
                    client!!.execute(Runnable {
                        EventsHandler.onServerUpdate(uuid, maxChargeTime, isCharging)
                    })
                })
        }
    }
}
