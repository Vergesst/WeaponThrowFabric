package vergisst.minecraftmod.weaponthrowlite.events

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.player.PlayerEntity

interface OnFOVUpdate {
    fun interact(player: PlayerEntity, fov: Float): Float

    val EVENT
        get() = EventFactory.createArrayBacked(
        OnFOVUpdate::class.java,
        {listeners ->
            object: OnFOVUpdate {
                override fun interact(player: PlayerEntity, fov: Float): Float {
                    for (item in listeners) {
                        val result = item.interact(player, fov)

                        if (result != 0f) {
                            return result
                        }
                    }
                    return 0f
                }
            }
        })

}