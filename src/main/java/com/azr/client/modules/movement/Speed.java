package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.util.math.Vec3d;

public class Speed extends Module {
    public final Setting<Double> boost = register(new Setting<>("Boost", 0.3, 0.05, 1.5));

    public Speed() { super("Speed", "Adds extra horizontal velocity", 0, ModuleCategory.MOVEMENT); }

    @Override
    public void onTick() {
        if (!inGame() || player().isSneaking() || !player().isOnGround()) return;
        if (player().forwardSpeed == 0 && player().sidewaysSpeed == 0) return;
        float yaw = (float) Math.toRadians(player().getYaw());
        double mx = -Math.sin(yaw) * boost.asDouble();
        double mz =  Math.cos(yaw) * boost.asDouble();
        Vec3d v = player().getVelocity();
        player().setVelocity(v.x + mx, v.y, v.z + mz);
    }
}
