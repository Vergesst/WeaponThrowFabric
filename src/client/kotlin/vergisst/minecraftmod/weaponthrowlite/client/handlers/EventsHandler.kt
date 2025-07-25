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
//        OnHeldItemRender.EVENT.register(Object: OnHeldItemRenderer{
//                renderer: HeldItemRenderer,
//                player: AbstractClientPlayerEntity,
//                tickDelta: Float,
//                pitch: Float, hand: Hand,
//                swingProgress: Float,
//                item: ItemStack,
//                equipProgress: Float,
//                matrices: MatrixStack,
//                vertexConsumers: VertexConsumerProvider,
//                light: Int ->
//
//            val cap = (player as IPlayerEntityMixin).getThrowPower()
//            if (cap.action == (PacketState.DURING)) {
//                var preProgress = 1.0f
//
//                if (sign(cap.MAX_CHARGE.toFloat()) != 0.0f && cap.chargeTime > 0) {
//                    // ???? MathHelper.lerp(Float, Int, Int) ---> err??????
//                    val lerp: Float = MathHelper.lerp(tickDelta, cap.chargeTime + 1, cap.chargeTime).toFloat()
//                    preProgress = 1f - lerp / cap.MAX_CHARGE
//                }
//
//                val progress = MathHelper.clamp(preProgress, 0f, 1.0f)
//
//                matrices.translate(0.0, 0.0, progress * 0.50)
//                matrices.multiply(Quaternionf().fromAxisAngleDeg(POSITIVE_Z, progress * 10.0f))
//                matrices.multiply(Quaternionf().fromAxisAngleDeg(POSITIVE_X, progress * 10.0f))
//            }
//        })

        OnHeldItemRender.EVENT.register(object : OnHeldItemRender {
            override fun interact(
                renderer: HeldItemRenderer?,
                player: AbstractClientPlayerEntity?,
                tickDelta: Float,
                pitch: Float,
                hand: Hand?,
                swingProgress: Float,
                item: ItemStack?,
                equipProgress: Float,
                matrices: MatrixStack?,
                vertexConsumers: VertexConsumerProvider?,
                light: Int
            ) {
                // 在这里放置你原来的 Lambda 表达式中的逻辑
                // 确保 player 不为 null 且可以安全转换为 IPlayerEntityMixin
                if (player == null) return // 或者根据需要处理 null 情况

                val cap = (player as IPlayerEntityMixin).getThrowPower()
                if (cap.action == PacketState.DURING) {
                    var preProgress = 1.0f

                    // 确保 MathHelper.lerp 的参数类型正确，如之前讨论的
                    if (cap.MAX_CHARGE.toFloat() != 0.0f && cap.chargeTime > 0) {
                        val lerp: Float = MathHelper.lerp(tickDelta, (cap.chargeTime + 1).toFloat(), cap.chargeTime.toFloat())
                        preProgress = 1f - lerp / cap.MAX_CHARGE
                    }

                    val progress = MathHelper.clamp(preProgress, 0f, 1.0f)

                    // 确保 matrices 不为 null
                    matrices?.apply { // 使用安全调用和作用域函数
                        translate(0.0, 0.0, progress * 0.50)
                        multiply(Quaternionf().fromAxisAngleDeg(POSITIVE_Z, progress * 10.0f))
                        multiply(Quaternionf().fromAxisAngleDeg(POSITIVE_X, progress * 10.0f))
                    }
                }
            }
        })

        OnStartPlayerRender.EVENT.register(object: OnStartPlayerRender {
            override fun interact(render: PlayerEntityRenderer, entity: PlayerEntity) {
                val cap = (entity as IPlayerEntityMixin).getThrowPower()
                if (cap.action == PacketState.DURING) {
                    if (entity is AbstractClientPlayerEntity) { // player 已经是 PlayerEntity，这里检查 AbstractClientPlayerEntity
                        val hand = entity.mainArm
                        if (hand == Arm.RIGHT) render.model.rightArmPose = BipedEntityModel.ArmPose.THROW_SPEAR
                        else render.model.leftArmPose = BipedEntityModel.ArmPose.THROW_SPEAR
                    }
                }
            }
        })

        OnApplySlow.EVENT.register(object: OnApplySlow {
            override fun interact(player: PlayerEntity): Boolean {
                val cap = (player as IPlayerEntityMixin).getThrowPower()

                return cap.action == PacketState.DURING
            }
        })

        OnFOVUpdate.EVENT.register(object: OnFOVUpdate {
            override fun interact(player: PlayerEntity, fov: Float): Float {
                val cap = (player as IPlayerEntityMixin).getThrowPower()
                val maxChargeTime = cap.MAX_CHARGE.toDouble()
                val chargeTime = cap.chargeTime
                val isCharging = cap.action == PacketState.DURING
                var f = fov

                if (isCharging) {
                    var f1 = 1.0F
                    if (sign(maxChargeTime).toFloat() != 0.0F &&chargeTime > 0) {
                        val lerp = MathHelper.lerp(
                            MinecraftClient.getInstance().tickDelta,
                            chargeTime + 1,
                            chargeTime
                        )
                    }
                    f1 = f1 * f1

                    f *= 1.0F + f1 * 0.15F
                }

                return f
            }
        })

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