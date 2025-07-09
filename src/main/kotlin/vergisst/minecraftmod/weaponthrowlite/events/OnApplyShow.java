package vergisst.minecraftmod.weaponthrowlite.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

@Deprecated
public interface OnApplyShow {
    boolean interact(PlayerEntity player);

    Event<OnApplyShow> EVENT = EventFactory.createArrayBacked(
            OnApplyShow.class,
            (listeners) ->
                    new OnApplyShow() {
                        @Override
                        public boolean interact(@NotNull PlayerEntity player) {
                            var result = false;

                            for (var item : listeners) {
                                result = item.interact(player) || result;
                            }

                            return result;
                        }
                    }

    );
}
