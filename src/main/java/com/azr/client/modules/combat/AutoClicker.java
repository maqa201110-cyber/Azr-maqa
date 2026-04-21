package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class AutoClicker extends Module {

    private final Setting<Double> cps   = register(new Setting<>("CPS", 10.0, 1.0, 20.0));
    private final Setting<Boolean> right = register(new Setting<>("RightClick", false));

    private long last;

    public AutoClicker() { super("AutoClicker", "Auto left/right click", 0, ModuleCategory.COMBAT); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        long handle = mc.getWindow().getHandle();
        boolean btn = right.asBool()
                ? GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS
                : GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT)  == GLFW.GLFW_PRESS;
        if (!btn) return;
        long now = System.currentTimeMillis();
        if (now - last < 1000.0 / cps.asDouble()) return;
        last = now;
        if (right.asBool()) {
            KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(mc.options.useKey.getBoundKeyTranslationKey()));
        } else {
            if (mc.crosshairTarget != null) {
                player().swingHand(Hand.MAIN_HAND);
                KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(mc.options.attackKey.getBoundKeyTranslationKey()));
            }
        }
    }
}
