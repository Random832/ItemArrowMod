package random832.itemarrows.blocks.dispenser;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.*;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.MathHelper;
import random832.itemarrows.capability.CapabilityHelper;

public class AdvancedDispenserBlock extends DispenserBlock {

    public AdvancedDispenserBlock(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedDispenserBlockEntity(pos, state);
    }

    final float LB = 8f - 8f * (float) Math.sqrt(0.5);
    final float UB = 8f + 8f * (float) Math.sqrt(0.5);
    final VoxelShape SHAPE = box(LB, LB, LB, UB, UB, UB);


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void dispenseFrom(ServerLevel pLevel, BlockPos pPos) {
        AdvancedDispenserBlockSource blockSource = new AdvancedDispenserBlockSource(pLevel, pPos);
        DispenserBlockEntity dispenserblockentity = blockSource.getEntity();
        int i = dispenserblockentity.getRandomSlot(pLevel.random);
        if (i < 0) {
            pLevel.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pPos, 0);
            pLevel.gameEvent(null, GameEvent.DISPENSE_FAIL, pPos);
        } else {
            ItemStack itemstack = dispenserblockentity.getItem(i);
            DispenseItemBehavior behavior = this.getDispenseMethod(itemstack);
            if (behavior == DispenseItemBehavior.NOOP) return;
            if (behavior instanceof AbstractProjectileDispenseBehavior projectileDispenseBehavior) {
                dispenserblockentity.setItem(i, fireProjectileAdvanced(blockSource, itemstack, projectileDispenseBehavior));
            } else if (behavior.getClass() == DefaultDispenseItemBehavior.class) {
                dispenserblockentity.setItem(i, fireDefaultAdvanced(blockSource, itemstack));
            } else if (itemstack.is(Items.SPLASH_POTION) || itemstack.is(Items.LINGERING_POTION)) {
                dispenserblockentity.setItem(i, fireProjectileAdvanced(blockSource, itemstack, new PotionDispenseItemBehavior()));
            } else {
                dispenserblockentity.setItem(i, magicAdapt(blockSource, itemstack, behavior));
            }
        }
    }

    // rotates any created entity around the center of the dispenser
    private ItemStack magicAdapt(AdvancedDispenserBlockSource blockSource, ItemStack stack, DispenseItemBehavior behavior) {
        Vector3f legacyVec = blockSource.getBlockState().getValue(DispenserBlock.FACING).step();
        Vector3f v = new Vector3f(blockSource.getRealEntity().getAimVector());
        v.cross(legacyVec);
        MagicAdapterEventHandler.rotation = new Quaternion(v, -90, true);
        MagicAdapterEventHandler.dispenserLocation = Vec3.atCenterOf(blockSource.getPos());
        MagicAdapterEventHandler.isActive = true;
        try {
            return behavior.dispense(blockSource, stack);
        } finally {
            MagicAdapterEventHandler.isActive = false;
        }
    }

    private Position getDispensePositionAdvanced(AdvancedDispenserBlockSource blockSource) {
        Vec3 vec = Vec3.atCenterOf(blockSource.getPos()).add(blockSource.getRealEntity().getAimVector().scale(0.7));
        return new PositionImpl(vec.x, vec.y, vec.z);
    }

    private ItemStack fireDefaultAdvanced(AdvancedDispenserBlockSource blockSource, ItemStack stack) {
        Level level = blockSource.getLevel();
        Position position = getDispensePositionAdvanced(blockSource);
        ItemStack split = stack.split(1);
        ItemEntity itemEntity = new ItemEntity(level, position.x(), position.y(), position.z(), split);
        Vec3 vec = blockSource.getRealEntity().getAimVector();
        double inaccuracy = 0.05; // vanilla: 0.0172275D * 6
        double speed = 0.225 + level.random.nextDouble() * 0.5; // vanilla: 0.2+rand*0.1
        final double vx = level.random.triangle(vec.x * speed, inaccuracy);
        final double vy = level.random.triangle(vec.y * speed - 0.1, inaccuracy);
        final double vz = level.random.triangle(vec.z * speed, inaccuracy);
        itemEntity.setDeltaMovement(vx, vy, vz);
        level.addFreshEntity(itemEntity);
        level.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, blockSource.getPos(), 0);
        level.levelEvent(LevelEvent.PARTICLES_SHOOT, blockSource.getPos(), blockSource.getBlockState().getValue(FACING).get3DDataValue());
        return stack;
    }

    private ItemStack fireProjectileAdvanced(AdvancedDispenserBlockSource blockSource, ItemStack stack, AbstractProjectileDispenseBehavior behavior) {
        Level level = ((BlockSource) blockSource).getLevel();
        Position position = getDispensePositionAdvanced(blockSource);
        Vec3 vec = blockSource.getRealEntity().getAimVector();
        Projectile projectile = behavior.getProjectile(level, position, stack);
        // y increment of 0.1 in vanilla dispenser ~ angle of about 5.71 degrees up
        float effectivePowerSetting = blockSource.getRealEntity().consumeGunpowder();
        float power = behavior.getPower() * effectivePowerSetting;
        float uncertainty = behavior.getUncertainty() / 2;
        projectile.shoot(vec.x, vec.y, vec.z, power, uncertainty);
        level.addFreshEntity(projectile);
        stack.shrink(1);
        //TODO animation has to be reworked anyway, also do explosion stuff for supercharged
        level.levelEvent(LevelEvent.SOUND_DISPENSER_PROJECTILE_LAUNCH, blockSource.getPos(), 0);
        level.levelEvent(LevelEvent.PARTICLES_SHOOT, blockSource.getPos(), blockSource.getBlockState().getValue(FACING).get3DDataValue());
        if(effectivePowerSetting > 1) {
            // TODO explosive sound
        }
        return stack;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof AdvancedDispenserBlockEntity be) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, be);
                pPlayer.awardStat(Stats.INSPECT_DISPENSER);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.is(pNewState.getBlock())) return;
        if (pLevel.getBlockEntity(pPos) instanceof AdvancedDispenserBlockEntity be) {
            if (!be.getType().isValid(pNewState)) {
                be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> CapabilityHelper.dropContents(pLevel, pPos, h));
                if (be.gunpowder > 0)
                    Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), new ItemStack(Items.GUNPOWDER, MathHelper.probabilisticDivide(be.gunpowder, 100, pLevel.random)));
                pLevel.removeBlockEntity(pPos);
                //pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
        }
    }
}
