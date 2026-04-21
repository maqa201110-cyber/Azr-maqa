package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class Phase extends Module {
    public Phase() { super("Phase", "Disable client-side collision (noclip-style)", 0, ModuleCategory.MOVEMENT); }

    @Override
    public void onEnable()  { if (inGame()) player().noClip = true;  }
    @Override
    public void onDisable() { if (inGame()) player().noClip = false; }

    @Override
    public void onTick() { if (inGame()) player().noClip = true; }
}
