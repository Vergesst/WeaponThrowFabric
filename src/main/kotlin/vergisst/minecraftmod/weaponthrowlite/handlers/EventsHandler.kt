package vergisst.minecraftmod.weaponthrowlite.handlers

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import vergisst.minecraftmod.weaponthrowlite.api.IPlayerEntityMixin

import vergisst.minecraftmod.weaponthrowlite.packets.PacketState

object EventsHandler {
    var wasPressed = false

    fun onThrowItem(serverPlayer: ServerPlayerEntity, action: PacketState) {

        // player's state
        val world: ServerWorld = serverPlayer.world as ServerWorld
        val stack = serverPlayer.getStackInHand(Hand.MAIN_HAND)

        val isThrowAble = ConfigRegistry.COMMON.get().experimental.shouldThrowItemsToo

        val multimap = stack.getAttributeModifiers(EquipmentSlot.MAINHAND)
        val haveAttributes = multimap.containsKey(EntityAttributes.GENERIC_ATTACK_DAMAGE) || multimap.containsKey(
            EntityAttributes.GENERIC_ATTACK_SPEED)

        val data = (serverPlayer as IPlayerEntityMixin).getThrowPower()
    }
}