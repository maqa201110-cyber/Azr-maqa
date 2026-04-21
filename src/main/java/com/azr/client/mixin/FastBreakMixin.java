package com.azr.client.mixin;

import com.azr.client.modules.player.FastBreak;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockState.class)
public abstract class FastBreakMixin {
    @Inject(method = "calcBlockBreakingDelta", at = @At("RETURN"), cancellable = true)
    private void azr$fastBreak(CallbackInfoReturnable<Float> cir) {
        if (FastBreak.mul() != 1.0) cir.setReturnValue((float) (cir.getReturnValue() * FastBreak.mul()));
    }
}
