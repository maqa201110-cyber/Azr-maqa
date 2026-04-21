package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class Sprint extends Module {
    public Sprint() { super("Sprint", "Always sprint", 0, ModuleCategory.MOVEMENT); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        if (player().forwardSpeed > 0 && !player().isSneaking() && !player().isUsingItem()) {
            player().setSprinting(true);
        }
    }
}
