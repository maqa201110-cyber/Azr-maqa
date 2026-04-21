package com.azr.client.mixin;

import com.azr.client.modules.movement.NoSlow;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class NoSlowMixin {

    @Inject(method = "isUsingItem", at = @At("HEAD"), cancellable = true)
    private void azr$noSlow(CallbackInfoReturnable<Boolean> cir) {
        if (NoSlow.active()) cir.setReturnValue(false);
    }
}
