package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.util.math.Vec3d;

public class Spider extends Module {
    public Spider() { super("Spider", "Climb walls like a spider", 0, ModuleCategory.MOVEMENT); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        if (player().horizontalCollision) {
            Vec3d v = player().getVelocity();
            player().setVelocity(v.x, 0.2, v.z);
            player().fallDistance = 0;
        }
    }
}
