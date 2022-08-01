package random832.itemarrows.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import random832.itemarrows.blocks.dispenser.AdvancedDispenserMenu;

import java.util.function.Supplier;

public record ServerBoundSetDispenserParametersPacket(int windowId, AdvancedDispenserMenu.ValueType param, float value) {

    public ServerBoundSetDispenserParametersPacket(FriendlyByteBuf packetBuffer) {
        this(packetBuffer.readByte(), packetBuffer.readEnum(AdvancedDispenserMenu.ValueType.class), packetBuffer.readFloat());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            @Nullable ServerPlayer player = ctx.get().getSender();
            if (player.containerMenu instanceof AdvancedDispenserMenu menu && menu.containerId == windowId) {
                menu.handleValueUpdate(param, value);
            }
        });
        //context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandlers.handle(this)));
        context.setPacketHandled(true);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(windowId);
        buf.writeEnum(param);
        buf.writeFloat(value);
    }
}
