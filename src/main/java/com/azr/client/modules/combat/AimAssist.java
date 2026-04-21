package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AimAssist extends Module {

    private final Setting<Double> range = register(new Setting<>("Range", 5.0, 1.0, 8.0));
    private final Setting<Double> speed = register(new Setting<>("Speed", 0.5, 0.05, 1.0));
    private final Setting<Boolean> playersOnly = register(new Setting<>("PlayersOnly", true));

    public AimAssist() { super("AimAssist", "Smoothly snap aim toward nearest entity", 0, ModuleCategory.COMBAT); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        ClientPlayerEntity p = player();
        LivingEntity t = nearest(p);
        if (t == null) return;
        Vec3d eye = p.getCameraPosVec(1f);
        Vec3d tar = t.getCameraPosVec(1f);
        double dx = tar.x - eye.x, dy = tar.y - eye.y, dz = tar.z - eye.z;
        double dist = Math.sqrt(dx*dx + dz*dz);
        float wantYaw   = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float wantPitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        float s = speed.asFloat();
        p.setYaw(  lerpAngle(p.getYaw(), wantYaw, s));
        p.setPitch(lerpAngle(p.getPitch(), wantPitch, s));
    }

    private float lerpAngle(float a, float b, float t) {
        float diff = ((b - a) % 360 + 540) % 360 - 180;
        return a + diff * t;
    }

    private LivingEntity nearest(ClientPlayerEntity p) {
        LivingEntity best = null;
        double bestD = range.asDouble() * range.asDouble();
        for (Entity e : world().getEntities()) {
            if (!(e instanceof LivingEntity le) || le == p || !le.isAlive() || le.isInvisible()) continue;
            if (playersOnly.asBool() && !(le instanceof PlayerEntity)) continue;
            double d = p.squaredDistanceTo(le);
            if (d < bestD) { bestD = d; best = le; }
        }
        return best;
    }
}
