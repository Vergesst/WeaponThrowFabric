package vergisst.minecraftmod.weaponthrowlite.capabilities

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper
import vergisst.minecraftmod.weaponthrowlite.handlers.ConfigRegistry
import vergisst.minecraftmod.weaponthrowlite.packets.PacketState

class PlayerThrowData(val player: PlayerEntity) {
    // client
    var MAX_CHARGE = -1

    // both
    var action = PacketState.NONE

    // Server
    var chargeTime = -1

    var item: ItemStack = ItemStack.EMPTY
    
    fun startCharging(stack: ItemStack) {
        item = stack.copy()
        chargeTime = getMaximumCharge(player)
    }
    
    fun resetCharging() {
        action = if(action == PacketState.DURING) PacketState.FINISH else PacketState.NONE
        item = ItemStack.EMPTY
        chargeTime = -1
    }

    fun getChargingStack(): ItemStack { return item }
    
    companion object {
        fun getMaximumCharge(player: PlayerEntity): Int {
            return MathHelper.
                    floor(
                        player
                            .attackCooldownProgressPerTick * ConfigRegistry
                                .COMMON
                                .get()
                                .times
                                .castTimeMuliplier
                    )
        }
    }
}