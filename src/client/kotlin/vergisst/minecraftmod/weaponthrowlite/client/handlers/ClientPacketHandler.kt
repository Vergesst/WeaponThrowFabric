package vergisst.minecraftmod.weaponthrowlite.client.handlers

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import vergisst.minecraftmod.weaponthrowlite.handlers.PacketsIdentifier
import vergisst.minecraftmod.weaponthrowlite.packets.SPacketThrow

object ClientPacketHandler {
    fun registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(
            PacketsIdentifier.SPACKET_THROW,
            ClientPlayNetworking.PlayChannelHandler { client: MinecraftClient, handler: ClientPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender ->
                val packet = SPacketThrow.read(buf)

                client.execute {
                    EventsHandler.onServerUpdate(packet.uuid, packet.maxChargeTime, packet.isCharging)
                }
            }
        )
    }
}