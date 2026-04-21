package com.azr.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final ModuleCategory category;
    private int key;
    private boolean enabled;
    private final List<Setting<?>> settings = new ArrayList<>();

    protected Module(String name, String description, int key, ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.key = key;
        this.category = category;
    }

    public final String getName() { return name; }
    public final String getDescription() { return description; }
    public final ModuleCategory getCategory() { return category; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public boolean isEnabled() { return enabled; }
    public List<Setting<?>> getSettings() { return settings; }

    protected <T> Setting<T> register(Setting<T> s) { settings.add(s); return s; }

    public final void toggle() { setEnabled(!enabled); }

    public final void setEnabled(boolean state) {
        if (this.enabled == state) return;
        this.enabled = state;
        if (state) onEnable();
        else onDisable();
        try {
            var ts = com.azr.client.modules.client.ToggleSounds.instance();
            if (ts != null) ts.play(state);
        } catch (Throwable ignored) {}
        try {
            if (com.azr.client.modules.visuals.Notifications.active()) {
                com.azr.client.modules.visuals.Notifications.push(
                        (state ? "§a+ " : "§c- ") + name);
            }
        } catch (Throwable ignored) {}
    }

    protected ClientPlayerEntity player() { return mc.player; }
    protected ClientWorld world() { return mc.world; }
    protected boolean inGame() { return mc.player != null && mc.world != null; }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onRender2D(net.minecraft.client.gui.DrawContext ctx) {}
}
