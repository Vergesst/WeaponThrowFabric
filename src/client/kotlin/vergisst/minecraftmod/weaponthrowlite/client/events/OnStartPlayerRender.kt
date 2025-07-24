package vergisst.minecraftmod.weaponthrowlite.client.events

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.render.entity.PlayerEntityRenderer
import net.minecraft.entity.player.PlayerEntity

interface OnStartPlayerRender {
    fun interact(render: PlayerEntityRenderer, entity: PlayerEntity)

    companion object {
        val EVENT
            get() = EventFactory.createArrayBacked(
                OnStartPlayerRender::class.java
            ) { listeners ->
                object : OnStartPlayerRender {
                    override fun interact(render: PlayerEntityRenderer, entity: PlayerEntity) {
                        for (listener in listeners) {
                            listener.interact(render, entity)
                        }
                    }
                }
            }
    }
}