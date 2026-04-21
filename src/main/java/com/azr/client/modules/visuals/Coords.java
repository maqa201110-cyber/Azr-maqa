package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.client.gui.DrawContext;

public class Coords extends Module {
    public Coords() { super("Coords", "Show XYZ coordinates", 0, ModuleCategory.VISUALS); }

    @Override
    public void onRender2D(DrawContext ctx) {
        if (!inGame()) return;
        String s = String.format("%.1f / %.1f / %.1f", player().getX(), player().getY(), player().getZ());
        ctx.drawTextWithShadow(mc.textRenderer, s, 4, 34, 0xFFFFFFFF);
    }
}
