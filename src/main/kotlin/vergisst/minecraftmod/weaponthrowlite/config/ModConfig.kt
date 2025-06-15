package vergisst.minecraftmod.weaponthrowlite.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry

@Deprecated(message = "ONLY FOR TEST")
@Config(name = "weaponthrowlite")
class ModConfig: ConfigData {
    val toggleA = true
    val toggleB = false

    @ConfigEntry.Gui.CollapsibleObject
    var stuff = InnerStuff()

    @ConfigEntry.Gui.Excluded
    var invisibleStuff = InnerStuff()

    class InnerStuff {
        val a = 0
        val b = 1
    }
}