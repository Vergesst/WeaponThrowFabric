package vergisst.minecraftmod.weaponthrowlite.client.mixins

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import vergisst.minecraftmod.weaponthrowlite.events.OnFOVUpdate

@Mixin(AbstractClientPlayerEntity::class)
abstract class AbstractClientPlayerEntityMixin {

    // Error -- 25/07/24 -- getFovMultiplier -- getFovMultiplayer
    // Vergisst is so stupid that she write a wrong name for such a simple inject point
    @Inject(at = [At("RETURN")], method = ["getFovMultiplier"], cancellable = true)
    private fun getSpeed(info: CallbackInfoReturnable<Float>) {
//        val player = this as Any as PlayerEntity

        val amount = info.getReturnValue()!!
        val result = OnFOVUpdate.Companion.EVENT.invoker().interact(this as PlayerEntity, amount)

        info.returnValue = result
    }
}