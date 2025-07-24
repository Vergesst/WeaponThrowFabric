package vergisst.minecraftmod.weaponthrowlite.client.mixins

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import vergisst.minecraftmod.weaponthrowlite.events.OnApplySlow

@Mixin(ClientPlayerEntity::class)
abstract class ClientPlayerEntityMixin {
    @Inject(at = [At("RETURN")], method = ["shouldSlowDown"], cancellable = true)
    fun init(info: CallbackInfoReturnable<Boolean>) {
        info.returnValue =
            OnApplySlow.EVENT.invoker().interact(this as Any as PlayerEntity) || info.returnValue
    }
}