package random832.itemarrows.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Arrays;

public class AdvancedDispenserRenderer implements BlockEntityRenderer<AdvancedDispenserBlockEntity> {
    static final Direction[] BAKED_DIRECTIONS = Arrays.copyOf(Direction.values(), Direction.values().length+1);

    public AdvancedDispenserRenderer(BlockEntityRendererProvider.Context c) {
    }

    @Override
    public void render(AdvancedDispenserBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(.5, .5, .5);
        pPoseStack.mulPose(Vector3f.YN.rotationDegrees(pBlockEntity.yAngle));
        pPoseStack.mulPose(Vector3f.XP.rotationDegrees(pBlockEntity.xAngle));
        pPoseStack.translate(-.5, -.5, -.5);

        //TODO try to render up/down dispenser when elevation angle is > 45?
        Level level = pBlockEntity.getLevel();
        BlockState state = Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.SOUTH);
        BlockPos pos = pBlockEntity.getBlockPos();
        int light = LevelRenderer.getLightColor(level, Blocks.AIR.defaultBlockState(), pos);
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        VertexConsumer vertices = pBufferSource.getBuffer(RenderType.cutout());
        for (Direction direction1 : BAKED_DIRECTIONS)
            for (BakedQuad q : model.getQuads(null, direction1, level.getRandom(), EmptyModelData.INSTANCE))
                vertices.putBulkData(pPoseStack.last(), q, 1, 1, 1, light, pPackedOverlay);

        pPoseStack.popPose();
    }
}
