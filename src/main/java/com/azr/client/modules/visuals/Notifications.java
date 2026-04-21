package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayDeque;
import java.util.Deque;

public class Notifications extends Module {

    private static final Deque<long[]> queue = new ArrayDeque<>();
    private static final Deque<String> messages = new ArrayDeque<>();

    public Notifications() { super("Notifications", "Show toast for module toggles", 0, ModuleCategory.VISUALS); }

    public static void push(String msg) {
        synchronized (queue) {
            queue.addLast(new long[]{ System.currentTimeMillis() + 2000 });
            messages.addLast(msg);
            if (queue.size() > 6) { queue.removeFirst(); messages.removeFirst(); }
        }
    }

    @Override
    public void onRender2D(DrawContext ctx) {
        if (!inGame()) return;
        synchronized (queue) {
            long now = System.currentTimeMillis();
            while (!queue.isEmpty() && queue.peekFirst()[0] < now) { queue.removeFirst(); messages.removeFirst(); }
            int sw = ctx.getScaledWindowWidth(), sh = ctx.getScaledWindowHeight();
            int y = sh - 20;
            for (String s : messages) {
                int w = mc.textRenderer.getWidth(s) + 8;
                ctx.fill(sw - w - 4, y - 12, sw - 4, y, 0xC0000000);
                ctx.drawTextWithShadow(mc.textRenderer, s, sw - w, y - 10, 0xFFFFFFFF);
                y -= 14;
            }
        }
    }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get() == null ? null
                : com.azr.client.module.ModuleManager.get().byClass(Notifications.class);
        return m != null && m.isEnabled();
    }
}
