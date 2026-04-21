package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class InvMove extends Module {
    public InvMove() { super("InvMove", "Allows movement while inventory is open (mixin)", 0, ModuleCategory.PLAYER); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(InvMove.class);
        return m != null && m.isEnabled();
    }
}
