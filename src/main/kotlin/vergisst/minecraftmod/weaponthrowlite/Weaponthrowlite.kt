package vergisst.minecraftmod.weaponthrowlite

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer

import vergisst.minecraftmod.weaponthrowlite.config.ModConfig


@Environment(EnvType.CLIENT)
class Weaponthrowlite : ModInitializer {

    override fun onInitialize() {
        AutoConfig.register(ModConfig::class.java, ::GsonConfigSerializer)
    }
}
