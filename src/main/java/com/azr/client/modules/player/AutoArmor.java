package com.azr.client.modules.player;

import com.azr.client.Module;
import com.azr.client.ModuleCategory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

/**
 * AutoArmor - 1.8.9 cc.simp portu, Azr Client (Fabric 1.21.4).
 *
 * Notlar (kullanici istegi uzerine):
 *  - "En iyi zirhi sec" mantigi korundu, sadece materyal siralamasi 1.21.4 itemlerine
 *    gore basit bir item-id rank fonksiyonuyla yeniden yazildi (ArmorMaterial yarn API'si
 *    1.21.x icinde sik degisiyor; bu yontem versiyon-dayanikli).
 *  - Azr'in mevcut Module API'sinde BooleanSetting / NumberSetting / Event bus yok,
 *    bu yuzden ayarlar public alan olarak tutuluyor ve mantik onTick() icinde calisiyor.
 *  - KillAura referansi kaldirildi (kullanici istegi).
 *  - inventoryContainer yerine player.playerScreenHandler kullaniliyor.
 *  - Slot indeksleri 1.21.4 PlayerScreenHandler'a uyarlandi:
 *      0       = crafting result
 *      1..4    = crafting grid
 *      5       = HEAD  (helmet)
 *      6       = CHEST
 *      7       = LEGS
 *      8       = FEET  (boots)
 *      9..35   = main inventory
 *      36..44  = hotbar
 *      45      = offhand
 */
public final class AutoArmor extends Module {

    public enum Mode { OPEN, SPOOF }

    // ----- Ayarlar (Setting sistemi yok, public alanlar) -----
    public Mode mode        = Mode.OPEN;
    public boolean stop     = true;     // sadece SPOOF modunda anlamli
    public double startDelay = 150.0;   // ms
    public double speed      = 150.0;   // ms

    // ----- Iç durum -----
    private long startTime  = 0L;
    private long actionTime = 0L;

    public AutoArmor() {
        super("Auto Armor", "Envanterdeki en iyi zirhi otomatik takar.", -1, ModuleCategory.PLAYER);
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        long now = System.currentTimeMillis();

        // OPEN modunda: ekran kapaliyken zamani sifirla, startDelay kadar bekle
        if (mode == Mode.OPEN) {
            if (mc.currentScreen == null) {
                startTime = now;
                return;
            }
            if (now - startTime < startDelay) return;
            // OPEN modunda envanter ekrani acik olmali
            if (!(mc.currentScreen instanceof InventoryScreen)) return;
        }

        if (now - actionTime < speed) return;

        PlayerScreenHandler handler = player.playerScreenHandler;

        // ---- 1) Yanlis/zayif zirhi at ----
        // 5..8 -> HEAD, CHEST, LEGS, FEET
        for (int type = 0; type < 4; type++) {
            int slotId = 5 + type;
            EquipmentSlot armorSlot = armorSlotFor(type);
            Slot slot = handler.getSlot(slotId);
            if (slot.hasStack()) {
                ItemStack equipped = slot.getStack();
                if (!isBestArmor(equipped, armorSlot, handler)) {
                    dropSlot(mc, slotId);
                    actionTime = now;
                    if (speed != 0.0) break;
                }
            }
        }

        // ---- 2) Envanterden en iyi zirhi tak ----
        for (int type = 0; type < 4; type++) {
            if (now - actionTime <= speed) continue;
            EquipmentSlot armorSlot = armorSlotFor(type);
            for (int i = 9; i < 45; i++) {
                Slot slot = handler.getSlot(i);
                if (!slot.hasStack()) continue;
                ItemStack is = slot.getStack();
                if (!(is.getItem() instanceof ArmorItem armor)) continue;
                if (!matchesSlot(armor, armorSlot)) continue;
                if (!isBestArmor(is, armorSlot, handler)) continue;
                if (isBadStack(is)) continue;

                shiftClick(mc, i);
                actionTime = now;
                if (speed != 0.0) break;
            }
        }
    }

    @Override
    public void onDisable() {
        // Ozel temizlik gerekmiyor
    }

    // ============== yardimcilar ==============

    private static EquipmentSlot armorSlotFor(int type) {
        switch (type) {
            case 0:  return EquipmentSlot.HEAD;
            case 1:  return EquipmentSlot.CHEST;
            case 2:  return EquipmentSlot.LEGS;
            default: return EquipmentSlot.FEET;
        }
    }

    /** ArmorItem'in hedef slotuyla eslesip eslesmedigi (1.21.4 yarn isim degisikliklerine karsi guvenli). */
    private static boolean matchesSlot(ArmorItem armor, EquipmentSlot slot) {
        // ArmorItem.getSlotType() bazi yarn surumlerinde kaldirildi; Identifier path uzerinden eslestir.
        Identifier id = Registries.ITEM.getId(armor);
        String path = id.getPath();
        if (path.endsWith("_helmet"))     return slot == EquipmentSlot.HEAD;
        if (path.endsWith("_chestplate")) return slot == EquipmentSlot.CHEST;
        if (path.endsWith("_leggings"))   return slot == EquipmentSlot.LEGS;
        if (path.endsWith("_boots"))      return slot == EquipmentSlot.FEET;
        if (path.equals("turtle_helmet")) return slot == EquipmentSlot.HEAD;
        if (path.equals("elytra"))        return slot == EquipmentSlot.CHEST;
        return false;
    }

    private static boolean isBadStack(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }

    /**
     * Materyal sirasi (yuksek = daha iyi). Versiyon-dayanikli olmasi icin item id'sinden okunur,
     * boylece ArmorMaterial yarn API'si degisse bile derlenir.
     */
    private static int materialRank(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return -1;
        Identifier id = Registries.ITEM.getId(stack.getItem());
        String p = id.getPath();
        if (p.startsWith("netherite_"))               return 6;
        if (p.startsWith("diamond_"))                 return 5;
        if (p.equals("turtle_helmet"))                return 4;
        if (p.startsWith("iron_"))                    return 4;
        if (p.startsWith("chainmail_"))               return 3;
        if (p.startsWith("golden_"))                  return 2;
        if (p.startsWith("leather_"))                 return 1;
        if (p.equals("elytra"))                       return 0;
        return 0;
    }

    /** Verilen stack, envanterdeki ayni slot tipi icin en iyi mi? */
    private static boolean isBestArmor(ItemStack candidate, EquipmentSlot slot, PlayerScreenHandler handler) {
        if (candidate == null || candidate.isEmpty()) return false;
        if (!(candidate.getItem() instanceof ArmorItem cArmor)) return false;
        if (!matchesSlot(cArmor, slot)) return false;

        int candidateRank = materialRank(candidate);

        for (int i = 9; i < 45; i++) {
            ItemStack other = handler.getSlot(i).getStack();
            if (other == candidate || other.isEmpty()) continue;
            if (!(other.getItem() instanceof ArmorItem oArmor)) continue;
            if (!matchesSlot(oArmor, slot)) continue;
            if (materialRank(other) > candidateRank) return false;
        }
        return true;
    }

    // ============== etkilesim ==============

    private void dropSlot(MinecraftClient mc, int slotId) {
        if (mc.interactionManager == null || mc.player == null) return;
        mc.interactionManager.clickSlot(
                mc.player.playerScreenHandler.syncId,
                slotId,
                1, // Q-drop entire stack
                SlotActionType.THROW,
                mc.player);
    }

    private void shiftClick(MinecraftClient mc, int slotId) {
        if (mc.interactionManager == null || mc.player == null) return;
        mc.interactionManager.clickSlot(
                mc.player.playerScreenHandler.syncId,
                slotId,
                0,
                SlotActionType.QUICK_MOVE,
                mc.player);
    }
}
