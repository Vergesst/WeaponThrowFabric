package vergisst.minecraftmod.weaponthrow.client;

import net.fabricmc.api.ClientModInitializer;
import vergisst.minecraftmod.weaponthrow.client.handler.EventsHandler;
import vergisst.minecraftmod.weaponthrow.client.handler.KeyBindingHandler;
import vergisst.minecraftmod.weaponthrow.client.handler.PacketHandler;
import vergisst.minecraftmod.weaponthrow.client.handler.RenderRegistry;

public class WeaponThrowClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EventsHandler.registerClientEvents();

        KeyBindingHandler.registerKeyBindings();

        PacketHandler.register();

        RenderRegistry.registerRenderers();
    }
}
