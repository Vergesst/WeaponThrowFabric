package com.dainxt.weaponthrow.packets;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class C2SThrowPacket implements FabricPacket {
    static Identifier ID = PacketIdentifiers.CLIENT_PACKET_THROW;
    static PacketType<C2SThrowPacket> TYPE = PacketType.create(ID, C2SThrowPacket::new);
    State state;

    public C2SThrowPacket(State state) {
        this.state = state;
    }

    public C2SThrowPacket(PacketByteBuf buf) {
         this(State.fromByte(buf.readByte()));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeByte(state.ordinal());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
