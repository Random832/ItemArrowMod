package random832.itemarrows;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientHelper {
    static Player getPlayer() {
        return Minecraft.getInstance().player;
    }
}
