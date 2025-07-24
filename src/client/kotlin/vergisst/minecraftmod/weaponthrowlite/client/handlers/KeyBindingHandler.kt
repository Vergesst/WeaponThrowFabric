package vergisst.minecraftmod.weaponthrowlite.client.handlers

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
object KeyBindingHandler {
    val KEYBINDING: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.weaponthrow",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KeyBinding.GAMEPLAY_CATEGORY
        )
    )

    fun registerKeyBindings() {}
}