package com.azr.client.module;

import com.azr.client.modules.combat.KillAura;
import com.azr.client.modules.combat.Velocity;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {

    private final List<Module> modules = new ArrayList<>();
    private final Map<Integer, Long> lastToggle = new java.util.HashMap<>();

    public void registerDefaults() {
        modules.add(new KillAura());
        modules.add(new Velocity());
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        List<Module> out = new ArrayList<>();
        for (Module m : modules) if (m.getCategory() == category) out.add(m);
        return out;
    }

    public Map<ModuleCategory, List<Module>> grouped() {
        Map<ModuleCategory, List<Module>> map = new EnumMap<>(ModuleCategory.class);
        for (ModuleCategory c : ModuleCategory.values()) map.put(c, new ArrayList<>());
        for (Module m : modules) map.get(m.getCategory()).add(m);
        return map;
    }

    public void tick() {
        for (Module m : modules) {
            if (m.isEnabled()) {
                try { m.onTick(); } catch (Throwable t) { t.printStackTrace(); }
            }
        }
    }

    /** Polls the OS keyboard for module hotkeys. Called every client tick from main entrypoint. */
    public void handleToggleKeys() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getWindow() == null) return;
        long handle = mc.getWindow().getHandle();
        // Only allow toggles when no GUI is open (so M for ClickGUI doesn't conflict)
        if (mc.currentScreen != null) return;

        long now = System.currentTimeMillis();
        for (Module m : modules) {
            int k = m.getKey();
            if (k <= 0) continue;
            boolean down = GLFW.glfwGetKey(handle, k) == GLFW.GLFW_PRESS;
            Long last = lastToggle.get(k);
            if (down && (last == null || now - last > 250)) {
                m.toggle();
                lastToggle.put(k, now);
            } else if (!down && last != null) {
                lastToggle.remove(k);
            }
        }
    }
}
