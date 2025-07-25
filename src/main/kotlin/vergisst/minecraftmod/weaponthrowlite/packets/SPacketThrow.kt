package vergisst.minecraftmod.weaponthrowlite.packets

import net.minecraft.network.PacketByteBuf
import vergisst.minecraftmod.weaponthrowlite.handlers.PacketsIdentifier
import java.util.UUID

class SPacketThrow(val uuid: UUID, val maxChargeTime: Int, val isCharging: Boolean) : BasePacket(PacketsIdentifier.SPACKET_THROW) {

    fun write(buf: PacketByteBuf) {
        buf.writeUuid(uuid)
        buf.writeVarInt(maxChargeTime)
        buf.writeBoolean(isCharging)
    }

    companion object {
        fun read(buf: PacketByteBuf): SPacketThrow {
            val uuid = buf.readUuid()
            val maxChargeTime = buf.readVarInt()
            val isCharging = buf.readBoolean()

            return SPacketThrow(uuid,maxChargeTime, isCharging)
        }
    }
}
