package vergisst.minecraftmod.weaponthrowlite.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject
import vergisst.minecraftmod.weaponthrowlite.Weaponthrowlite

@Config(name = Weaponthrowlite.MODID)

class WeaponThrowConfig: ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    var general = General()

    @ConfigEntry.Category("default")
    @ConfigEntry.Gui.TransitiveObject
    var default = Default()

    @ConfigEntry.Category("enchantments")
    @ConfigEntry.Gui.TransitiveObject
    var enchantments = Enchantments()

    @ConfigEntry.Category("experimental")
    @ConfigEntry.Gui.TransitiveObject
    var experimental = Experimental()

    @ConfigEntry.Category("interactions")
    @ConfigEntry.Gui.TransitiveObject
    var interactions = Interactions()

    @ConfigEntry.Category("multipliers")
    @ConfigEntry.Gui.TransitiveObject
    var multipliers = Multipliers()

    @ConfigEntry.Category("times")
    @ConfigEntry.Gui.TransitiveObject
    var times = Times()

    class General: ConfigData {
        var creativeSpamming = false
        var notUseWhenCooldown = false
    }

    class Default: ConfigData {
        var baseDamageDefault = 1.0
        var velocityDefault = 2.0
    }

    class Enchantments: ConfigData {
        var enchantAllWeapons = false
        var enableThrow = true
        var enableConccusion = true
        var enableGroundedEdge = true
        var enableGravity = true
        var enableReturn = true
    }

    class Experimental : ConfigData {
        var shouldThrowItemsToo = false
    }

    class Interactions : ConfigData {
        var canBreakBlocks = true
    }

    class Multipliers : ConfigData {
        @CollapsibleObject
        var tools: ToolMultipliers = ToolMultipliers()

        class ToolMultipliers : ConfigData {
            var pickaxeMultiplier = 0.8
            var axeMultiplier = 1.2
            var swordMultiplier = 1.0
            var hoeMultiplier = 1.3
            var shovelMultiplier = 0.9
        }

        @CollapsibleObject
        var damages: DamageMultipliers = DamageMultipliers()

        class DamageMultipliers : ConfigData {
            var baseDamageMultiplier = 0.25
            var stackDamageMultiplier = 0.0
            var modifiedDamageMultiplier = 0.50
        }

        @CollapsibleObject
        var velocities: VelocityMultipliers = VelocityMultipliers()

        class VelocityMultipliers : ConfigData {
            var baseVelocityMultiplier = 0.25
            var stackVelocityMultiplier = 0.005
            var modifiedVelocityMultiplier = 0.4
        }

        @CollapsibleObject
        var exhaustions: ExhaustionMultipliers = ExhaustionMultipliers()

        class ExhaustionMultipliers : ConfigData {
            var baseExhaustionMultiplier = 0.075
            var stackExhaustionMultiplier = 0.01
            var modifiedExhaustionMultiplier = 2.0
        }
    }

    class Times : ConfigData {
        @BoundedDiscrete(min = 0, max = Int.MAX_VALUE.toLong())
        var despawnTime = 60 * 20

        var castTimeMuliplier = 3.0

        @BoundedDiscrete(min = 0, max = Int.MAX_VALUE.toLong())
        var ticksUntilWeaponLoseOwner = 20 * 7
    }
}