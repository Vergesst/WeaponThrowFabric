package com.dainxt.weaponthrow.packets;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class S2CThrowPacket implements FabricPacket {
    UUID uuid;
    int maxChargeTime;
    boolean isCharging;

    static Identifier ID = PacketIdentifiers.SERVER_PACKET_THROW;
    static PacketType<S2CThrowPacket> TYPE = PacketType.create(ID, S2CThrowPacket::new);

    public S2CThrowPacket(UUID uuid, int maxChargeTime, boolean isCharging) {
        this.uuid = uuid;
        this.maxChargeTime = maxChargeTime;
        this.isCharging = isCharging;
    }

    public S2CThrowPacket(PacketByteBuf buf) {
        this (buf.readUuid(), buf.readInt(), buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(uuid);
        buf.writeInt(maxChargeTime);
        buf.writeBoolean(isCharging);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
