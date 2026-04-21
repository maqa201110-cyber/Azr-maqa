package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class ClientSpoofer extends Module {
    public ClientSpoofer() { super("ClientSpoofer", "Hide client brand from server (best-effort)", 0, ModuleCategory.CLIENT); }
    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(ClientSpoofer.class);
        return m != null && m.isEnabled();
    }
    public static String brand() { return "vanilla"; }
}
