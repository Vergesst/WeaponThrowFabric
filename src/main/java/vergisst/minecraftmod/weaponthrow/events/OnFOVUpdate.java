package vergisst.minecraftmod.weaponthrow.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface OnFOVUpdate {
    Event<OnFOVUpdate> EVENT = EventFactory.createArrayBacked(OnFOVUpdate.class,
            (listeners) -> (player, fov) -> {
                var currentFov = fov;
                for (var listener: listeners) {
                    currentFov = listener.interact(player, fov);
                }

                return currentFov;
            });
    float interact(PlayerEntity entity, float fov);
}
