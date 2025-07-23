package vergisst.minecraftmod.weaponthrowlite.mixins

import net.minecraft.entity.player.PlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import vergisst.minecraftmod.weaponthrowlite.api.IPlayerEntityMixin

import vergisst.minecraftmod.weaponthrowlite.capabilities.PlayerThrowData
import vergisst.minecraftmod.weaponthrowlite.events.OnStartPlayerTick


@Mixin(PlayerEntity::class)
abstract class PlayerEntityMixin: IPlayerEntityMixin {
    // do not use lateinit here
    var throwPower = PlayerThrowData(this as Any as PlayerEntity)

    override fun setThrowPower(value: PlayerThrowData) {
        throwPower = value
    }

    override fun getThrowPower(): PlayerThrowData {
        return throwPower
    }

    @Inject(at = [At("HEAD")], method = ["tick"])
    private fun init(info: CallbackInfo) {
        OnStartPlayerTick.EVENT.invoker().interact(this as Any as PlayerEntity)
    }
}