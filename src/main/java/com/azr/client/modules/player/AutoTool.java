package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoTool extends Module {
    public AutoTool() { super("AutoTool", "Auto-switch to best tool when mining", 0, ModuleCategory.PLAYER); }

    @Override
    public void onTick() {
        if (!inGame() || mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult brt = (BlockHitResult) mc.crosshairTarget;
        BlockState state = world().getBlockState(brt.getBlockPos());
        if (state.isAir()) return;

        int best = player().getInventory().selectedSlot;
        float bestSpeed = player().getInventory().getStack(best).getMiningSpeedMultiplier(state);
        for (int i = 0; i < 9; i++) {
            ItemStack s = player().getInventory().getStack(i);
            float sp = s.getMiningSpeedMultiplier(state);
            if (sp > bestSpeed) { bestSpeed = sp; best = i; }
        }
        if (best != player().getInventory().selectedSlot) player().getInventory().selectedSlot = best;
    }
}
