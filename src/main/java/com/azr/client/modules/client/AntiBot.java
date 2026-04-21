package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class AntiBot extends Module {
    public AntiBot() { super("AntiBot", "Filter out fake/bot players from targeting", 0, ModuleCategory.CLIENT); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(AntiBot.class);
        return m != null && m.isEnabled();
    }

    public static boolean isBot(Entity e) {
        if (!(e instanceof PlayerEntity p)) return false;
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.getNetworkHandler() == null) return true;
        return mc.getNetworkHandler().getPlayerListEntry(p.getUuid()) == null;
    }
}
