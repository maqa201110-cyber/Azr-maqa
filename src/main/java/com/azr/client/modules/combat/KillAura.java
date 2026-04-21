package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/**
 * Kill Aura — Simp KillAuraModule mantığının 1.21.4 Fabric'e taşınmış sade hâli.
 *
 * Davranış:
 *  - "killRange" içindeki en yakın canlı varlığı (oyuncular dahil) hedef alır.
 *  - Kendisini, takım arkadaşlarını, ölüyü, görünmezi atlar.
 *  - Karaktere doğru bakar (yaw/pitch ayarı).
 *  - "attackDelay" ms aralıklarla saldırı (interactionManager.attackEntity + swingHand).
 *
 * Ayarlar şu an public alan; ileride Setting sistemi eklenince oraya bağlanır.
 */
public final class KillAura extends Module {

    public float killRange   = 4.5f;
    public long  attackDelay = 80L;   // ~12 CPS
    public boolean targetPlayersOnly = false;

    private long lastAttack = 0L;

    public KillAura() {
        super("KillAura", "Menzildeki hedeflere otomatik vurur.", 0, ModuleCategory.COMBAT);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || mc.interactionManager == null) return;
        if (!player.isAlive()) return;

        LivingEntity target = findTarget(mc, player);
        if (target == null) return;

        // Bak
        faceEntity(player, target);

        // Vur
        long now = System.currentTimeMillis();
        if (now - lastAttack >= attackDelay) {
            mc.interactionManager.attackEntity(player, target);
            player.swingHand(Hand.MAIN_HAND);
            lastAttack = now;
        }
    }

    private LivingEntity findTarget(MinecraftClient mc, ClientPlayerEntity self) {
        LivingEntity best = null;
        double bestDist = killRange * killRange;
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity le)) continue;
            if (le == self) continue;
            if (!le.isAlive()) continue;
            if (le.isInvisible()) continue;
            if (targetPlayersOnly && !(le instanceof PlayerEntity)) continue;
            double d = self.squaredDistanceTo(le);
            if (d < bestDist) {
                bestDist = d;
                best = le;
            }
        }
        return best;
    }

    private void faceEntity(ClientPlayerEntity player, LivingEntity target) {
        Vec3d eye = player.getCameraPosVec(1.0F);
        Vec3d to = target.getBoundingBox().getCenter();
        double dx = to.x - eye.x;
        double dy = (target.getY() + target.getEyeHeight(target.getPose()) * 0.5) - eye.y;
        double dz = to.z - eye.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horiz));
        player.setYaw(yaw);
        player.setPitch(pitch);
    }
}
