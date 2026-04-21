package com.azr.client.mixin;

import com.azr.client.modules.movement.NoFall;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class NoFallMixin {

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void azr$cancelFall(double fallDistance, float damageMultiplier,
                                net.minecraft.entity.damage.DamageSource source,
                                CallbackInfoReturnable<Boolean> cir) {
        if (NoFall.active()) cir.setReturnValue(false);
    }
}
