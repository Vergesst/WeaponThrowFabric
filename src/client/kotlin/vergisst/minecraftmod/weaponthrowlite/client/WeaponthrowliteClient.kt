package vergisst.minecraftmod.weaponthrowlite.client

import net.fabricmc.api.ClientModInitializer
import vergisst.minecraftmod.weaponthrowlite.client.handlers.EventsHandler
import vergisst.minecraftmod.weaponthrowlite.client.handlers.KeyBindingHandler
import vergisst.minecraftmod.weaponthrowlite.client.handlers.PacketHandler
import vergisst.minecraftmod.weaponthrowlite.client.handlers.RendererRegistry

class WeaponthrowliteClient : ClientModInitializer {

    override fun onInitializeClient() {
        PacketHandler.registerClientListeners()

        KeyBindingHandler.registerKeyBindings()

        RendererRegistry.registerRenderers()

        EventsHandler.registerClientEvents()

//        EventsHandler.registerEvents()
    }
}
