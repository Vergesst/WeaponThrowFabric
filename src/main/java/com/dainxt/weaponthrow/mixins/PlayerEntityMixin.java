package com.dainxt.weaponthrow.mixins;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.dainxt.weaponthrow.Interface.IPlayerEntityMixin;
import com.dainxt.weaponthrow.capabilities.PlayerThrowData;
import com.dainxt.weaponthrow.events.OnStartPlayerTick;

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
