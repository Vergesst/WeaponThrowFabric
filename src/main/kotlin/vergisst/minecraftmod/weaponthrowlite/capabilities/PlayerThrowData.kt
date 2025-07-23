package vergisst.minecraftmod.weaponthrowlite.capabilities

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import vergisst.minecraftmod.weaponthrowlite.packets.PacketState

class PlayerThrowData(val player: PlayerEntity) {
    // client
    val MAX_CHARGE = -1

    // both
    val action = PacketState.NONE

    // Server
    val chargeTime = -1

    val item = ItemStack.EMPTY
}