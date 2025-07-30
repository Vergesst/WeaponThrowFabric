package vergisst.minecraftmod.weaponthrow.mixins;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vergisst.minecraftmod.weaponthrow.Interface.IPlayerEntityMixin;
import vergisst.minecraftmod.weaponthrow.capabilities.PlayerThrowData;
import vergisst.minecraftmod.weaponthrow.events.OnStartPlayerTick;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IPlayerEntityMixin {
    @Unique
    private PlayerThrowData throwPower = new PlayerThrowData((PlayerEntity)(Object)this);

    @Override
    public void weaponThrow$setThrowPower(PlayerThrowData value) {
        throwPower = value;
    }

    @Override
    public PlayerThrowData weaponThrow$getThrowPower() {
        return throwPower;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        OnStartPlayerTick.EVENT.invoker().interact((PlayerEntity)(Object)this);
    }
}
