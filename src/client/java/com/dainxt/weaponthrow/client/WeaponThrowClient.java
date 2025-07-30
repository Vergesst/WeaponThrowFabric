package com.dainxt.weaponthrow.client;

import net.fabricmc.api.ClientModInitializer;
import com.dainxt.weaponthrow.client.handler.EventsHandler;
import com.dainxt.weaponthrow.client.handler.KeyBindingHandler;
import com.dainxt.weaponthrow.client.handler.PacketHandler;
import com.dainxt.weaponthrow.client.handler.RenderRegistry;

public class WeaponThrowClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EventsHandler.registerClientEvents();

        KeyBindingHandler.registerKeyBindings();

        PacketHandler.register();

        RenderRegistry.registerRenderers();
    }
}
