package com.dainxt.weaponthrow.handler;

import com.dainxt.weaponthrow.packets.PacketIdentifiers;
import com.dainxt.weaponthrow.packets.State;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

public class PacketHandler {
    public static void registerServerListener() {
        ServerPlayNetworking.registerGlobalReceiver(
                PacketIdentifiers.CLIENT_PACKET_THROW,
                (
                        server,
                        player,
                        handler,
                        buf,
                        responseSender
                ) -> {
                    var action = State.fromByte(buf.readByte());
                    server.execute(() -> {
                        EventsHandler.onThrowItem(player, action);
                    });
                }
        );
    }

    public static void sendToAll(Entity entity, FabricPacket packet) {
        for (var player : PlayerLookup.tracking((ServerWorld) entity.getWorld(), entity.getBlockPos())) {
            ServerPlayNetworking.send(player, packet);
        }
    }
}
