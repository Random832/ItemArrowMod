package random832.itemarrows.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import random832.itemarrows.ItemArrowsMod;

public class ModPackets {
    private static final String PROTOCOL_VERSION = "0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ItemArrowsMod.MODID, "channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static int id = 0;

    public static int nextId() {
        return id++;
    }

    public static void registerMessages() {
        INSTANCE.registerMessage(nextId(),
                ServerBoundSetDispenserParametersPacket.class,
                ServerBoundSetDispenserParametersPacket::write,
                ServerBoundSetDispenserParametersPacket::new,
                ServerBoundSetDispenserParametersPacket::handle);
    }
}
