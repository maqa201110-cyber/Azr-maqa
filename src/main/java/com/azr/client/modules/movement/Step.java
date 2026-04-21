package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

public class Step extends Module {
    public final Setting<Double> height = register(new Setting<>("Height", 1.0, 0.6, 3.0));
    private double original = 0.6;

    public Step() { super("Step", "Step up taller blocks", 0, ModuleCategory.MOVEMENT); }

    @Override
    public void onEnable() {
        if (!inGame()) return;
        EntityAttributeInstance a = player().getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (a != null) { original = a.getBaseValue(); a.setBaseValue(height.asDouble()); }
    }

    @Override
    public void onDisable() {
        if (!inGame()) return;
        EntityAttributeInstance a = player().getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (a != null) a.setBaseValue(original);
    }

    @Override
    public void onTick() {
        if (!inGame()) return;
        EntityAttributeInstance a = player().getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (a != null && a.getBaseValue() != height.asDouble()) a.setBaseValue(height.asDouble());
    }
}
