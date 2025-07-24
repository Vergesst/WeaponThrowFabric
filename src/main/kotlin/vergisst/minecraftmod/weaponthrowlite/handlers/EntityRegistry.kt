package vergisst.minecraftmod.weaponthrowlite.handlers

import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

import vergisst.minecraftmod.weaponthrowlite.Weaponthrowlite
import vergisst.minecraftmod.weaponthrowlite.entity.WeaponThrowEntity

object EntityRegistry {
    val WEAPONTHROW: EntityType<WeaponThrowEntity> = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of(Weaponthrowlite.MODID, "weaponthrow"),
        FabricEntityTypeBuilder
            .create(
            SpawnGroup.MISC
            // the closure here is alternative of ::WeaponThrowEntity in java
        ) { world, type ->
            WeaponThrowEntity(world, type)
        }
            .trackRangeBlocks(4)
            .trackedUpdateRate(20)
            .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
            .build()
    )

    fun registerEntities() {}
}