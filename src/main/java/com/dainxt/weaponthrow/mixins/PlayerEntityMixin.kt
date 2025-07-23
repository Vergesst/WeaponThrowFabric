package com.dainxt.weaponthrow.mixins

import com.dainxt.weaponthrow.capabilities.PlayerThrowData
import com.dainxt.weaponthrow.events.OnStartPlayerTick
import com.dainxt.weaponthrow.interfaces.IPlayerEntityMixin

import net.minecraft.entity.player.PlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(PlayerEntity::class)
abstract class PlayerEntityMixin: IPlayerEntityMixin {
	private var throwPower: PlayerThrowData = PlayerThrowData(this as Any as PlayerEntity)

	override fun setThrowPower(value: PlayerThrowData) {
		throwPower = value
	}

	override fun getThrowPower(): PlayerThrowData {
		return throwPower
	}

	@Inject(at = [At("HEAD")], method = ["tick"])
	fun init(info: CallbackInfo) {
//		throwPower =
		OnStartPlayerTick.EVENT.invoker().interact(this as Any as PlayerEntity)

//		println("Interface Path is ${IPlayerEntityMixin::class.java.`package`.name}")
	}
}