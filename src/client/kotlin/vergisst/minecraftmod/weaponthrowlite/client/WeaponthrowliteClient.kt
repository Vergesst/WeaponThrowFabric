package vergisst.minecraftmod.weaponthrowlite.client

import net.fabricmc.api.ClientModInitializer
import vergisst.minecraftmod.weaponthrowlite.client.handlers.KeyBindingHandler
import vergisst.minecraftmod.weaponthrowlite.client.handlers.PacketHandler
import vergisst.minecraftmod.weaponthrowlite.handlers.EventsHandler

class WeaponthrowliteClient : ClientModInitializer {

    override fun onInitializeClient() {
        EventsHandler.registerEvents()

        PacketHandler.registerClientListeners()

        KeyBindingHandler.registerKeyBindings()

//        RenderRegistry
    }
}
