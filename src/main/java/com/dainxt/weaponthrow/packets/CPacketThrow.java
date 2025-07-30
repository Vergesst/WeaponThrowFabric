package com.dainxt.weaponthrow.packets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import com.dainxt.weaponthrow.handler.EventsHandler;

public class CPacketThrow extends BasePacket {
    public CPacketThrow(State state) {
        super(PacketIdentifiers.CPACKET_THROW);
        buf.writeByte(state.toByte());
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.CPACKET_THROW, (server, player, handler, buf, responseSender) -> {
            State action = State.fromByte(buf.readByte());

            server.execute(() -> {
                EventsHandler.onThrowItem(player, action);
            });
        });
    }
}
