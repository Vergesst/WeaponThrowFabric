package com.dainxt.weaponthrow.client.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.dainxt.weaponthrow.client.handler.EventsHandler;
import com.dainxt.weaponthrow.packets.BasePacket;
import com.dainxt.weaponthrow.packets.PacketIdentifiers;

import java.util.UUID;

@Deprecated
public class SPacketThrow extends BasePacket {

    public SPacketThrow(UUID uuid, int maxChargeTime, boolean isCharging) {
        super(PacketIdentifiers.SPACKET_THROW);
        buf.writeUuid(uuid);
        buf.writeVarInt(maxChargeTime);
        buf.writeBoolean(isCharging);
    }

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
