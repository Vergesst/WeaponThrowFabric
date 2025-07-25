package vergisst.minecraftmod.weaponthrowlite.entity

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.EnvironmentInterface
import net.minecraft.block.*
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EntityType
import net.minecraft.entity.FlyingItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import vergisst.minecraftmod.weaponthrowlite.handlers.ConfigRegistry
import vergisst.minecraftmod.weaponthrowlite.handlers.EnchantmentHandler
import vergisst.minecraftmod.weaponthrowlite.handlers.EntityRegistry
import kotlin.math.abs


@EnvironmentInterface(
    value =  EnvType.CLIENT,
    itf = FlyingItemEntity::class
)
class WeaponThrowEntity(type: EntityType<out WeaponThrowEntity>, worldIn: World):
    PersistentProjectileEntity(type,  worldIn), FlyingItemEntity {

    /**
     * @param worldIn: current level
     * @param thrower: item thrower
     * @param canDestroy: whether target block can be destroyed by current item
     * @param damage: how much damage will this throw cause
     * @param thrownStackIn: item stack...?
     */
    constructor(worldIn: World, thrower: LivingEntity, canDestroy: Boolean, damage: Float, thrownStackIn: ItemStack):
            this(EntityRegistry.WEAPONTHROW, worldIn) {
        // another member property
        attackDamage = damage
        // dataTracker
        dataTracker.set(COMPOUND_STACK, thrownStackIn.copy().writeNbt(NbtCompound()))
        dataTracker.set(LOYALTY_LEVEL, getReturnOrLoyaltyEnchantment(thrownStackIn).toByte())
        dataTracker.set(DESTROYED_BLOCK, BlockPos.ORIGIN)
        dataTracker.set(SHOULD_DESTROY, canDestroy)
    }

    constructor(worldIn: World, x: Double, y: Double, z: Double):
            this(EntityRegistry.WEAPONTHROW, worldIn) {
        // set position (PersistentProjectileEntity needed)
        setPosition(x, y, z)
    }

    /**
     *  private properties & public properties
     */
    private var clientSideRotation = 0.0f
    private var counterClockwiseBounce = true
    private var dealtDamage = false
    private var attackDamage = 0.0f
    private var lastState: BlockState? = null

    // public
    var returningTicks = 0


    companion object {
        private val LOYALTY_LEVEL = DataTracker.registerData(WeaponThrowEntity::class.java, TrackedDataHandlerRegistry.BYTE)
        private val COMPOUND_STACK = DataTracker.registerData(WeaponThrowEntity::class.java, TrackedDataHandlerRegistry.NBT_COMPOUND)
        private val DESTROYED_BLOCK = DataTracker.registerData(WeaponThrowEntity::class.java, TrackedDataHandlerRegistry.BLOCK_POS)
        private val SHOULD_DESTROY = DataTracker.registerData(WeaponThrowEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)

        public fun getReturnOrLoyaltyEnchantment(stack: ItemStack): Int {
            val loyaltyLevel = EnchantmentHelper.getLevel(Enchantments.LOYALTY, stack)
            val returnLevel = EnchantmentHelper.getLevel(EnchantmentHandler.RETURN, stack)

            return if(loyaltyLevel > 0)
                loyaltyLevel
            else
                returnLevel
        }
    }

    override fun initDataTracker() {
        super.initDataTracker()
        this.dataTracker.startTracking(COMPOUND_STACK, NbtCompound())
        this.dataTracker.startTracking(LOYALTY_LEVEL, 0.toByte())
        this.dataTracker.startTracking(DESTROYED_BLOCK, BlockPos.ORIGIN)
        this.dataTracker.startTracking(SHOULD_DESTROY, false)
    }

    // getter and setter ??
    fun setItemStack(stack: ItemStack) {
        this.dataTracker.set(COMPOUND_STACK, stack.writeNbt(NbtCompound()))
    }

    fun getItemStack(): ItemStack {
        return ItemStack.fromNbt(this.dataTracker.get(COMPOUND_STACK) as NbtCompound)
    }

    fun shouldDestroy(stack: Boolean) {
        this.dataTracker.set(SHOULD_DESTROY, stack)
    }

    fun shouldDestroy(): Boolean {
        return this.dataTracker.get(SHOULD_DESTROY) as Boolean
    }

    fun setDestroyedBlock(pos: BlockPos)  {
        this.dataTracker.set(DESTROYED_BLOCK, pos)
    }

    fun getDestroyedBlock(): BlockPos {
        return this.dataTracker.get(DESTROYED_BLOCK)
    }

    // ??
    override fun tick() {
        if (inGroundTime > 4 && !dealtDamage) {
            dealtDamage = true
        }

        // destroy block in server ...?
        if (this.getDestroyedBlock() != BlockPos.ZERO && !this.world.isClient) {
            this.doInteractions {
                val event = this.world.getBlockState(this.getDestroyedBlock()).soundGroup.breakSound
                val destroyed = (this.owner as ServerPlayerEntity).interactionManager.tryBreakBlock(this.getDestroyedBlock())
                if (destroyed) {
                    this.world.playSound(null, this.getDestroyedBlock(), event, SoundCategory.AMBIENT, 10F, 1.0f)
                }
            }

            this.setDestroyedBlock(BlockPos.ORIGIN)
        }

        var gravityWorld = if (ConfigRegistry.COMMON.get().enchantments.enableGravity) EnchantmentHelper.getLevel(EnchantmentHandler.GRAVITY, this.getItemStack()) else 0

        // whether there should be gravity
        if (gravityWorld > 0) {
            this.setNoGravity(true)

            if (world.isOutOfHeightLimit(this.blockPos)) {
                this.velocity = this.velocity.multiply(1.0, 0.0, 1.0)
            }

            if (abs(velocity.x) < 0.1 && abs(velocity.z) < 0.1) {
                this.setNoGravity(false)
            }
        }

        val entity = owner
        val i = if (ConfigRegistry.COMMON.get().enchantments.enableReturn) this.dataTracker.get(LOYALTY_LEVEL).toInt() else 0

        if (i > 0 && (dealtDamage || isNoClip) && entity != null) {
            if (!this.shouldReturnToThrower()) {
                if (!world.isClient && pickupType == PickupPermission.ALLOWED) {
                    this.dropStack(this.getItemStack(), 0.1F)
                }

                this.remove(RemovalReason.DISCARDED)
            } else {
                isNoClip = true

                val v3d = entity.eyePos.subtract(pos)
                this.setPos(x, y + v3d.y * 0.015 * i, z)

                if (world.isClient) {
                    lastRenderY = y
                }

                val d0 = 0.05 * i
                velocity = velocity.multiply(0.95).add(v3d.normalize().multiply(d0))

                if (returningTicks == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F)
                }
                returningTicks++
            }
        }

        super.tick()
    }

    fun shouldReturnToThrower(): Boolean {
        val entity = owner

        return if (entity != null && entity.isAlive) {
            entity !is ServerPlayerEntity || !entity.isSpectator
        } else {
            false
        }
    }

    override fun asItemStack(): ItemStack = this.getItemStack().copy()

    override fun getEntityCollision(currentPosition: Vec3d?, nextPosition: Vec3d?): EntityHitResult? = if (dealtDamage)
            null
        else
            super.getEntityCollision(currentPosition, nextPosition)

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        val entity = entityHitResult.entity
        var f = attackDamage

        if (entity is LivingEntity) {
            f += if (ConfigRegistry.COMMON.get().enchantments.enableThrow)
                EnchantmentHelper.getLevel(EnchantmentHandler.THROW, this.getItemStack())*1F
            else 0F

            f += EnchantmentHelper.getAttackDamage(this.getItemStack(), entity.group)
        }

        val entity1 = owner
        val damageSource = damageSources.thrown(this, entity1 ?: this)

        this.dealtDamage = true
        val soundEvent = SoundEvents.ITEM_TRIDENT_HIT

        if (entity.damage(damageSource, f)) {
            // enderman --- will never suffer from projectile_damage
            if (entity.type == EntityType.ENDERMAN) {
                return
            }

            if (entity is LivingEntity) {
                val contusionWorld =
                    if (ConfigRegistry.COMMON.get().enchantments.enableConccusion) EnchantmentHelper.getLevel(
                        EnchantmentHandler.CONCCUSION,
                        this.getItemStack()
                    ) else 0

                if (contusionWorld > 0) {
                    entity.addStatusEffect(
                        StatusEffectInstance(
                            StatusEffects.SLOWNESS,
                            20 * 2 * contusionWorld,
                            5
                        )
                    )
                    entity.addStatusEffect(
                        StatusEffectInstance(
                            StatusEffects.NAUSEA,
                            20 * 5 * contusionWorld,
                            3
                        )
                    )
                }

                val fireTime = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, this.getItemStack())
                val groundedWorld =
                    if (ConfigRegistry.COMMON.get().enchantments.enableGroundedEdge) EnchantmentHelper.getLevel(
                        EnchantmentHandler.GROUNDEDEDGE,
                        this.getItemStack()
                    ) else 0

                if (fireTime > 0 || groundedWorld > 0) {
                    val nearEntities = this.world.getNonSpectatingEntities<LivingEntity?>(
                        LivingEntity::class.java,
                        this.getBoundingBox().expand(1.0)
                    )

                    if (!nearEntities.isEmpty()) {
                        for (nearEntity in nearEntities) {
                            if (nearEntity.getRandom().nextInt(3) == 0) {
                                nearEntity.setOnFireFor(fireTime)
                            }
                            nearEntity.addStatusEffect(
                                StatusEffectInstance(
                                    StatusEffects.WEAKNESS,
                                    80,
                                    groundedWorld - 1
                                )
                            )
                        }
                    }
                }

                if (entity1 is LivingEntity) {
                    EnchantmentHelper.onUserDamaged(entity, entity1)
                    EnchantmentHelper.onTargetDamaged(entity1, entity)
                }

                this.onHit(entity)

                if (this.getItemStack().getItem() is BlockItem) {
                    val blockItem = Block.getBlockFromItem(this.getItemStack().getItem())
                    if (blockItem is SandBlock) {
                        if (entity.getRandom().nextInt(10) == 0) entity.addStatusEffect(
                            StatusEffectInstance(StatusEffects.BLINDNESS, 60, 3)
                        )
                    } else if (blockItem is TorchBlock) {
                        if (entity.getRandom().nextInt(5) == 0) entity.setOnFireFor(1)
                    } else if (blockItem is AnvilBlock) {
                        entity.addStatusEffect(StatusEffectInstance(StatusEffects.NAUSEA, 60, 3))
                        entity.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, 60, 5))
                    }
                } else {
                    val itemThrowed = this.getItemStack().getItem()
                    if (itemThrowed == Items.BLAZE_ROD || itemThrowed == Items.BLAZE_POWDER) {
                        entity.setOnFireFor(1)
                    }
                }
            }
        }
        this.velocity = this.velocity.multiply(-0.01, -0.1, -0.01)
        this.playSound(soundEvent, 1f, 1f)
    }

    override fun getHitSound(): SoundEvent = SoundEvents.BLOCK_METAL_HIT

    override fun onPlayerCollision(entityIn: PlayerEntity) {
        val entity = owner
        if (entity == null || entity.uuid == entityIn.uuid || this.inGroundTime >(ConfigRegistry.COMMON.get().times.ticksUntilWeaponLoseOwner)) {
            super.onPlayerCollision(entityIn)
        }
    }

    override fun readCustomDataFromNbt(compound: NbtCompound) {
        super.readCustomDataFromNbt(compound)

        this.dealtDamage = compound.getBoolean("DealtDamage")
        if (compound.contains("Stack", 10)) {
            this.setItemStack(ItemStack.fromNbt(compound.getCompound("Stack")))
        }

        this.dataTracker.set(LOYALTY_LEVEL, WeaponThrowEntity.getReturnOrLoyaltyEnchantment(this.getItemStack()).toByte())
    }

    override fun writeCustomDataToNbt(compound: NbtCompound) {
        super.writeCustomDataToNbt(compound)

        compound.putBoolean("DealtDamage", this.dealtDamage)
        compound.put("Stack", this.dataTracker.get(COMPOUND_STACK))
        if (this.lastState != null)
            compound.put("inBlockState", NbtHelper.fromBlockState(this.lastState))
    }

    override fun checkDespawn() {
        val i = this.dataTracker.get(LOYALTY_LEVEL)
        if (this.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED || i <= 0) {
            if (this.inGroundTime > ConfigRegistry.COMMON.get().times.despawnTime)
                this.remove(RemovalReason.DISCARDED)
        }
    }

    override fun onCollision(result: HitResult) {
        val `raytraceresult$type` = result.type
        if (`raytraceresult$type` == HitResult.Type.ENTITY) {
            val hittedEntity = (result as EntityHitResult).entity
            if (hittedEntity is LivingEntity && this.owner is PlayerEntity) {
                this.doInteractions(Runnable {
                    (this.owner as PlayerEntity).attack(hittedEntity)
                })
            }
            this.onEntityHit(result)
        } else if (`raytraceresult$type` == HitResult.Type.BLOCK) {
            val stickBlockPos = (result as BlockHitResult).blockPos
            val state = this.world.getBlockState(stickBlockPos)
            if (state.block != Blocks.BEDROCK && this.shouldDestroy()) {
                val canBreak: Boolean = ConfigRegistry.COMMON.get().interactions.canBreakBlocks

                val canHarvest = this.getItemStack().isSuitableFor(state) && canBreak
                if (canHarvest) {
                    if (!this.world.isClient && this.lastState == null) {
                        this.setDestroyedBlock(stickBlockPos!!)
                    }
                }
            }
            this.onBlockHit(result)
        }
    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        this.lastState = this.world.getBlockState(blockHitResult.blockPos)
        val Vec3d = blockHitResult.getPos().subtract(x, y, z)
        velocity = Vec3d
        val Vec3d1 = Vec3d.normalize().multiply(0.05)
        this.setPos(x - Vec3d1.x, y - Vec3d1.y, z - Vec3d1.z)

        var event = SoundEvents.ITEM_TRIDENT_HIT_GROUND
        if (this.getItemStack().item is BlockItem) {
            val block = Block.getBlockFromItem(this.getItemStack().item)
            event = block.defaultState.soundGroup.hitSound
        }

        this.playSound(event, 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f))
        this.inGround = true
        this.shake = 7

        this.isCritical = false
        this.pierceLevel = 0.toByte()
        this.sound = event
        this.isShotFromCrossbow = false

        this.applyBounce()
    }

    fun applyBounce() {
        if (this.inGround && this.getItemStack().item is BlockItem) {
            if (!(abs(this.velocity.x) < 0.05 && abs(this.velocity.z) < 0.05)) {
                val vec3 = this.velocity.multiply(0.9)
                val landingPos = steppingPos

                if (!this.world.getBlockState(landingPos.down()).isAir || !this.world
                        .getBlockState(landingPos.up()).isAir
                ) {
                    this.setVelocity(vec3.x, -vec3.y, vec3.z)
                } else if (!this.world.getBlockState(landingPos.west()).isAir || !this.world
                        .getBlockState(landingPos.east()).isAir
                ) {
                    this.setVelocity(-vec3.x, vec3.y, vec3.z)
                } else if (!this.world.getBlockState(landingPos.north()).isAir || !this.world
                        .getBlockState(landingPos.south()).isAir
                ) {
                    this.setVelocity(vec3.x, vec3.y, -vec3.z)
                }
                this.counterClockwiseBounce = !this.counterClockwiseBounce
                this.inGround = false
            }
        }
    }

    override fun createSpawnPacket(): Packet<ClientPlayPacketListener> = super.createSpawnPacket()

    fun doInteractions(action: Runnable) {
        val itemOwner = this.owner as PlayerEntity
        val originalStack = itemOwner.getStackInHand(Hand.MAIN_HAND)
        itemOwner.setStackInHand(Hand.MAIN_HAND, this.getItemStack())

        action.run()

        val newStack = itemOwner.getStackInHand(Hand.MAIN_HAND)
        if (!newStack.isEmpty) {
            itemOwner.setStackInHand(Hand.MAIN_HAND, originalStack)
        } else {
            this.world.playSound(null, this.blockPos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.AMBIENT, 0.8f, 1.0f)

            this.spawnItemParticles(this.getItemStack(), 5)

            this.remove(RemovalReason.DISCARDED)
        }
    }

    private fun spawnItemParticles(itemStack: ItemStack, count: Int) {
        for (i in 0 until count) {
            var vec3d = Vec3d((random.nextFloat().toDouble() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
            vec3d = vec3d.rotateX(-this.pitch * 0.017453292f)
            vec3d = vec3d.rotateY(-this.yaw * 0.017453292f)
            val d = (-random.nextFloat()).toDouble() * 0.6 - 0.3
            var vec3d2 = Vec3d((random.nextFloat().toDouble() - 0.5) * 0.3, d, 0.6)
            vec3d2 = vec3d2.rotateX(-this.pitch * 0.017453292f)
            vec3d2 = vec3d2.rotateY(-this.yaw * 0.017453292f)
            vec3d2 = vec3d2.add(this.x, this.eyeY, this.z)
            if (world is ServerWorld)
                (world as ServerWorld).spawnParticles(
                    ItemStackParticleEffect(ParticleTypes.ITEM, stack),
                    vec3d2.x,
                    vec3d2.y,
                    vec3d2.z,
                    1,
                    vec3d.x,
                    vec3d.y + 0.05,
                    vec3d.z,
                    0.0
                )
            else
                world.addParticle(
                ItemStackParticleEffect(ParticleTypes.ITEM, stack),
                vec3d2.x,
                vec3d2.y,
                vec3d2.z,
                vec3d.x,
                vec3d.y + 0.05,
                vec3d.z
            )
        }
    }

    @Environment(EnvType.CLIENT)
    fun getRotationAnimation(partialTicks: Float): Float {
        if (!inGround)
            clientSideRotation = (age + partialTicks) * 50 * if (counterClockwiseBounce) 1 else -1

        return clientSideRotation
    }

    override fun getStack(): ItemStack = getItemStack()
}