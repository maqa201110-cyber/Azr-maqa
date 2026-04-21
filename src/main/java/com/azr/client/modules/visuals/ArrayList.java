package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayList extends Module {

    private static final int ACCENT = 0xFF7A5CFF;

    public ArrayList() { super("ArrayList", "List of enabled modules on the right", 0, ModuleCategory.VISUALS); }

    @Override
    public void onRender2D(DrawContext ctx) {
        if (!inGame() || ModuleManager.get() == null) return;
        List<Module> active = ModuleManager.get().getModules().stream()
                .filter(Module::isEnabled)
                .filter(m -> !(m instanceof ArrayList))
                .sorted(Comparator.comparingInt(m -> -mc.textRenderer.getWidth(m.getName())))
                .collect(Collectors.toList());

        int y = 4;
        int sw = ctx.getScaledWindowWidth();
        for (Module m : active) {
            int w = mc.textRenderer.getWidth(m.getName());
            ctx.fill(sw - w - 6, y, sw, y + 11, 0x80000000);
            ctx.fill(sw - w - 7, y, sw - w - 6, y + 11, ACCENT);
            ctx.drawTextWithShadow(mc.textRenderer, m.getName(), sw - w - 3, y + 1, 0xFFFFFFFF);
            y += 12;
        }
    }
}
