package com.azr.client.mixin;

import com.azr.client.modules.movement.SafeWalk;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class SafeWalkMixin {

    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    private void azr$safeWalk(CallbackInfoReturnable<Boolean> cir) {
        if (SafeWalk.active()) cir.setReturnValue(true);
    }
}
