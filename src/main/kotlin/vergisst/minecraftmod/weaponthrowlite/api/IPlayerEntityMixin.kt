package vergisst.minecraftmod.weaponthrowlite.api

import vergisst.minecraftmod.weaponthrowlite.capabilities.PlayerThrowData

interface IPlayerEntityMixin {
    fun setThrowPower(value: PlayerThrowData)

    fun getThrowPower(): PlayerThrowData
}