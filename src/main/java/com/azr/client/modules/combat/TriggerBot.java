package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TriggerBot extends Module {

    private final Setting<Double> cps = register(new Setting<>("CPS", 12.0, 1.0, 20.0));
    private final Setting<Boolean> playersOnly = register(new Setting<>("PlayersOnly", true));

    private long last;

    public TriggerBot() { super("TriggerBot", "Auto-attack when crosshair is on entity", 0, ModuleCategory.COMBAT); }

    @Override
    public void onTick() {
        if (!inGame() || mc.crosshairTarget == null) return;
        if (mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;
        var hit = (EntityHitResult) mc.crosshairTarget;
        if (!(hit.getEntity() instanceof LivingEntity le)) return;
        if (playersOnly.asBool() && !(le instanceof PlayerEntity)) return;
        long now = System.currentTimeMillis();
        if (now - last < 1000.0 / cps.asDouble()) return;
        last = now;
        mc.interactionManager.attackEntity(player(), le);
        player().swingHand(Hand.MAIN_HAND);
    }
}
