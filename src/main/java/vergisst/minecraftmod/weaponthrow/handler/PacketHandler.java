package vergisst.minecraftmod.weaponthrow.handler;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import vergisst.minecraftmod.weaponthrow.packets.BasePacket;
import vergisst.minecraftmod.weaponthrow.packets.CPacketThrow;


public class PacketHandler {
    public static void registerServerListener() {
        CPacketThrow.register();
    }

    public static void sendToAllTracking(Entity entity, BasePacket packet) {
        if (entity.getWorld() instanceof ServerWorld) {
            // back to player
            if (entity instanceof ServerPlayerEntity self) {
                ServerPlayNetworking.send(self, packet.getIdentifier(), packet.getBuf());
            }
            // all the player tracing entity
            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) entity.getWorld(), entity.getBlockPos())) {
                ServerPlayNetworking.send(player, packet.getIdentifier(), packet.getBuf());
            }
        }
    }

    public static void sendToAll(Entity entity, BasePacket packet) {
        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) entity.getWorld(), entity.getBlockPos())) {
            ServerPlayNetworking.send(player, packet.getIdentifier(), packet.getBuf());
        }
    }
}
