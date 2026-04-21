package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class HitSounds extends Module {
    public HitSounds() { super("HitSounds", "Play sound when you hit an entity", 0, ModuleCategory.CLIENT); }

    public void onHit() {
        if (!isEnabled() || mc.getSoundManager() == null) return;
        SoundEvent ev = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
        mc.getSoundManager().play(PositionedSoundInstance.master(ev, 1.5f, 1.0f));
    }

    public static HitSounds instance() {
        return (HitSounds) com.azr.client.module.ModuleManager.get().byClass(HitSounds.class);
    }
}
