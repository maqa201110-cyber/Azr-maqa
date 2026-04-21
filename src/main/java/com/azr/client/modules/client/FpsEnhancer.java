package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class FpsEnhancer extends Module {
    public FpsEnhancer() { super("FpsEnhancer", "Reduce particle/render distance for FPS", 0, ModuleCategory.CLIENT); }

    private int origRender, origSim;

    @Override
    public void onEnable() {
        if (mc.options == null) return;
        origRender = mc.options.getViewDistance().getValue();
        origSim    = mc.options.getSimulationDistance().getValue();
        mc.options.getViewDistance().setValue(6);
        mc.options.getSimulationDistance().setValue(6);
    }

    @Override
    public void onDisable() {
        if (mc.options == null) return;
        mc.options.getViewDistance().setValue(origRender);
        mc.options.getSimulationDistance().setValue(origSim);
    }
}
