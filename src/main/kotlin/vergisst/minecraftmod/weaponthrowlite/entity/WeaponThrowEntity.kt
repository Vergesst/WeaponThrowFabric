package vergisst.minecraftmod.weaponthrowlite.entity

import net.fabricmc.api.EnvType
import net.fabricmc.api.EnvironmentInterface
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EntityType
import net.minecraft.entity.FlyingItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import vergisst.minecraftmod.weaponthrowlite.handlers.ConfigRegistry
import vergisst.minecraftmod.weaponthrowlite.handlers.EnchantmentHandler
import vergisst.minecraftmod.weaponthrowlite.handlers.EntityRegistry


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
        dataTracker.set(LOYALTY_LEVEL, getReturnOrLoyaltyEnchantment(thrownStackIn) as Byte)
        dataTracker.set(DESTROYED_BLOCK, BlockPos.ORIGIN)
        dataTracker.set(SHOULD_DESTROY, canDestroy)
    }

    constructor(worldIn: World, x: Double, y: Double, z: Double):
            this(EntityRegistry.WEAPONTHROW, worldIn) {
        // set position (PersistentProjectileEntity needed)
        setPosition(x, y, z)
    }

    private var clientSideRotation = 0.0f
    private var counterClockWiseBounce = true
    private var dealtDamage = false
    private var attackDamage = 0.0f

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

    fun doInteractions(action: Runnable) {
        val itemOwner = this.owner as PlayerEntity
        var originalStack = itemOwner.getStackInHand(Hand.MAIN_HAND)
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

    // ??
    override fun tick() {
        if(this.inGroundTime > 4 && !this.dealtDamage) {
            this.dealtDamage = true
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
    }

    override fun asItemStack(): ItemStack {
        TODO("Not yet implemented")
    }

    override fun getStack(): ItemStack {
        TODO("Not yet implemented")
    }
}