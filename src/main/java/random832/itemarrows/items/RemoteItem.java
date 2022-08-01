package random832.itemarrows.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.blocks.dispenser.AdvancedDispenserBlock;
import random832.itemarrows.blocks.dispenser.AdvancedDispenserBlockEntity;

import javax.annotation.Nullable;
import java.util.Optional;

public class RemoteItem extends Item {

    private static final Component MSG_NO_COORDS = Component.translatable("info." + ItemArrowsMod.MODID + ".remote.no_coords");
    private static final String MSG_KEY_NO_DISPENSER = "info." + ItemArrowsMod.MODID + ".remote.no_dispenser";
    private static final String MSG_KEY_SET_DISPENSER = "info." + ItemArrowsMod.MODID + ".remote.set_dispenser";
    private static final String MSG_KEY_SET_TARGET = "info." + ItemArrowsMod.MODID + ".remote.set_target";

    public RemoteItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        BlockPos pos = coords(stack);
        if (pos == null) {
            pPlayer.displayClientMessage(MSG_NO_COORDS, true);
            return InteractionResultHolder.fail(stack);
        }
        if (pLevel.isClientSide) return InteractionResultHolder.consume(stack);
        if (pPlayer.isSecondaryUseActive()) {
            // TODO chunkload while menu is open
            if (pLevel.getBlockEntity(pos) instanceof AdvancedDispenserBlockEntity be) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, be);
                return InteractionResultHolder.consume(stack);
            } else {
                pPlayer.displayClientMessage(Component.translatable(MSG_KEY_NO_DISPENSER, pos), true);
                return InteractionResultHolder.fail(stack);
            }
        } else {
            if (pLevel.getBlockState(pos).getBlock() instanceof AdvancedDispenserBlock block) {
                block.dispenseFrom((ServerLevel) pLevel, pos);
                return InteractionResultHolder.consume(stack);
            } else {
                pPlayer.displayClientMessage(Component.translatable(MSG_KEY_NO_DISPENSER, pos), true);
                return InteractionResultHolder.fail(stack);
            }
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        Level level = pPlayer.getLevel();
        if(level.isClientSide) return InteractionResult.CONSUME;
        return doAimInteract(pPlayer, level, pPlayer.getItemInHand(pUsedHand), new Vec3(pInteractionTarget.getX(), pInteractionTarget.getY(0.5), pInteractionTarget.getZ()));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos clickedPos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        Level level = pContext.getLevel();
        ItemStack stack = pContext.getItemInHand();
        if (level.isClientSide) return InteractionResult.CONSUME;
        if (level.getBlockState(clickedPos).is(ItemArrowsMod.DISPENSER_BLOCK.get())) {
            stack.addTagElement("coords", NbtUtils.writeBlockPos(clickedPos));
            if (player != null)
                player.displayClientMessage(Component.translatable(MSG_KEY_SET_DISPENSER, clickedPos), true);
            return InteractionResult.SUCCESS;
        } else {
            Vec3 target = pContext.getClickLocation();
            return doAimInteract(player, level, stack, target);
        }
    }

    double round(double value) {
        return Math.floor((value+.5)*10)/10d;
    }

    @NotNull
    private InteractionResult doAimInteract(Player player, Level level, ItemStack stack, Vec3 target) {
        BlockPos dispenserPos = coords(stack);
        if (dispenserPos == null) {
            if (player != null) player.displayClientMessage(MSG_NO_COORDS, true);
            return InteractionResult.FAIL;
        } else if (!((level.getBlockEntity(dispenserPos)) instanceof AdvancedDispenserBlockEntity be)) {
            if (player != null)
                player.displayClientMessage(Component.translatable(MSG_KEY_NO_DISPENSER, dispenserPos), true);
            return InteractionResult.FAIL;
        } else {
            be.aimAt(target);
            if (player != null) {
                Vec3 targetRound = new Vec3(round(target.x), round(target.y), round(target.z));
                player.displayClientMessage(Component.translatable(MSG_KEY_SET_TARGET, dispenserPos, targetRound), true);
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Nullable
    BlockPos coords(ItemStack stack) {
        return Optional.ofNullable(stack.getTagElement("coords")).map(NbtUtils::readBlockPos).orElse(null);
    }
}
