package vergisst.minecraftmod.weaponthrowlite

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import vergisst.minecraftmod.weaponthrowlite.handlers.*


@Environment(EnvType.CLIENT)
class Weaponthrowlite : ModInitializer {
    companion object{
        const val MODID = "weaponthrow"
    }

    override fun onInitialize() {
//        AutoConfig.register(WeaponThrowConfig::class.java, ::GsonConfigSerializer)

        ConfigRegistry.registerConfig()

        EntityRegistry.registerEntities()

        EventsHandler.registerEvents()

        EnchantmentHandler.registerEnchantments()

        PacketHandler.registerServerListeners()
    }
}
