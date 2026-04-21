package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class ChestStealer extends Module {
    public final Setting<Double> delay = register(new Setting<>("Delay", 50.0, 0.0, 500.0));
    private long last;

    public ChestStealer() { super("ChestStealer", "Auto-take items from chests/containers", 0, ModuleCategory.PLAYER); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        var screen = mc.currentScreen;
        if (!(screen instanceof GenericContainerScreen) && !(screen instanceof ShulkerBoxScreen) && !(screen instanceof HopperScreen)) return;
        long now = System.currentTimeMillis();
        if (now - last < (long) delay.asDouble()) return;
        var sh = player().currentScreenHandler;
        int chestSize = sh.slots.size() - 36;
        for (int i = 0; i < chestSize; i++) {
            ItemStack s = sh.getSlot(i).getStack();
            if (!s.isEmpty()) {
                mc.interactionManager.clickSlot(sh.syncId, i, 0, SlotActionType.QUICK_MOVE, player());
                last = now;
                return;
            }
        }
        if (mc.player != null) mc.player.closeHandledScreen();
    }
}
