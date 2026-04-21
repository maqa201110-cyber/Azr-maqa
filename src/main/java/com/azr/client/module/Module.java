package com.azr.client.module;

public abstract class Module {

    private final String name;
    private final String description;
    private final ModuleCategory category;
    private int key;
    private boolean enabled;

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

    public final void toggle() {
        setEnabled(!enabled);
    }

    public final void setEnabled(boolean state) {
        if (this.enabled == state) return;
        this.enabled = state;
        if (state) onEnable();
        else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
}
