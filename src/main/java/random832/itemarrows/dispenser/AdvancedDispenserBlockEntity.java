package random832.itemarrows.dispenser;

import com.mojang.math.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrowsMod;

public class AdvancedDispenserBlockEntity extends BlockEntity implements MenuProvider {

    static final int GUNPOWDER_SLOT = 0;
    public static final int MAX_GUNPOWDER = 1000;
    static final int GUNPOWDER_PER_ITEM = 100;
    public static final int DATA_GUNPOWDER = 0;
    public static final int DATA_YANGLE = 1;
    public static final int DATA_XANGLE = 2;
    public static final int DATA_POWER = 3;
    public static final int DATA_FLAGS = 4;
    public static final int NUM_DATA_VALUES = 5;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case DATA_GUNPOWDER -> gunpowder;
                case DATA_YANGLE -> getYAngleShort();
                case DATA_XANGLE -> getXAngleShort();
                case DATA_POWER -> Math.round(powerSetting * 10000);
                default -> 0;
            };
        }

        private short getYAngleShort() {
            // 180*180 is 32400, fits in a signed short
            return (short) (Math.round(Mth.wrapDegrees(yAngle)) * 180);
        }

        private short getXAngleShort() {
            return (short) (Math.round(Mth.wrapDegrees(xAngle)) * 180);
        }

        @Override
        public void set(int pIndex, int pValue) {
        }

        @Override
        public int getCount() {
            return NUM_DATA_VALUES;
        }
    };

    final DispenserBlockEntity vanillaDispenser;
    private final ItemStackHandler gunpowderInventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(Tags.Items.GUNPOWDER);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot == GUNPOWDER_SLOT && !getStackInSlot(slot).isEmpty())
                tryLoadGunpowder();
        }
    };

    private void tryLoadGunpowder() {
        if (gunpowder > MAX_GUNPOWDER - GUNPOWDER_PER_ITEM) return;
        ItemStack stack = gunpowderInventory.extractItem(GUNPOWDER_SLOT, (MAX_GUNPOWDER - gunpowder) / GUNPOWDER_PER_ITEM, false);
        if (!stack.isEmpty())
            gunpowder += stack.getCount() * GUNPOWDER_PER_ITEM;
    }

    LazyOptional<IItemHandler> lazyOptItemHandler;
    public float xAngle = 0;
    public float yAngle = 0;
    int gunpowder = 0;
    public float powerSetting = 1;

    public AdvancedDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ItemArrowsMod.DISPENSER_BE.get(), pos, state);
        vanillaDispenser = new DispenserBlockEntity(pos, state) {
            @Override
            public void setChanged() {
                super.setChanged();
                AdvancedDispenserBlockEntity.this.setChanged();
            }
        };
        lazyOptItemHandler = LazyOptional.of(() -> new CombinedInvWrapper(gunpowderInventory, new InvWrapper(vanillaDispenser)));
    }

    void setPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            yAngle = Mth.wrapDegrees(player.getYRot() - 180);
            xAngle = -player.getXRot();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, lazyOptItemHandler);
        else return super.getCapability(cap, side);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("xAngle", xAngle);
        tag.putFloat("yAngle", yAngle);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        xAngle = tag.getFloat("xAngle");
        yAngle = tag.getFloat("yAngle");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("dispenser", vanillaDispenser.saveWithoutMetadata());
        pTag.putFloat("xAngle", xAngle);
        pTag.putFloat("yAngle", yAngle);
        pTag.putFloat("power", powerSetting);
        pTag.putInt("gunpowder", gunpowder);
        pTag.put("gunpowderSlot", gunpowderInventory.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        vanillaDispenser.load(pTag.getCompound("dispenser"));
        xAngle = pTag.getFloat("xAngle");
        yAngle = pTag.getFloat("yAngle");
        powerSetting = pTag.getFloat("power");
        gunpowder = pTag.getInt("gunpowder");
        gunpowderInventory.deserializeNBT(pTag.getCompound("gunpowderSlot"));
    }

    Vec3 getAimVector() {
        float rx = xAngle * Constants.DEG_TO_RAD;
        float ry = -yAngle * Constants.DEG_TO_RAD;
        float cy = Mth.cos(ry);
        float sy = Mth.sin(ry);
        float cx = Mth.cos(rx);
        float sx = Mth.sin(rx);
        return new Vec3(sy * cx, -sx, cy * cx);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + ItemArrowsMod.MODID + ".advanced_dispenser");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AdvancedDispenserMenu(pContainerId, pPlayerInventory, this);
    }

    BlockState getLegacyBlockState() {
        Direction direction;
        if (xAngle > 45)
            direction = Direction.DOWN;
        else if (xAngle < -45)
            direction = Direction.UP;
        else
            direction = Direction.fromYRot(yAngle);
        return Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, direction);
    }

    public void updateClientAngles() {
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }


    public void aimAt(Vec3 target) {
        Vec3 origin = Vec3.atCenterOf(worldPosition);
        Vec3 distance = target.subtract(origin);
        // xAngle = -(float) Mth.atan2(distance.y, distance.horizontalDistance()) * Mth.RAD_TO_DEG; // TODO try to adjust pitch for distance
        xAngle = (float) aimX(distance.horizontalDistance(), distance.y, 1.1, 0.05, false);
        yAngle = (float) Mth.atan2(-distance.x, distance.z) * Mth.RAD_TO_DEG;
        updateClientAngles();
        powerSetting = 1;
    }


    double aimX(double x, double y, double v0, double g, boolean high) {
        double root = v0 * v0 * v0 * v0 - g * (g * x * x + 2f * y * v0 * v0);
        if (root >= 0) {
            root = Math.sqrt(root);
            return -Mth.RAD_TO_DEG * Mth.atan2(v0 * v0 + (high ? +root : -root), (g * x));
        } else {
            return -45;
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, lazyOptItemHandler);
        return super.getCapability(cap);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyOptItemHandler.invalidate();
    }
}