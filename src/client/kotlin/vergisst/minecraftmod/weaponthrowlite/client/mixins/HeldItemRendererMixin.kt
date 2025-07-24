package vergisst.minecraftmod.weaponthrowlite.client.mixins

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.item.HeldItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import vergisst.minecraftmod.weaponthrowlite.client.events.OnHeldItemRender

@Mixin(HeldItemRenderer::class)
abstract class HeldItemRendererMixin {
    @Inject(method = ["renderFirstPersonItem"], at = [At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingRiptide()Z")])
    fun renderCustom(
        player: AbstractClientPlayerEntity,
        tickDelta: Float,
        pitch: Float,
        hand: Hand,
        swingProgress: Float,
        item: ItemStack,
        equipProgress: Float,
        matrices: MatrixStack,
        vertexConsumer: VertexConsumerProvider,
        light: Int,
        info: CallbackInfo
    ) {
        OnHeldItemRender.EVENT.invoker().interact(
            this as Any as HeldItemRenderer,
            player,
            tickDelta,
            pitch,
            hand,
            swingProgress,
            item,
            equipProgress,
            matrices,
            vertexConsumer,
            light
        )
    }
}