package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class NoFall extends Module {
    public NoFall() { super("NoFall", "Cancel fall damage (mixin sends onGround=true)", 0, ModuleCategory.MOVEMENT); }

    @Override
    public void onTick() {
        if (inGame()) player().fallDistance = 0;
    }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(NoFall.class);
        return m != null && m.isEnabled();
    }
}
