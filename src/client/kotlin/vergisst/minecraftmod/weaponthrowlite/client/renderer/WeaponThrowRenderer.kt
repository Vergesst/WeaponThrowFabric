package vergisst.minecraftmod.weaponthrowlite.client.renderer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.FlyingItemEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import org.joml.Quaternionf
import vergisst.minecraftmod.weaponthrowlite.entity.WeaponThrowEntity
import vergisst.minecraftmod.weaponthrowlite.impl.MathConstants.POSITIVE_Y
import vergisst.minecraftmod.weaponthrowlite.impl.MathConstants.POSITIVE_Z

@Environment(EnvType.CLIENT)
class WeaponThrowRenderer(rendererManagerIn: EntityRendererFactory.Context): FlyingItemEntityRenderer<WeaponThrowEntity>(rendererManagerIn) {
    val itemRenderer = rendererManagerIn.itemRenderer

    override fun render(entityIn: WeaponThrowEntity, entityYaw: Float, partialTicks: Float, matrixStackIn: MatrixStack, bufferIn: VertexConsumerProvider, packedLightIn: Int) {
        val degree = entityIn.getRotationAnimation(partialTicks)

        val scale = 0.75F

        matrixStackIn.push()
        matrixStackIn.translate(0F, 0.15F, 0F)
        val interpolatedYaw = MathHelper.lerp(partialTicks, entityIn.prevYaw, entityIn.yaw)
        matrixStackIn.multiply(Quaternionf().fromAxisAngleDeg(POSITIVE_Y, interpolatedYaw - 90.0f))
        matrixStackIn.multiply(Quaternionf().fromAxisAngleDeg(POSITIVE_Z, -degree))
        matrixStackIn.scale(scale, scale, scale)

        val count = entityIn.getItemStack().count


        // ModelTransformation.Mode -> ModelTransformationMode (enum)
        // ItemRenderer.renderItem(ItemStack, ModelTransformation.Mode, int, int, MatrixStack, VortexConsumerProvider, @NullAble)
        //
        this.itemRenderer.renderItem(
            entityIn.getStack(), ModelTransformationMode.FIXED, packedLightIn,
            OverlayTexture.DEFAULT_UV, matrixStackIn, bufferIn, null, entityIn.getId()
        )
        if (count > 32) {
            matrixStackIn.translate(-0.05f, -0.05f, -0.05f)
            this.itemRenderer.renderItem(
                entityIn.getStack(), ModelTransformationMode.FIXED,
                packedLightIn, OverlayTexture.DEFAULT_UV, matrixStackIn, bufferIn, null, entityIn.getId()
            )
        }
        matrixStackIn.pop()
    }
}