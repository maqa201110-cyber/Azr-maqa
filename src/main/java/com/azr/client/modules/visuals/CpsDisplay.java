package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public class CpsDisplay extends Module {
    private static final Deque<Long> left = new ArrayDeque<>();
    private static final Deque<Long> right = new ArrayDeque<>();
    private boolean leftPrev, rightPrev;

    public CpsDisplay() { super("CpsDisplay", "Show clicks per second", 0, ModuleCategory.VISUALS); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        long h = mc.getWindow().getHandle();
        boolean l = GLFW.glfwGetMouseButton(h, GLFW.GLFW_MOUSE_BUTTON_LEFT)  == GLFW.GLFW_PRESS;
        boolean r = GLFW.glfwGetMouseButton(h, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        long now = System.currentTimeMillis();
        if (l && !leftPrev)  left.addLast(now);
        if (r && !rightPrev) right.addLast(now);
        leftPrev = l; rightPrev = r;
        while (!left.isEmpty()  && now - left.peekFirst()  > 1000) left.removeFirst();
        while (!right.isEmpty() && now - right.peekFirst() > 1000) right.removeFirst();
    }

    @Override
    public void onRender2D(DrawContext ctx) {
        if (!inGame()) return;
        String s = "CPS: " + left.size() + " | " + right.size();
        ctx.drawTextWithShadow(mc.textRenderer, s, 4, 24, 0xFFFFFFFF);
    }
}
