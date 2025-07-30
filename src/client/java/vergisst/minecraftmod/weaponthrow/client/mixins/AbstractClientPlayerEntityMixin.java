package vergisst.minecraftmod.weaponthrow.client.mixins;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vergisst.minecraftmod.weaponthrow.events.OnFOVUpdate;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Inject(at = @At("RETURN"), method = "getFovMultiplier", cancellable = true)
    private void getSpeed(CallbackInfoReturnable<Float> info) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        float amount = info.getReturnValue();

        float result = OnFOVUpdate.EVENT.invoker().interact(player, amount);

        info.setReturnValue(result);
    }
}
