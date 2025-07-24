package vergisst.minecraftmod.weaponthrowlite.client.events

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.item.HeldItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand


interface OnHeldItemRender {
    fun interact(
        renderer: HeldItemRenderer?,
        player: AbstractClientPlayerEntity?,
        tickDelta: Float,
        pitch: Float,
        hand: Hand?,
        swingProgress: Float,
        item: ItemStack?,
        equipProgress: Float,
        matrices: MatrixStack?,
        vertexConsumers: VertexConsumerProvider?,
        light: Int
    )

    companion object {
        val EVENT
            get() = EventFactory.createArrayBacked(
                OnHeldItemRender::class.java
            ) { listeners ->
                object : OnHeldItemRender {
                    override fun interact(
                        renderer: HeldItemRenderer?,
                        player: AbstractClientPlayerEntity?,
                        tickDelta: Float,
                        pitch: Float,
                        hand: Hand?,
                        swingProgress: Float,
                        item: ItemStack?,
                        equipProgress: Float,
                        matrices: MatrixStack?,
                        vertexConsumers: VertexConsumerProvider?,
                        light: Int
                    ) {
                        for (listener in listeners) {
                            listener.interact(
                                renderer,
                                player,
                                tickDelta,
                                pitch,
                                hand,
                                swingProgress,
                                item,
                                equipProgress,
                                matrices,
                                vertexConsumers,
                                light
                            )
                        }
                    }
                }
            }
    }
}