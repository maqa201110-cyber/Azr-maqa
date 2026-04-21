package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Jesus extends Module {
    public Jesus() { super("Jesus", "Walk on water", 0, ModuleCategory.MOVEMENT); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        BlockPos below = BlockPos.ofFloored(player().getX(), player().getY() - 0.1, player().getZ());
        if (world().getFluidState(below).getFluid() == Fluids.WATER && !mc.options.sneakKey.isPressed()) {
            Vec3d v = player().getVelocity();
            if (v.y < 0) player().setVelocity(v.x, 0.0, v.z);
            player().setOnGround(true);
        }
    }
}
