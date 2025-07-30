package vergisst.minecraftmod.weaponthrow.client.mixins;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vergisst.minecraftmod.weaponthrow.client.events.OnStartPlayerRender;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "setModelPose", at = @At("TAIL"))
    private void setModelPose(AbstractClientPlayerEntity player, CallbackInfo info) {
        OnStartPlayerRender.EVENT.invoker().interact(
                (PlayerEntityRenderer) (Object) this,
                player
        );
    }
}
