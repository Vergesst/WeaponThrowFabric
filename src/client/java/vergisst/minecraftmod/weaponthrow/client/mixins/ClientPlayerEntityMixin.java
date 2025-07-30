package vergisst.minecraftmod.weaponthrow.client.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vergisst.minecraftmod.weaponthrow.events.OnApplySlow;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(at = @At("RETURN"), method = "shouldSlowDown", cancellable = true)
    private void init(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(OnApplySlow.EVENT.invoker().interact((ClientPlayerEntity)(Object)this));
    }
}
