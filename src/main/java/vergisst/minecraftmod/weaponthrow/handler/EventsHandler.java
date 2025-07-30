package vergisst.minecraftmod.weaponthrow.handler;

import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import vergisst.minecraftmod.weaponthrow.Interface.IPlayerEntityMixin;
import vergisst.minecraftmod.weaponthrow.capabilities.PlayerThrowData;
import vergisst.minecraftmod.weaponthrow.entity.WeaponThrowEntity;
import vergisst.minecraftmod.weaponthrow.events.OnStartPlayerTick;
import vergisst.minecraftmod.weaponthrow.packets.BasePacket;
import vergisst.minecraftmod.weaponthrow.packets.SPacketThrow;
import vergisst.minecraftmod.weaponthrow.packets.State;

public class EventsHandler {
    public static void registerEvents(){
        OnStartPlayerTick.EVENT.register((player)->{
            PlayerThrowData cap = ((IPlayerEntityMixin)player).weaponThrow$getThrowPower();
            if(!player.getWorld().isClient()) {

                boolean attacked = player.getAttackCooldownProgress(0.0F) < 1.0F;
                boolean cdConfig = ConfigRegistry.COMMON.get().general.notUseWhenCooldown;

                boolean changedItem = !ItemStack.areEqual(cap.getChargingStack(), player.getMainHandStack());

                if (attacked && cdConfig  || changedItem) {
                    cap.resetCharging();
                }

                if (cap.getChargeTime() > 0) {
                    cap.setChargeTime(cap.getChargeTime() - 1);
                }

                if(cap.getAction().equals(State.START) || cap.getAction().equals(State.FINISH)) {

                    PacketHandler.sendToAll(player, new SPacketThrow(player.getUuid(), PlayerThrowData.getMaximumCharge(player), cap.getAction().equals(State.START)));

                    if(cap.getAction().equals(State.FINISH)) {
                        cap.setAction(State.NONE);
                    }
                }
            }else {

                if(cap.getChargeTime() > 0) {
                    cap.setChargeTime(cap.getChargeTime()-1);
                }
            }
        });
        ServerTickEvents.START_SERVER_TICK.register((server)->{
            for (var player : server.getPlayerManager().getPlayerList()) {
                var cap = ((IPlayerEntityMixin) player).weaponThrow$getThrowPower();
                var attacked = player.getAttackCooldownProgress(0.0F) < 1.0F;
                var cdConfig = ConfigRegistry.COMMON.get().general.notUseWhenCooldown;
                var changedItem = !ItemStack.areEqual(cap.getChargingStack(), player.getMainHandStack());

                if ((attacked && cdConfig)  || changedItem) {
                    cap.resetCharging();
                }

                if (cap.getChargeTime() > 0) {
                    cap.setChargeTime(cap.getChargeTime() - 1);
                }
            }
        });
    }

    // original code
    public static void onThrowItem(ServerPlayerEntity serverplayer, State action){


        ServerWorld world = (ServerWorld) serverplayer.getWorld();
        ItemStack stack = serverplayer.getMainHandStack();

        boolean isThrowable = ConfigRegistry.COMMON.get().experimental.shouldThrowItemsToo;

        Multimap<EntityAttribute, EntityAttributeModifier> multimap = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
        boolean haveAttributes = multimap.containsKey(EntityAttributes.GENERIC_ATTACK_DAMAGE) || multimap.containsKey(EntityAttributes.GENERIC_ATTACK_SPEED);

        PlayerThrowData data = ((IPlayerEntityMixin) serverplayer).weaponThrow$getThrowPower();

        if ((isThrowable || haveAttributes) && !stack.isEmpty()) {

            boolean cdConfig = ConfigRegistry.COMMON.get().general.notUseWhenCooldown;

            if(!(serverplayer.getItemCooldownManager().getCooldownProgress(stack.getItem(), 1.0F) > 0 && cdConfig)) {

                data.setAction(action);

                if(action.equals(State.START) && data.getChargeTime() <= 0) {
                    data.startCharging(stack);
                }

                if(action.equals(State.FINISH) && data.getChargeTime() >= 0 ) {

                    float baseThrow = 0;
                    float baseExhaustion = 0.05F;
                    float modThrow = 1.0F;

                    if(Math.signum(PlayerThrowData.getMaximumCharge(serverplayer)) != 0.0F) {
                        modThrow = 1.F - (data.getChargeTime()/(float)PlayerThrowData.getMaximumCharge(serverplayer));
                    }

                    data.resetCharging();

                    double defaultVelocity = ConfigRegistry.COMMON.get().defaults.velocityDefault;

                    if (ConfigRegistry.COMMON.get().experimental.shouldThrowItemsToo){
                        baseThrow = (float) defaultVelocity;
                    }

                    if(haveAttributes) {
                        baseThrow = 20/ serverplayer.getAttackCooldownProgressPerTick();
                        baseExhaustion = serverplayer.getAttackCooldownProgressPerTick()/20;
                    }

                    if(baseThrow>0) {

                        boolean shouldDestroy = modThrow > 0.99;
                        double baseDamage = ConfigRegistry.COMMON.get().defaults.baseDamageDefault;
                        double toolMultiplier = 0.0D;

                        if(haveAttributes) {
                            baseDamage = (float) serverplayer.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

                            int types = 0;
                            if(stack.getItem() instanceof AxeItem) {
                                toolMultiplier += ConfigRegistry.COMMON.get().multipliers.tools.axeMultiplier;
                                types++;
                            }
                            if(stack.getItem() instanceof HoeItem) {
                                toolMultiplier += ConfigRegistry.COMMON.get().multipliers.tools.hoeMultiplier;
                                types++;
                            }
                            if(stack.getItem() instanceof PickaxeItem) {
                                toolMultiplier += ConfigRegistry.COMMON.get().multipliers.tools.pickaxeMultiplier;
                                types++;
                            }
                            if(stack.getItem() instanceof ShovelItem) {
                                toolMultiplier += ConfigRegistry.COMMON.get().multipliers.tools.shovelMultiplier;
                                types++;
                            }
                            if(stack.getItem() instanceof SwordItem) {
                                toolMultiplier = ConfigRegistry.COMMON.get().multipliers.tools.swordMultiplier;
                                types++;
                            }

                            toolMultiplier/=(types>0? types : 1);

                        }

                        if(toolMultiplier == 0.0F) {
                            toolMultiplier = 1.0F;
                        }

                        int size = serverplayer.isSneaking() ? stack.getCount() : 1;

                        double bDamageMul = ConfigRegistry.COMMON.get().multipliers.damages.baseDamageMultiplier;
                        double sDamageMul = ConfigRegistry.COMMON.get().multipliers.damages.stackDamageMultiplier;
                        double mDamageMul = ConfigRegistry.COMMON.get().multipliers.damages.modifiedDamageMultiplier;
                        double totalDamage = (baseDamage)*(1*bDamageMul + modThrow*mDamageMul) + (size*sDamageMul);
                        totalDamage*=toolMultiplier;

                        double bVelocityMul = ConfigRegistry.COMMON.get().multipliers.velocities.baseVelocityMultiplier;
                        double sVelocityMul = ConfigRegistry.COMMON.get().multipliers.velocities.stackVelocityMultiplier;
                        double mVelocityMul = ConfigRegistry.COMMON.get().multipliers.velocities.modifiedVelocityMultiplier;
                        double totalVelocity = (baseThrow)*(1*bVelocityMul + modThrow*mVelocityMul) - (size*sVelocityMul);
                        totalVelocity*=toolMultiplier;

                        double bExhaustionMul = ConfigRegistry.COMMON.get().multipliers.exhaustions.baseExhaustionMultiplier;
                        double sExhaustionMul = ConfigRegistry.COMMON.get().multipliers.exhaustions.stackExhaustionMultiplier;
                        double mExhaustionMul = ConfigRegistry.COMMON.get().multipliers.exhaustions.modifiedExhaustionMultiplier;
                        double totalExhaustion = (baseExhaustion)*(1*bExhaustionMul + modThrow*mExhaustionMul) + (size*sExhaustionMul);
                        totalExhaustion*=toolMultiplier;

                        WeaponThrowEntity throwedEntity = new WeaponThrowEntity(world, serverplayer, shouldDestroy, (float) totalDamage, stack.split(size));
                        throwedEntity.setVelocity(serverplayer, serverplayer.getPitch(), serverplayer.getYaw(), 0.0F, (float) totalVelocity, 1.0F);
                        serverplayer.addExhaustion((float) totalExhaustion);

                        world.spawnEntity(throwedEntity);

                        SoundEvent soundevent = SoundEvents.ENTITY_EGG_THROW;
                        throwedEntity.playSound(soundevent, 1.0F, 0.5F);
                    }
                }
                ((IPlayerEntityMixin) serverplayer).weaponThrow$setThrowPower(data);
            }
        }
    }
}
