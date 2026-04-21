package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class ToggleSounds extends Module {
    public ToggleSounds() { super("ToggleSounds", "Play sound on module toggle", 0, ModuleCategory.CLIENT); }

    public void play(boolean on) {
        if (!isEnabled() || mc.getSoundManager() == null) return;
        mc.getSoundManager().play(PositionedSoundInstance.master(
                SoundEvents.UI_BUTTON_CLICK.value(),
                on ? 1.2f : 0.8f, 1.0f));
    }

    public static ToggleSounds instance() {
        var m = com.azr.client.module.ModuleManager.get();
        return m == null ? null : (ToggleSounds) m.byClass(ToggleSounds.class);
    }
}
