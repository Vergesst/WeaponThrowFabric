package vergisst.minecraftmod.weaponthrowlite.events

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.player.PlayerEntity

interface OnStartPlayerTick {
    fun interact(entity: PlayerEntity)

    companion object {
        val EVENT
            get() = EventFactory.createArrayBacked(
                OnStartPlayerTick::class.java,
                { listeners ->
                    object: OnStartPlayerTick {
                        override fun interact(entity: PlayerEntity) {
                            for (listener in listeners) {
                                listener.interact(entity)
                            }
                        }
                    }
                }
            )
    }
}