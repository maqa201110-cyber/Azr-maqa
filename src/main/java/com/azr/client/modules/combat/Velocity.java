package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

/**
 * Velocity — Simp VelocityModule fikrinin 1.21.4 sade hâli.
 *
 * 1.21.4'te knockback iki yoldan gelir:
 *  1) EntityVelocityUpdateS2CPacket  (mob hits, projectile, explosion)
 *  2) ExplosionS2CPacket             (explosions)
 *
 * Bu modül bayrağı set eder; iptali Mixin'ler runtime'da okur:
 *   {@link com.azr.client.mixin.EntityVelocityCancelMixin}
 *   {@link com.azr.client.mixin.ExplosionVelocityCancelMixin}
 *
 * "horizontal" / "vertical" çarpanları yüzde olarak uygulanır
 * (1.0 = tam knockback, 0.0 = tamamen iptal).
 */
public final class Velocity extends Module {

    public double horizontal = 0.0;  // 0 = tamamen iptal
    public double vertical   = 0.0;

    public Velocity() {
        super("Velocity", "Knockback iptali / azaltma.", 0, ModuleCategory.COMBAT);
    }

    /* Mixin'lerin kontrol ettigi statik durum (modul referans cevriminden kacinmak icin). */
    public static boolean isActive() {
        var mm = com.azr.client.AzrClient.getInstance() == null ? null
                : com.azr.client.AzrClient.getInstance().getModuleManager();
        if (mm == null) return false;
        for (Module m : mm.getModules()) {
            if (m instanceof Velocity v && v.isEnabled()) return true;
        }
        return false;
    }

    public static double horizontalMul() {
        var mm = com.azr.client.AzrClient.getInstance().getModuleManager();
        for (Module m : mm.getModules()) if (m instanceof Velocity v) return v.horizontal;
        return 1.0;
    }

    public static double verticalMul() {
        var mm = com.azr.client.AzrClient.getInstance().getModuleManager();
        for (Module m : mm.getModules()) if (m instanceof Velocity v) return v.vertical;
        return 1.0;
    }
}
