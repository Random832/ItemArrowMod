package random832.itemarrows.blocks.crafters;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.ItemArrowsMod;
import random832.itemarrows.items.ItemHelper;

public class CrafterMenu extends AbstractContainerMenu {
    public static final ResourceLocation EMPTY_ARROW_SLOT = new ResourceLocation(ItemArrowsMod.MODID, "item/empty_arrow_slot");
    public static final ResourceLocation EMPTY_ENVELOPE_SLOT = new ResourceLocation(ItemArrowsMod.MODID, "item/empty_envelope_slot");

    public CrafterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory pPlayerInventory, IItemHandler blockInv) {
        super(pMenuType, pContainerId);
        addSlot(new SlotItemHandler(blockInv, 0, 53, 17));
        addSlot(new SlotItemHandler(blockInv, 1, 71, 17) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                if (pMenuType == ItemArrowsMod.ARROW_CRAFTER_MENU.get())
                    return stack.is(ItemArrowsMod.PRECISE_ARROW_ITEM.get());
                else if (pMenuType == ItemArrowsMod.ENVELOPE_CRAFTER_MENU.get())
                    return stack.is(ItemArrowsMod.ENVELOPE_ITEM.get()) && ItemHelper.getContainedItem(stack).isEmpty();
                else return true;
            }

            @Nullable
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                if (pMenuType == ItemArrowsMod.ARROW_CRAFTER_MENU.get())
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_ARROW_SLOT);
                else if (pMenuType == ItemArrowsMod.ENVELOPE_CRAFTER_MENU.get())
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_ENVELOPE_SLOT);
                else return null;
            }
        });
        addSlot(new SlotItemHandler(blockInv, 2, 107, 17) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        for(int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 48 + i * 18));
        for(int j = 0; j < 9; ++j)
            this.addSlot(new Slot(pPlayerInventory, j, 8 + j * 18, 106));
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public static CrafterMenu createArrowMenu(int i, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        return new CrafterMenu(ItemArrowsMod.ARROW_CRAFTER_MENU.get(), i, inventory, new ItemStackHandler(3));
    }

    public static CrafterMenu createEnvelopeMenu(int i, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        return new CrafterMenu(ItemArrowsMod.ARROW_CRAFTER_MENU.get(), i, inventory, new ItemStackHandler(3));
    }
}
