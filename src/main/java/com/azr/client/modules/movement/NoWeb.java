package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class NoWeb extends Module {
    public NoWeb() { super("NoWeb", "Ignore cobweb slowdown", 0, ModuleCategory.MOVEMENT); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        BlockPos pos = player().getBlockPos();
        if (world().getBlockState(pos).isOf(Blocks.COBWEB)) {
            Vec3d v = player().getVelocity();
            player().setVelocity(v.x * 25.0, v.y, v.z * 25.0);
        }
    }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(NoWeb.class);
        return m != null && m.isEnabled();
    }
}
