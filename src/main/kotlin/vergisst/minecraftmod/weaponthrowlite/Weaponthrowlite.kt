package vergisst.minecraftmod.weaponthrowlite

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import vergisst.minecraftmod.weaponthrowlite.config.WeaponThrowConfig


@Environment(EnvType.CLIENT)
class Weaponthrowlite : ModInitializer {
    companion object{
        const val MODID = "weaponthrow"
    }

    override fun onInitialize() {
        AutoConfig.register(WeaponThrowConfig::class.java, ::GsonConfigSerializer)
    }
}
