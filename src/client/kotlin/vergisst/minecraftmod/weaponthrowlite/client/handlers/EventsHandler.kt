package vergisst.minecraftmod.weaponthrowlite.client.handlers

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.PlayerEntityRenderer
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.item.HeldItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Arm
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper

import org.joml.Quaternionf

import vergisst.minecraftmod.weaponthrowlite.api.IPlayerEntityMixin
import vergisst.minecraftmod.weaponthrowlite.capabilities.PlayerThrowData
import vergisst.minecraftmod.weaponthrowlite.client.events.OnHeldItemRender
import vergisst.minecraftmod.weaponthrowlite.client.events.OnStartPlayerRender
import vergisst.minecraftmod.weaponthrowlite.client.packets.SPacketThrow
import vergisst.minecraftmod.weaponthrowlite.events.OnApplySlow
import vergisst.minecraftmod.weaponthrowlite.events.OnFOVUpdate
import vergisst.minecraftmod.weaponthrowlite.events.OnStartPlayerTick
import vergisst.minecraftmod.weaponthrowlite.handlers.ConfigRegistry
import vergisst.minecraftmod.weaponthrowlite.handlers.EventsHandler.wasPressed
import vergisst.minecraftmod.weaponthrowlite.handlers.PacketHandler
import vergisst.minecraftmod.weaponthrowlite.impl.MathConstants.POSITIVE_X
import vergisst.minecraftmod.weaponthrowlite.impl.MathConstants.POSITIVE_Z
import vergisst.minecraftmod.weaponthrowlite.packets.CPacketThrow
import vergisst.minecraftmod.weaponthrowlite.packets.PacketState

import java.util.*
import kotlin.math.sign

/**
 *  Client side implementation for vergisst.minecraftmod.weaponthrowlite.handler.EventHandler
 *
 *  For purpose of separation of client_side code and server_side code
 */
object EventsHandler {
    fun registerEvents() {
        OnStartPlayerTick.EVENT.register({ player: PlayerEntity ->
            if (!player.world.isClient()) {
                val cap = (player as IPlayerEntityMixin).getThrowPower()

                val attacked = player.getAttackCooldownProgress(0.0f) < 1.0f
                val cdConfig = ConfigRegistry.COMMON.get().general.notUseWhenCooldown

                val changedItem = !ItemStack.areEqual(cap.getChargingStack(), player.mainHandStack)

                if (attacked && cdConfig || changedItem) {
                    cap.resetCharging()
                }

                if (cap.chargeTime > 0) {
                    cap.chargeTime = cap.chargeTime - 1
                }

                if (cap.action == (PacketState.START) || cap.action
                         == (PacketState.FINISH)
                ) {
                    PacketHandler.sendToAll(
                        player,
                        SPacketThrow(
                            player.getUuid(),
                            PlayerThrowData.getMaximumCharge(player),
                            cap.action == (PacketState.START)
                        )
                    )

                    if (cap.action == (PacketState.FINISH)) {
                        cap.action = PacketState.NONE
                    }
                }
            } else {
                val cap = (player as IPlayerEntityMixin).getThrowPower()

                if (cap.chargeTime > 0) {
                    cap.chargeTime = cap.chargeTime - 1
                }
            }
        } as OnStartPlayerTick?)
    }

    fun onServerUpdate(playerUUID: UUID, maxChargeTime: Int, isCharging: Boolean) {
        val clientInstance = MinecraftClient.getInstance()

        val playerEntity = clientInstance.world?.getPlayerByUuid(playerUUID)

        if (playerEntity != null) {
            val cap = (playerEntity as IPlayerEntityMixin).getThrowPower()
            cap.MAX_CHARGE = maxChargeTime

            if (isCharging) {
                cap.chargeTime = maxChargeTime
            }

            cap.action = if (isCharging) PacketState.DURING else PacketState.NONE
        }
    }

    fun registerClientEvents() {
        OnHeldItemRender.EVENT.register({ renderer: HeldItemRenderer, player: AbstractClientPlayerEntity, tickDelta: Float, pitch: Float, hand: Hand, swingProgress: Float, item: ItemStack, equipProgress: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int ->
            val cap = (player as IPlayerEntityMixin).getThrowPower()
            if (cap.action == (PacketState.DURING)) {
                var preProgress = 1.0f

                if (sign(cap.MAX_CHARGE.toFloat()) != 0.0f && cap.chargeTime > 0) {
                    // ???? MathHelper.lerp(Float, Int, Int) ---> err??????
                    val lerp: Float = MathHelper.lerp(tickDelta, cap.chargeTime + 1, cap.chargeTime).toFloat()
                    preProgress = 1f - lerp / cap.MAX_CHARGE
                }

                val progress = MathHelper.clamp(preProgress, 0f, 1.0f)

                matrices.translate(0.0, 0.0, progress * 0.50)
                //				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(progress * 10.0F));
    //				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(progress * 40.0F));
                matrices.multiply(Quaternionf().fromAxisAngleDeg(POSITIVE_Z, progress * 10.0f))
                matrices.multiply(Quaternionf().fromAxisAngleDeg(POSITIVE_X, progress * 10.0f))
            }
        } as OnHeldItemRender?)

        OnStartPlayerRender.EVENT.register({ renderer: PlayerEntityRenderer, player: PlayerEntity ->
            val cap = (player as IPlayerEntityMixin).getThrowPower()
            if (cap.action == (PacketState.DURING)) {
                if (player is AbstractClientPlayerEntity) {
                    val hand = player.mainArm
                    if (hand == Arm.RIGHT) renderer.getModel().rightArmPose = BipedEntityModel.ArmPose.THROW_SPEAR
                    else renderer.getModel().leftArmPose = BipedEntityModel.ArmPose.THROW_SPEAR
                }
            }
        } as OnStartPlayerRender)

        OnApplySlow.EVENT.register({ player: PlayerEntity ->
            val cap = (player as IPlayerEntityMixin).getThrowPower()
            cap.action == (PacketState.DURING)
        } as OnApplySlow)

        OnFOVUpdate.EVENT.register({ player: PlayerEntity, amount: Float ->
            val cap = (player as IPlayerEntityMixin).getThrowPower()
            val maxChargeTime = cap.MAX_CHARGE

            val chargeTime: Int = cap.chargeTime

            val isCharging: Boolean = cap.action == (PacketState.DURING)
            var f: Float = amount

            if (isCharging) {
                var f1 = 1.0f

                if (sign(maxChargeTime.toFloat()) != 0.0f && chargeTime > 0) {
                    val lerp = MathHelper.lerp(MinecraftClient.getInstance().tickDelta, chargeTime + 1, chargeTime)
                        .toFloat()
                    f1 = MathHelper.clamp(1.0f - lerp / maxChargeTime, 0f, 1.0f)
                }
                f1 = if (f1 > 1.0f) {
                    1.0f
                } else {
                    f1 * f1
                }

                f *= 1.0f + f1 * 0.15f
            }
            f
        } as OnFOVUpdate)

        ClientTickEvents.END_WORLD_TICK.register(ClientTickEvents.EndWorldTick { client: ClientWorld? ->
            val pressed: Boolean = KeyBindingHandler.KEYBINDING.isPressed
            if (pressed) {
                PacketHandler.sendToServer(CPacketThrow(if (wasPressed) PacketState.DURING else PacketState.START))
                wasPressed = true
            } else if (wasPressed) {
                PacketHandler.sendToServer(CPacketThrow(PacketState.FINISH))
                wasPressed = false
            }
        })
    }
}