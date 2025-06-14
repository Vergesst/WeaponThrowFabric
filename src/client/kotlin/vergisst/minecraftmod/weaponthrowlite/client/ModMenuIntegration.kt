package vergisst.minecraftmod.weaponthrowlite.client

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.autoconfig.AutoConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import vergisst.minecraftmod.weaponthrowlite.config.ModConfig

// since net.minecraft.client is A CLIENT_ONLY package, so all of those classes who use classes under
// this package should exist under xxx.client package
// so that net.minecraft.client can be loaded correctly
@Environment(EnvType.CLIENT)
class ModMenuIntegration: ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent: Screen ->
                AutoConfig.getConfigScreen(ModConfig::class.java, parent).get()
        }
    }
}