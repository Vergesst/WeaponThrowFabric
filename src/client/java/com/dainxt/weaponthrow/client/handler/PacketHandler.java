package com.dainxt.weaponthrow.client.handler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.dainxt.weaponthrow.packets.BasePacket;
import com.dainxt.weaponthrow.packets.PacketIdentifiers;

import java.util.UUID;

public class PacketHandler {
    public static void sendToServer(BasePacket packet) {
        ClientPlayNetworking.send(packet.getIdentifier(), packet.getBuf());
    }

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.SPACKET_THROW, (client, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            int maxChargeTime = buf.readVarInt();
            boolean isCharging = buf.readBoolean();

            client.execute(() -> {
                EventsHandler.onSeverUpdate(uuid, maxChargeTime, isCharging);
            });
        });
    }
}
