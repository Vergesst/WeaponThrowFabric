package vergisst.minecraftmod.weaponthrowlite.client.handlers

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import vergisst.minecraftmod.weaponthrowlite.client.renderer.WeaponThrowRenderer
import vergisst.minecraftmod.weaponthrowlite.handlers.EntityRegistry

object RendererRegistry {
    @JvmStatic
    fun registerRenderers() {
        EntityRendererRegistry.register(
            EntityRegistry.WEAPONTHROW,
            ::WeaponThrowRenderer
        )
    }
}