package com.dainxt.weaponthrow.client.handler;

import com.dainxt.weaponthrow.packets.PacketIdentifiers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;

public class PacketHandler {
    public static void sendToServer(FabricPacket packet) {
        ClientPlayNetworking.send(packet);
    }

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(
                PacketIdentifiers.SERVER_PACKET_THROW,
                (client, handler, buf, receiver) -> {
            client.execute(() -> {
                EventsHandler.onSeverUpdate(
                        buf.readUuid(), buf.readVarInt(), buf.readBoolean()
                );
            });
        });
    }
}
