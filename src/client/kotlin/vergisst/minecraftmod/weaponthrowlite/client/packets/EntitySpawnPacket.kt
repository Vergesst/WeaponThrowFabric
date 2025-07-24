package vergisst.minecraftmod.weaponthrowlite.client.packets

import io.netty.buffer.Unpooled

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.entity.Entity
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.Packet
import net.minecraft.registry.Registries
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

import vergisst.minecraftmod.weaponthrowlite.handlers.PacketsIdentifier

/**
 * changes:
 * Registry.TYPE_IDENT -> Registries.TYPE_IDENT
 * net.minecraft.network.Packet -> net.minecraft.network.packet.Packet
 */
object EntitySpawnPacket {
    fun create(e: Entity): Packet<*> {
        check(!e.world.isClient) { "SpawnPacketUtil.create called on the logical client!" }
        val byteBuf = PacketByteBuf(Unpooled.buffer())
        byteBuf.writeVarInt(Registries.ENTITY_TYPE.getRawId(e.type))
        byteBuf.writeUuid(e.getUuid())
        byteBuf.writeVarInt(e.id)

        PacketBufUtil.writeVec3d(byteBuf, e.pos)
        PacketBufUtil.writeAngle(byteBuf, e.pitch)
        PacketBufUtil.writeAngle(byteBuf, e.yaw)
        return ServerPlayNetworking.createS2CPacket(PacketsIdentifier.SPAWN_PACKET, byteBuf)
    }


    fun register() {
        ClientPlayNetworking.registerGlobalReceiver(
            PacketsIdentifier.SPAWN_PACKET,
            ClientPlayNetworking.PlayChannelHandler { client: MinecraftClient?, handler: ClientPlayNetworkHandler?, buf: PacketByteBuf?, responseSender: PacketSender? ->
                val et = Registries.ENTITY_TYPE.get(
                    buf!!.readVarInt()
                )
                val uuid = buf.readUuid()
                val entityId = buf.readVarInt()
                val pos = PacketBufUtil.readVec3d(buf)
                val pitch = PacketBufUtil.readAngle(buf)
                val yaw = PacketBufUtil.readAngle(buf)
                client?.execute(Runnable {
                    checkNotNull(MinecraftClient.getInstance().world) { "Tried to spawn entity in a null world!" }
                    val e: Entity? = et.create(MinecraftClient.getInstance().world)
                    checkNotNull(e) { "Failed to create instance of entity \"" + Registries.ENTITY_TYPE.getId(et) + "\"!" }
                    e.updateTrackedPosition(pos.getX(), pos.getY(), pos.getZ())
                    e.setPos(pos.x, pos.y, pos.z)
                    e.pitch = pitch
                    e.yaw = yaw
                    e.id = entityId
                    e.setUuid(uuid)
                    MinecraftClient.getInstance().world?.addEntity(entityId, e)
                })
            })
    }

    object PacketBufUtil {
        /**
         * Packs a floating-point angle into a `byte`.
         *
         * @param angle
         * angle
         * @return packed angle
         */
        fun packAngle(angle: Float): Byte {
            return MathHelper.floor(angle * 256 / 360).toByte()
        }

        /**
         * Unpacks a floating-point angle from a `byte`.
         *
         * @param angleByte
         * packed angle
         * @return angle
         */
        fun unpackAngle(angleByte: Byte): Float {
            return (angleByte * 360) / 256f
        }

        /**
         * Writes an angle to a [PacketByteBuf].
         *
         * @param byteBuf
         * destination buffer
         * @param angle
         * angle
         */
        fun writeAngle(byteBuf: PacketByteBuf, angle: Float) {
            byteBuf.writeByte(packAngle(angle).toInt())
        }

        /**
         * Reads an angle from a [PacketByteBuf].
         *
         * @param byteBuf
         * source buffer
         * @return angle
         */
        fun readAngle(byteBuf: PacketByteBuf): Float {
            return unpackAngle(byteBuf.readByte())
        }

        /**
         * Writes a [net.minecraft.util.math.Vec3d] to a [PacketByteBuf].
         *
         * @param byteBuf
         * destination buffer
         * @param vec3d
         * vector
         */
        fun writeVec3d(byteBuf: PacketByteBuf, vec3d: Vec3d) {
            byteBuf.writeDouble(vec3d.x)
            byteBuf.writeDouble(vec3d.y)
            byteBuf.writeDouble(vec3d.z)
        }

        /**
         * Reads a [Vec3d] from a [PacketByteBuf].
         *
         * @param byteBuf
         * source buffer
         * @return vector
         */
        fun readVec3d(byteBuf: PacketByteBuf): Vec3d {
            val x = byteBuf.readDouble()
            val y = byteBuf.readDouble()
            val z = byteBuf.readDouble()
            return Vec3d(x, y, z)
        }
    }
}