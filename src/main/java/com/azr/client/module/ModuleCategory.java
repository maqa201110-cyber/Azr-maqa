package com.azr.client.module;

public enum ModuleCategory {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    WORLD("World"),
    VISUALS("Visuals"),
    CLIENT("Client");

    private final String label;
    ModuleCategory(String label) { this.label = label; }
    public String getLabel() { return label; }
    public String getDisplayName() { return label; }
}
