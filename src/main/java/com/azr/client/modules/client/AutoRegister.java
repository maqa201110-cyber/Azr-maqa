package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;

public class AutoRegister extends Module {
    public final Setting<String> password = register(new Setting<>("Password", "azrclient"));
    public AutoRegister() { super("AutoRegister", "Auto /register and /login on join", 0, ModuleCategory.CLIENT); }

    public void onJoin() {
        if (!isEnabled() || mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().sendChatCommand("register " + password.asString() + " " + password.asString());
        mc.getNetworkHandler().sendChatCommand("login " + password.asString());
    }
}
