package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.client.gui.DrawContext;

public class FpsDisplay extends Module {
    public FpsDisplay() { super("FpsDisplay", "Show FPS counter", 0, ModuleCategory.VISUALS); }

    @Override
    public void onRender2D(DrawContext ctx) {
        if (!inGame()) return;
        String s = mc.getCurrentFps() + " FPS";
        ctx.drawTextWithShadow(mc.textRenderer, s, 4, 14, 0xFFFFFFFF);
    }
}
