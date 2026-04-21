package com.azr.client.mixin;

import com.azr.client.modules.combat.NoHitDelay;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class NoHitDelayMixin {

    @Inject(method = "getAttackCooldownProgress", at = @At("HEAD"), cancellable = true)
    private void azr$noCooldown(float baseTime, CallbackInfoReturnable<Float> cir) {
        if (NoHitDelay.active()) cir.setReturnValue(2.0f);
    }
}
