package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class KillAura extends Module {

    private final Setting<Double> range   = register(new Setting<>("Range", 4.5, 3.0, 6.0));
    private final Setting<Double> cps     = register(new Setting<>("CPS", 12.0, 1.0, 20.0));
    private final Setting<Boolean> rotate = register(new Setting<>("Rotate", true));
    private final Setting<Boolean> players = register(new Setting<>("Players", true));
    private final Setting<Boolean> mobs    = register(new Setting<>("Mobs", true));

    private long lastAttack;

    public KillAura() { super("KillAura", "Auto attack nearby entities", GLFW.GLFW_KEY_R, ModuleCategory.COMBAT); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        ClientPlayerEntity p = player();
        LivingEntity target = findTarget(p);
        if (target == null) return;
        if (rotate.asBool()) lookAt(p, target);
        long now = System.currentTimeMillis();
        long delay = (long) (1000.0 / cps.asDouble());
        if (now - lastAttack < delay) return;
        lastAttack = now;
        var crit = Criticals.instance();
        if (crit != null) crit.onAttack();
        mc.interactionManager.attackEntity(p, target);
        p.swingHand(Hand.MAIN_HAND);
        var hs = com.azr.client.modules.client.HitSounds.instance();
        if (hs != null) hs.onHit();
        if (!target.isAlive()) {
            var ins = com.azr.client.modules.client.Insults.instance();
            if (ins != null) ins.onKill(target.getName().getString());
        }
    }

    private LivingEntity findTarget(ClientPlayerEntity p) {
        LivingEntity best = null;
        double bestD = range.asDouble() * range.asDouble();
        for (Entity e : world().getEntities()) {
            if (!(e instanceof LivingEntity le)) continue;
            if (le == p || !le.isAlive() || le.isInvisible()) continue;
            if (le instanceof PlayerEntity && !players.asBool()) continue;
            if (!(le instanceof PlayerEntity) && !mobs.asBool()) continue;
            double d = p.squaredDistanceTo(le);
            if (d < bestD) { bestD = d; best = le; }
        }
        return best;
    }

    private void lookAt(ClientPlayerEntity p, LivingEntity t) {
        Vec3d eye = p.getCameraPosVec(1.0f);
        Vec3d tar = t.getCameraPosVec(1.0f);
        double dx = tar.x - eye.x, dy = tar.y - eye.y, dz = tar.z - eye.z;
        double dist = Math.sqrt(dx*dx + dz*dz);
        float yaw   = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        p.setYaw(yaw);
        p.setPitch(pitch);
    }
}
