package com.azr.client.module;

import com.azr.client.modules.combat.*;
import com.azr.client.modules.movement.*;
import com.azr.client.modules.player.*;
import com.azr.client.modules.visuals.*;
import com.azr.client.modules.client.*;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {

    private static ModuleManager INSTANCE;
    public static ModuleManager get() { return INSTANCE; }

    private final List<Module> modules = new ArrayList<>();
    private final Map<Class<?>, Module> byClass = new HashMap<>();
    private final Map<Integer, Long> lastToggle = new HashMap<>();

    public ModuleManager() { INSTANCE = this; }

    public void registerDefaults() {
        // Combat
        add(new KillAura());
        add(new Velocity());
        add(new AimAssist());
        add(new AutoClicker());
        add(new Criticals());
        add(new Reach());
        add(new TriggerBot());
        add(new NoHitDelay());
        add(new KeepSprint());
        add(new HitboxExpand());
        add(new WTap());
        // Movement
        add(new Sprint());
        add(new Speed());
        add(new Fly());
        add(new NoFall());
        add(new Step());
        add(new NoSlow());
        add(new SafeWalk());
        add(new Spider());
        add(new Jesus());
        add(new NoWeb());
        add(new Phase());
        add(new NoJumpDelay());
        // Player
        add(new AutoArmor());
        add(new Scaffold());
        add(new ChestStealer());
        add(new FastPlace());
        add(new FastUse());
        add(new FastBreak());
        add(new AutoTool());
        add(new AntiVoid());
        add(new InvMove());
        add(new Regen());
        add(new SpinBot());
        // Visuals
        add(new FullBright());
        add(new Watermark());
        add(new com.azr.client.modules.visuals.ArrayList());
        add(new NameTags());
        add(new Trajectories());
        add(new Chams());
        add(new ESP());
        add(new Notifications());
        add(new FpsDisplay());
        add(new CpsDisplay());
        add(new Coords());
        add(new NickHider());
        add(new Glint());
        add(new ChinaHat());
        add(new Capes());
        add(new Greeting());
        // Client
        add(new Timer());
        add(new HitSounds());
        add(new FpsEnhancer());
        add(new AntiBot());
        add(new NoCrash());
        add(new FakeLag());
        add(new AutoPlay());
        add(new ToggleSounds());
        add(new ClientSpoofer());
        add(new DiscordRPC());
        add(new AutoRegister());
        add(new Insults());
        add(new NoRotate());
        add(new Blink());
    }

    private void add(Module m) {
        modules.add(m);
        byClass.put(m.getClass(), m);
    }

    public List<Module> getModules() { return modules; }

    public Module byClass(Class<?> c) { return byClass.get(c); }

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

    public void handleToggleKeys() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getWindow() == null) return;
        long handle = mc.getWindow().getHandle();
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
