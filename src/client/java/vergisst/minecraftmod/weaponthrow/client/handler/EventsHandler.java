package vergisst.minecraftmod.weaponthrow.client.handler;

import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import vergisst.minecraftmod.weaponthrow.Interface.IPlayerEntityMixin;
import vergisst.minecraftmod.weaponthrow.capabilities.PlayerThrowData;
import vergisst.minecraftmod.weaponthrow.client.events.OnHeldItemRender;
import vergisst.minecraftmod.weaponthrow.client.events.OnStartPlayerRender;
import vergisst.minecraftmod.weaponthrow.packets.CPacketThrow;
import vergisst.minecraftmod.weaponthrow.events.OnApplySlow;
import vergisst.minecraftmod.weaponthrow.events.OnFOVUpdate;
import vergisst.minecraftmod.weaponthrow.packets.State;

import java.util.UUID;

import static vergisst.minecraftmod.weaponthrow.impl.MathConstant.POSITIVE_X;
import static vergisst.minecraftmod.weaponthrow.impl.MathConstant.POSITIVE_Z;

public class EventsHandler {

    public static boolean wasPressed = false;

    public static void onSeverUpdate(UUID playerUUID, int maxChargeTime, boolean isCharging) {
        assert MinecraftClient.getInstance().world != null;
        PlayerEntity playerentity = MinecraftClient.getInstance().world.getPlayerByUuid(playerUUID);
        if(playerentity != null) {

            PlayerThrowData cap = ((IPlayerEntityMixin)playerentity).weaponThrow$getThrowPower();
            cap.MAX_CHARGE = maxChargeTime;

            if(isCharging) {
                cap.setChargeTime(maxChargeTime);
            }

            cap.setAction(isCharging ? State.DURING : State.NONE);

        }
    }

    public static void registerClientEvents() {
        OnHeldItemRender.EVENT.register((renderer, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light)->
        {

            PlayerThrowData cap = ((IPlayerEntityMixin)player).weaponThrow$getThrowPower();

            if(cap.getAction().equals(State.DURING)) {

                float preProgress = 1.0F;

                if(Math.signum(cap.MAX_CHARGE) != 0.0F && cap.getChargeTime() > 0) {
                    float lerp = MathHelper.lerp(tickDelta, cap.getChargeTime()+1, cap.getChargeTime());
                    preProgress = 1.F- lerp /cap.MAX_CHARGE;
                }

                float progress = MathHelper.clamp(preProgress, 0.F, 1.0F);

                matrices.translate(0.0D, 0.0F, progress * 0.50F);
//				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(progress * 10.0F));
//				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(progress * 40.0F));
                matrices.multiply(new Quaternionf().fromAxisAngleDeg(POSITIVE_Z, progress * 10.0f));
                matrices.multiply(new Quaternionf().fromAxisAngleDeg(POSITIVE_X, progress * 10.0f));
            }
        });

        OnStartPlayerRender.EVENT.register((renderer, player)->{

            PlayerThrowData cap = ((IPlayerEntityMixin)player).weaponThrow$getThrowPower();
            if(cap.getAction().equals(State.DURING)) {
                if(player instanceof AbstractClientPlayerEntity) {
                    Arm hand = player.getMainArm();
                    if(hand == Arm.RIGHT)
                        renderer.getModel().rightArmPose = BipedEntityModel.ArmPose.THROW_SPEAR;
                    else
                        renderer.getModel().leftArmPose = BipedEntityModel.ArmPose.THROW_SPEAR;
                }
            }
        });

        OnApplySlow.EVENT.register((player)->{
            PlayerThrowData cap = ((IPlayerEntityMixin)player).weaponThrow$getThrowPower();
            return cap.getAction().equals(State.DURING);
        });

        OnFOVUpdate.EVENT.register((player, amount)->{

            PlayerThrowData cap = ((IPlayerEntityMixin)player).weaponThrow$getThrowPower();

            int maxChargeTime = cap.MAX_CHARGE;

            int chargeTime = cap.getChargeTime();

            boolean isCharging = cap.getAction().equals(State.DURING);
            float f = amount;

            if(isCharging) {

                float f1 = 1.0F;

                if(Math.signum(maxChargeTime) != 0.0F && chargeTime > 0) {
                    float lerp = MathHelper.lerp(MinecraftClient.getInstance().getTickDelta(), chargeTime+1, chargeTime);
                    f1 = MathHelper.clamp(1.0F- lerp / maxChargeTime, 0.F, 1.0F);
                }
                if (f1 > 1.0F) {
                    f1 = 1.0F;
                } else {
                    f1 = f1 * f1;
                }

                f *= 1.0F + f1 * 0.15F;
            }
            return f;
        });

        ClientTickEvents.END_WORLD_TICK.register(client -> {

            boolean pressed = KeyBindingHandler.KEYBINDING.isPressed();

            if (pressed) {
                PacketHandler.sendToServer(new CPacketThrow(EventsHandler.wasPressed ? State.DURING: State.START));
                EventsHandler.wasPressed = true;
            }else if(EventsHandler.wasPressed){
                PacketHandler.sendToServer(new CPacketThrow(State.FINISH));
                EventsHandler.wasPressed = false;
            }
        });
    }
}

