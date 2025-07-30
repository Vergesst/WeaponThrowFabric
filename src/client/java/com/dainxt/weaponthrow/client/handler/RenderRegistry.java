package com.dainxt.weaponthrow.client.handler;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.dainxt.weaponthrow.handler.EntityRegistry;
import com.dainxt.weaponthrow.client.render.WeaponThrowRenderer;

public class RenderRegistry {

    public static void registerRenderers() {
        EntityRendererRegistry.register(EntityRegistry.WEAPONTHROW, WeaponThrowRenderer::new);

    }
}
