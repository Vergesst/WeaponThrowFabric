package vergisst.minecraftmod.weaponthrowlite.handlers

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigHolder
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer

import vergisst.minecraftmod.weaponthrowlite.config.WeaponThrowConfig

object ConfigRegistry {
    lateinit var COMMON: ConfigHolder<WeaponThrowConfig>

    fun registerConfig() {
        COMMON = AutoConfig.register(
            WeaponThrowConfig::class.java,
//            ::GsonConfigSerializer
        ) { config, clazz ->
            GsonConfigSerializer(config, clazz)
        }
    }
}