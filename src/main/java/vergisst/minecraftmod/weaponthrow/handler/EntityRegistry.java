package vergisst.minecraftmod.weaponthrow.handler;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import vergisst.minecraftmod.weaponthrow.WeaponThrow;
import vergisst.minecraftmod.weaponthrow.entity.WeaponThrowEntity;

public class EntityRegistry {
    public static final EntityType<WeaponThrowEntity> WEAPONTHROW = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(WeaponThrow.MOD_ID, "weaponthrow"),
            FabricEntityTypeBuilder.<WeaponThrowEntity>create(SpawnGroup.MISC, WeaponThrowEntity::new).trackRangeBlocks(4).trackedUpdateRate(20).dimensions(EntityDimensions.fixed(0.5F, 0.5F)).build()
    );
    public static void registerEntities() {

    }
}