package vergisst.minecraftmod.weaponthrowlite.client.mixins

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.entity.PlayerEntityRenderer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import vergisst.minecraftmod.weaponthrowlite.client.events.OnStartPlayerRender

@Mixin(PlayerEntityRenderer::class)
class PlayerEntityRendererMixin {
    @Inject(method = ["setModelPose"], at = [At("TAIL")])
    private fun setModelPose(player: AbstractClientPlayerEntity, info: CallbackInfo?) {
        OnStartPlayerRender.EVENT.invoker().interact((this as Any as PlayerEntityRenderer), player)
    }
}