package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Scaffold extends Module {
    public Scaffold() { super("Scaffold", "Auto-place blocks below the player", 0, ModuleCategory.PLAYER); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        int blockSlot = findBlockSlot();
        if (blockSlot == -1) return;
        int prev = player().getInventory().selectedSlot;
        if (player().getInventory().selectedSlot != blockSlot) {
            player().getInventory().selectedSlot = blockSlot;
        }

        BlockPos below = BlockPos.ofFloored(player().getX(), player().getY() - 1, player().getZ());
        if (!world().getBlockState(below).isAir()) { player().getInventory().selectedSlot = prev; return; }

        for (Direction d : Direction.values()) {
            if (d == Direction.UP) continue;
            BlockPos neighbor = below.offset(d);
            BlockState ns = world().getBlockState(neighbor);
            if (ns.isAir()) continue;
            Direction face = d.getOpposite();
            Vec3d hit = Vec3d.ofCenter(neighbor).add(face.getOffsetX() * 0.5, face.getOffsetY() * 0.5, face.getOffsetZ() * 0.5);
            BlockHitResult brt = new BlockHitResult(hit, face, neighbor, false);
            mc.interactionManager.interactBlock(player(), Hand.MAIN_HAND, brt);
            player().swingHand(Hand.MAIN_HAND);
            break;
        }
        player().getInventory().selectedSlot = prev;
    }

    private int findBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack s = player().getInventory().getStack(i);
            if (!s.isEmpty() && s.getItem() instanceof BlockItem) return i;
        }
        return -1;
    }
}
