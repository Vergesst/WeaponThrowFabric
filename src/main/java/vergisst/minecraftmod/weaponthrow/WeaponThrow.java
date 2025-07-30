package vergisst.minecraftmod.weaponthrow;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vergisst.minecraftmod.weaponthrow.handler.*;

public class WeaponThrow implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "weaponthrow";

    @Override
    public void onInitialize() {
        ConfigRegistry.registerConfig();

        EntityRegistry.registerEntities();

        EventsHandler.registerEvents();

        PacketHandler.registerServerListener();

        EnchantmentHandler.registerEnchantments();
    }
}
