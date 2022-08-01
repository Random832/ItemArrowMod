package random832.itemarrows.blocks.dispenser;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class AdvancedDispenserRenderer implements BlockEntityRenderer<AdvancedDispenserBlockEntity> {
    private static final float SCALE = (float)Math.sqrt(1/3d);

    public AdvancedDispenserRenderer(BlockEntityRendererProvider.Context c) {
    }
    @Override
    public void render(AdvancedDispenserBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        boolean useVertical = Math.abs(pBlockEntity.xAngle) > 45;

        pPoseStack.pushPose();
        pPoseStack.translate(.5, .5, .5);
        pPoseStack.scale(SCALE, SCALE, SCALE);
        pPoseStack.mulPose(Vector3f.YN.rotationDegrees(pBlockEntity.yAngle));
        pPoseStack.mulPose(Vector3f.XP.rotationDegrees(pBlockEntity.xAngle));
        if (useVertical) {
            pPoseStack.mulPose(Vector3f.XP.rotationDegrees(90));
        }
        pPoseStack.translate(-.5, -.5, -.5);

        BlockState blockState = Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, useVertical ? Direction.UP : Direction.SOUTH);
        renderModelFromBER(blockState, pPoseStack.last(), pBufferSource, pPackedLight, pPackedOverlay);
        pPoseStack.popPose();
    }

    private static void renderModelFromBER(BlockState blockState, PoseStack.Pose pose, MultiBufferSource bufferSource, int light, int overlay) {
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BakedModel model = blockRenderer.getBlockModel(blockState);
        RandomSource random = RandomSource.create();
        random.setSeed(42);
        for (RenderType renderType : model.getRenderTypes(blockState, random, ModelData.EMPTY)) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
            blockRenderer.getModelRenderer().renderModel(pose, vertexConsumer, blockState, model, 1, 1, 1, light, overlay, ModelData.EMPTY, renderType);
        }
    }
}
