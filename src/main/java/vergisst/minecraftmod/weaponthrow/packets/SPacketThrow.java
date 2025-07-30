package vergisst.minecraftmod.weaponthrow.packets;

import java.util.UUID;

public class SPacketThrow extends BasePacket {
    // just a data container
    public SPacketThrow(UUID uuid, int maxChargeTime, boolean isCharging) {
        super(PacketIdentifiers.SPACKET_THROW);
        buf.writeUuid(uuid);
        buf.writeVarInt(maxChargeTime);
        buf.writeBoolean(isCharging);
    }
}