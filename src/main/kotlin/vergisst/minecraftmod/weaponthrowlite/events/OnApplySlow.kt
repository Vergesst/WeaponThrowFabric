package vergisst.minecraftmod.weaponthrowlite.events

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.player.PlayerEntity

interface OnApplySlow {
    fun interact(player: PlayerEntity): Boolean

    companion object {
        val EVENT
            get()= EventFactory.createArrayBacked(
                OnApplySlow::class.java,
                { listeners: Array<OnApplySlow> ->
                    object: OnApplySlow {
                        override fun interact(player: PlayerEntity): Boolean {
                            var result = false
                            for (item in listeners) {
                                result = item.interact(player) || result
                            }

                            return result
                        }
                    }
                })
    }
}