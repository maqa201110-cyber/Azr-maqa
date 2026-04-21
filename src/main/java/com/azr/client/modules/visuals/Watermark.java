package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.client.gui.DrawContext;

public class Watermark extends Module {
    public Watermark() { super("Watermark", "Show client name on screen", 0, ModuleCategory.VISUALS); }

    @Override
    public void onRender2D(DrawContext ctx) {
        if (!inGame()) return;
        ctx.drawTextWithShadow(mc.textRenderer, "Azr Client 2.x", 4, 4, 0xFFFFFFFF);
    }
}
