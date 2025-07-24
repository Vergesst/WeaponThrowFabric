package vergisst.minecraftmod.weaponthrowlite.handlers

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import vergisst.minecraftmod.weaponthrowlite.packets.BasePacket
import vergisst.minecraftmod.weaponthrowlite.packets.CPacketThrow

object PacketHandler {

    fun registerServerListeners() {
        CPacketThrow.register()
    }

    fun sendToServer(packet: BasePacket) {
        ClientPlayNetworking.send(packet.identifier, packet.buf)
    }

    fun sendToAll(entity: Entity, packet: BasePacket) {
        for (player in PlayerLookup.tracking(entity.world as ServerWorld, entity.blockPos)) {
            ServerPlayNetworking.send(player, packet.identifier, packet.buf)
        }
    }
}