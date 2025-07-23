package vergisst.minecraftmod.weaponthrowlite.packets

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

abstract class BasePacket(val identifier: Identifier?) {
    var buf: PacketByteBuf?
        protected set

    init {
        this.buf = PacketByteBuf(Unpooled.buffer())
    }
}
