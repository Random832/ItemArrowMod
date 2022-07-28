package random832.itemarrows.dispenser;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import random832.itemarrows.ItemArrowsMod;

@Mod.EventBusSubscriber(modid = ItemArrowsMod.MODID)
public class MagicAdapterEventHandler {
    public static boolean isActive = false;
    public static Vec3 dispenserLocation;
    public static Quaternion rotation;

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent e) {
        if(!isActive) return;
        Entity entity = e.getEntity();
        Vector3f ePos = new Vector3f(entity.position().subtract(dispenserLocation));
        if(ePos.dot(ePos) > 4) return;
        Vector3f eVel = new Vector3f(entity.getDeltaMovement());
        ePos.transform(rotation);
        eVel.transform(rotation);
        entity.setDeltaMovement(new Vec3(eVel));
        entity.setPos(dispenserLocation.add(new Vec3(ePos)));
        if (entity instanceof AbstractHurtingProjectile fireball) {
            Vector3f eAcc = new Vector3f((float) fireball.xPower, (float) fireball.yPower, (float) fireball.zPower);
            eAcc.transform(rotation);
            fireball.xPower = eAcc.x();
            fireball.yPower = eAcc.y();
            fireball.zPower = eAcc.z();
        }
    }
}
