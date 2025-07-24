package vergisst.minecraftmod.weaponthrowlite.handlers

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import vergisst.minecraftmod.weaponthrowlite.api.IPlayerEntityMixin
import vergisst.minecraftmod.weaponthrowlite.capabilities.PlayerThrowData
import vergisst.minecraftmod.weaponthrowlite.entity.WeaponThrowEntity
import vergisst.minecraftmod.weaponthrowlite.packets.PacketState
import kotlin.math.sign

object EventsHandler {
    var wasPressed = false

    fun onThrowItem(serverPlayer: ServerPlayerEntity, action: PacketState) {

        // player's state
        val world: ServerWorld = serverPlayer.world as ServerWorld
        val stack = serverPlayer.getStackInHand(Hand.MAIN_HAND)

        val isThrowAble = ConfigRegistry.COMMON.get().experimental.shouldThrowItemsToo

        val multimap = stack.getAttributeModifiers(EquipmentSlot.MAINHAND)
        val haveAttributes = multimap.containsKey(EntityAttributes.GENERIC_ATTACK_DAMAGE) || multimap.containsKey(
            EntityAttributes.GENERIC_ATTACK_SPEED)

        val data = (serverPlayer as IPlayerEntityMixin).getThrowPower()

        if ((isThrowAble || haveAttributes) && !stack.isEmpty) {
            val cdConfig = ConfigRegistry.COMMON.get().general.notUseWhenCooldown
            val isInCollingDown = serverPlayer.itemCooldownManager.getCooldownProgress(stack.item, 1.0F) > 0

            if(!(isInCollingDown && cdConfig)) {
                data.action = action

                if(action == (PacketState.START) && data.chargeTime <= 0) {
                    data.startCharging(stack)
                }

                if (action == (PacketState.FINISH) && data.chargeTime >= 0) {
                    var baseThrow = 0F
                    var baseExhaustion = 0.05F
                    var modThrow = 1F
                    val maxChargeTime = PlayerThrowData.getMaximumCharge(serverPlayer).toFloat()

                    if (sign(maxChargeTime) != 0.0F) {
                        modThrow = 1F - (data.chargeTime/maxChargeTime)

                        // reset CHARGING state
                        data.resetCharging()

                        val defaultVelocity = ConfigRegistry.COMMON.get().default.velocityDefault

                        if (ConfigRegistry.COMMON.get().experimental.shouldThrowItemsToo) {
                            baseThrow = defaultVelocity.toFloat()
                        }

                        if (haveAttributes) {
                            baseThrow = 20 / serverPlayer.attackCooldownProgressPerTick
                            baseExhaustion = serverPlayer.attackCooldownProgressPerTick / 20
                        }

                        if (baseThrow > 0) {
                            val shouldDestroy = modThrow > 0.99
                            var baseDamage: Double = ConfigRegistry.COMMON.get().default.baseDamageDefault
                            var toolMultiplier = 0.0

                            if (haveAttributes) {
                                baseDamage =
                                    serverPlayer.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE).toFloat()
                                        .toDouble()

                                var types = 0
                                if (stack.item is AxeItem) {
                                    toolMultiplier += ConfigRegistry.COMMON.get().multipliers.tools.axeMultiplier
                                    types++
                                }
                                if (stack.item is HoeItem) {
                                    toolMultiplier += ConfigRegistry.COMMON.get().multipliers.tools.hoeMultiplier
                                    types++
                                }
                                if (stack.item is PickaxeItem) {
                                    toolMultiplier += ConfigRegistry.COMMON.get().multipliers.tools.pickaxeMultiplier
                                    types++
                                }
                                if (stack.item is ShovelItem) {
                                    toolMultiplier += ConfigRegistry.COMMON.get().multipliers.tools.shovelMultiplier
                                    types++
                                }
                                if (stack.item is SwordItem) {
                                    toolMultiplier = ConfigRegistry.COMMON.get().multipliers.tools.swordMultiplier
                                    types++
                                }

                                toolMultiplier /= (if (types > 0) types else 1).toDouble()
                            }

                            if (toolMultiplier == 0.0) {
                                toolMultiplier = 1.0
                            }

                            val size = if (serverPlayer.isSneaking) stack.count else 1

                            val bDamageMul = ConfigRegistry.COMMON.get().multipliers.damages.baseDamageMultiplier
                            val sDamageMul = ConfigRegistry.COMMON.get().multipliers.damages.stackDamageMultiplier
                            val mDamageMul = ConfigRegistry.COMMON.get().multipliers.damages.modifiedDamageMultiplier
                            var totalDamage =
                                (baseDamage) * (1 * bDamageMul + modThrow * mDamageMul) + (size * sDamageMul)
                            totalDamage *= toolMultiplier

                            val bVelocityMul = ConfigRegistry.COMMON.get().multipliers.velocities.baseVelocityMultiplier
                            val sVelocityMul =
                                ConfigRegistry.COMMON.get().multipliers.velocities.stackVelocityMultiplier
                            val mVelocityMul =
                                ConfigRegistry.COMMON.get().multipliers.velocities.modifiedVelocityMultiplier
                            var totalVelocity =
                                (baseThrow) * (1 * bVelocityMul + modThrow * mVelocityMul) - (size * sVelocityMul)
                            totalVelocity *= toolMultiplier

                            val bExhaustionMul =
                                ConfigRegistry.COMMON.get().multipliers.exhaustions.baseExhaustionMultiplier
                            val sExhaustionMul =
                                ConfigRegistry.COMMON.get().multipliers.exhaustions.stackExhaustionMultiplier
                            val mExhaustionMul =
                                ConfigRegistry.COMMON.get().multipliers.exhaustions.modifiedExhaustionMultiplier
                            var totalExhaustion: Double =
                                (baseExhaustion) * (1 * bExhaustionMul + modThrow * mExhaustionMul) + (size * sExhaustionMul)
                            totalExhaustion *= toolMultiplier

                            val thrownEntity = WeaponThrowEntity(
                                world,
                                serverPlayer,
                                shouldDestroy,
                                totalDamage.toFloat(),
                                stack.split(size)
                            )
                            thrownEntity.setVelocity(
                                serverPlayer,
                                serverPlayer.pitch,
                                serverPlayer.yaw,
                                0.0f,
                                totalVelocity.toFloat(),
                                1.0f
                            )
                            serverPlayer.addExhaustion(totalExhaustion.toFloat())

                            world.spawnEntity(thrownEntity)

                            val soundEvent = SoundEvents.ENTITY_EGG_THROW
                            thrownEntity.playSound(soundEvent, 1.0f, 0.5f)
                        }
                    }
                    // write the cast implicitly ---> in case of forget it
                    (serverPlayer as IPlayerEntityMixin).setThrowPower(data)
                }
            }
        }
    }

    fun registerEvents() {}
}