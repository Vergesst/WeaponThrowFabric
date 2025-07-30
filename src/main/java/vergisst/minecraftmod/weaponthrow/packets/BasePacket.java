package vergisst.minecraftmod.weaponthrow.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class BasePacket {
    private Identifier identifier;

    protected PacketByteBuf buf;

    public BasePacket(Identifier id) {
        identifier = id;
        buf = new PacketByteBuf(Unpooled.buffer());
    }

    public PacketByteBuf getBuf() {
        return buf;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
}
