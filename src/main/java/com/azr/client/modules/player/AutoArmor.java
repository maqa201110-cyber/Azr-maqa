package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {
    public final Setting<Double> delay = register(new Setting<>("Delay", 100.0, 0.0, 1000.0));
    private long lastAction;

    public AutoArmor() { super("AutoArmor", "Auto-equip best armor from inventory", 0, ModuleCategory.PLAYER); }

    @Override
    public void onTick() {
        if (!inGame() || mc.currentScreen != null) return;
        long now = System.currentTimeMillis();
        if (now - lastAction < (long) delay.asDouble()) return;

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            int currentVal = armorValue(player().getEquippedStack(slot), slot);
            int bestSlot = -1, bestVal = currentVal;
            for (int i = 9; i < 45; i++) {
                ItemStack s = player().getInventory().getStack(i);
                int v = armorValue(s, slot);
                if (v > bestVal) { bestVal = v; bestSlot = i; }
            }
            if (bestSlot < 0) continue;
            int armorIdx = 5 + (3 - slot.getEntitySlotId());
            int slotId = bestSlot < 9 ? 36 + bestSlot : bestSlot;
            int sync = player().playerScreenHandler.syncId;
            mc.interactionManager.clickSlot(sync, slotId,   0, SlotActionType.PICKUP, player());
            mc.interactionManager.clickSlot(sync, armorIdx, 0, SlotActionType.PICKUP, player());
            if (!player().currentScreenHandler.getCursorStack().isEmpty()) {
                mc.interactionManager.clickSlot(sync, slotId, 0, SlotActionType.PICKUP, player());
            }
            lastAction = now;
            break;
        }
    }

    private int armorValue(ItemStack s, EquipmentSlot slot) {
        if (s.isEmpty() || !(s.getItem() instanceof ArmorItem ai)) return -1;
        if (ai.getSlotType() != slot) return -1;
        return ai.getMaterial().value().getProtection(slot) * 10
                + ai.getMaterial().value().enchantmentValue();
    }
}
